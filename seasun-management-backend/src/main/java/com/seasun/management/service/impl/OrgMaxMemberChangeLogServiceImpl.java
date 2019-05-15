package com.seasun.management.service.impl;

import com.seasun.management.dto.OrgMaxMemberChangeLogDto;
import com.seasun.management.dto.ProjectMaxMemberDto;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.OrgMaxMemberChangeLogMapper;
import com.seasun.management.mapper.ProjectMapper;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.OrgMaxMemberChangeLog;
import com.seasun.management.model.Project;
import com.seasun.management.service.OrgMaxMemberChangeLogService;
import com.seasun.management.util.MyDateUtils;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.ProjectMaxMemberVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.sql.Timestamp;
import java.util.*;

import static org.apache.tomcat.jni.Time.now;

@Service
public class OrgMaxMemberChangeLogServiceImpl implements OrgMaxMemberChangeLogService {

    @Value("${file.sys.prefix}")
    private String filePathPrefix;

    @Value("${backup.excel.url}")
    private String backUpExcelUrl;

    @Autowired
    OrgMaxMemberChangeLogMapper orgMaxMemberChangeLogMapper;

    @Autowired
    ProjectMapper projectMapper;

    private static final Logger logger = LoggerFactory.getLogger(OrgMaxMemberChangeLogServiceImpl.class);

    @Override
    public void addlog(OrgMaxMemberChangeLog orgMaxMemberChangeLog) {
        orgMaxMemberChangeLogMapper.insert(orgMaxMemberChangeLog);
    }

    @Override
    public String importAttachment(MultipartFile attachment) {
        if (attachment == null || attachment.isEmpty()) {
            return " ";
        } else {
            try {
                File savePath = new File(filePathPrefix + backUpExcelUrl);
                if (!savePath.exists()) {
                    savePath.mkdirs();
                }
                attachment.transferTo(new File(savePath + File.separator + attachment.getOriginalFilename()));
                String attachmentUrl = backUpExcelUrl + File.separator + attachment.getOriginalFilename();
                return attachmentUrl;
            } catch (Exception e) {
                e.printStackTrace();
                throw new ParamException("文件保存失败");
            }
        }
    }

    @Override
    public Integer addLog(Long projectId, Integer newMaxMember, String reason, String attachmentUrl, Long operatorId) {
        Project project = projectMapper.getProjectDateByProjectId(projectId);
        if (!project.getActiveFlag() && !project.getName().endsWith("-废弃")) {
            project.setName(project.getName() + "-废弃");
        } else if (project.getActiveFlag() && project.getName().endsWith("-废弃")) {
            project.setName(project.getName().substring(0, project.getName().length() - 3));
        }
        OrgMaxMemberChangeLog orgMaxMemberChangeLog = new OrgMaxMemberChangeLog();
        orgMaxMemberChangeLog.setProjectId(projectId);
        orgMaxMemberChangeLog.setNewMaxMember(newMaxMember);
        orgMaxMemberChangeLog.setReason(reason);
        orgMaxMemberChangeLog.setAttachmentUrl(attachmentUrl);
        orgMaxMemberChangeLog.setOperatorId(operatorId == null ? MyTokenUtils.getCurrentUserId() : operatorId);
        orgMaxMemberChangeLog.setCreateTime(new Date());

        Project pro = projectMapper.selectByPrimaryKey(projectId);
        orgMaxMemberChangeLog.setOldMaxMember(pro.getMaxMember());

        int insert = orgMaxMemberChangeLogMapper.insert(orgMaxMemberChangeLog);
        //如果是人力传过来的数据则不要更新project表
        if (reason != null) {
            projectMapper.updateMaxMemberByProjectId(projectId, newMaxMember);
        }
        return insert;
    }

    @Override
    public List<OrgMaxMemberChangeLogDto> selectMaxMemberChangeLog(Long projectId) {
        List<OrgMaxMemberChangeLogDto> orgMaxMemberChangeLogDtos = orgMaxMemberChangeLogMapper.getMaxMemberChangeLogByProjectId(projectId);
        for (OrgMaxMemberChangeLogDto orgMaxMemberChangeLogDto : orgMaxMemberChangeLogDtos) {
            String attachmentUrl = orgMaxMemberChangeLogDto.getAttachmentUrl();
            String name = attachmentUrl.substring(attachmentUrl.lastIndexOf(File.separator) + 1);
            orgMaxMemberChangeLogDto.setAttachmentName(name);
        }
        return orgMaxMemberChangeLogDtos;
    }

    @Override
    public List<OrgMaxMemberChangeLogDto> selectMaxMemberChangeLogs() {
        List<OrgMaxMemberChangeLogDto> list = orgMaxMemberChangeLogMapper.getMaxMemberChangeLogsByProjectId();
        return list;
    }

    @Override
    public List<ProjectMaxMemberDto> getProjectMaxMemberVos() {
        List<ProjectMaxMemberDto> projectMaxMemberVos = projectMapper.getProjectMaxMemberVos();

        projectMaxMemberVos.stream().forEach(projectMaxMemberVo -> {

            String managerName = projectMaxMemberVo.getManagerName();
            String orderStr    = projectMaxMemberVo.getOrderStr();
            List<IdNameBaseObject> managers = new ArrayList<>();
            List<ProjectMaxMemberDto.OrderCode> orders = new ArrayList<>();
            StringBuilder tempManagerName = new StringBuilder();
            String  newManagerName = null;
            /**
             * mysql 查询格式 managerName -> id:name,id;name
             *                orderStr    -> code;city, code:city
             * */

            if (!StringUtils.isBlank(managerName)) {
                String strs[] = managerName.split(",");
                Arrays.stream(strs).forEach(str -> {
                    String s[] = str.split(":");
                    if (s.length==2) {
                        IdNameBaseObject idNameBaseObject = new IdNameBaseObject();
                        idNameBaseObject.setName(s[1]);
                        idNameBaseObject.setId(Long.valueOf(s[0]));
                        managers.add (idNameBaseObject);
                        tempManagerName.append(s[1]).append(",");
                    }
                });

                if (tempManagerName.length()>0) {
                    tempManagerName.deleteCharAt(tempManagerName.length()-1);
                    newManagerName = tempManagerName.toString();
                }


            }

            if (!StringUtils.isBlank(orderStr)) {
                String strs[] = orderStr.split(",");
                Arrays.stream(strs).forEach(str -> {
                    String s[] = str.split(":");
                    if (s.length==2) {
                        ProjectMaxMemberDto.OrderCode orderCode = new ProjectMaxMemberDto.OrderCode();
                        orderCode.setCode(s[0]);
                        orderCode.setCity(s[1].trim().equals("") ? null : s[1].trim());
                        orders.add (orderCode);
                    }
                });
            }

            projectMaxMemberVo.setManagerName(newManagerName);
            projectMaxMemberVo.setManagers(managers);
            projectMaxMemberVo.setOrders(orders);
        });

        return projectMaxMemberVos;
    }

}
