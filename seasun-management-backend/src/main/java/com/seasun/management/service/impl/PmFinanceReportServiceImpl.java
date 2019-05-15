package com.seasun.management.service.impl;

import com.seasun.management.exception.ParamException;
import com.seasun.management.exception.UserInvalidOperateException;
import com.seasun.management.helper.ExcelHelper;
import com.seasun.management.mapper.PmAttachmentMapper;
import com.seasun.management.mapper.PmFinanceReportMapper;
import com.seasun.management.mapper.RUserProjectPermMapper;
import com.seasun.management.model.PmAttachment;
import com.seasun.management.model.PmFinanceReport;
import com.seasun.management.model.PmPlan;
import com.seasun.management.model.ProjectRole;
import com.seasun.management.service.PmFinanceReportService;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.RUserProjectPermVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Service
public class PmFinanceReportServiceImpl implements PmFinanceReportService {

    @Autowired
    private PmFinanceReportMapper pmFinanceReportMapper;

    @Autowired
    private PmAttachmentMapper pmAttachmentMapper;

    @Autowired
    private RUserProjectPermMapper rUserProjectPermMapper;

    @Value("${file.sys.prefix}")
    private String filePathPrefix;

    @Value("${backup.excel.url}")
    private String backupExcelUrl;


    @Override
    public Long insertSelective(PmFinanceReport pmFinanceReport) {
        List<PmFinanceReport> reports = pmFinanceReportMapper.selectByCond(pmFinanceReport);
        if (reports.isEmpty()) {
            pmFinanceReport.setStatus(PmFinanceReport.Status.INITIALIZED);
            pmFinanceReport.setCreateTime(new Date());
            pmFinanceReport.setUpdateTime(new Date());
            pmFinanceReportMapper.insertSelective(pmFinanceReport);
        } else {
            PmFinanceReport oldPmFinanceReport = reports.get(0);
            pmFinanceReport.setId(oldPmFinanceReport.getId());
            pmFinanceReport.setUpdateTime(new Date());
            pmFinanceReportMapper.updateByPrimaryKeySelective(pmFinanceReport);
        }

        return pmFinanceReport.getId();
    }

    @Override
    public int updateSelective(Long id, PmFinanceReport pmFinanceReport) {

        pmFinanceReport.setUpdateTime(new Date());
        pmFinanceReport.setId(id);
        return pmFinanceReportMapper.updateByPrimaryKeySelective(pmFinanceReport);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PmAttachment insertAttachment(Long projectId, Integer year, Integer month, MultipartFile file, String size) {
        String fileBackUpUrl;
        PmFinanceReport condition = new PmFinanceReport();
        condition.setProjectId(projectId);
        condition.setYear(year);
        condition.setMonth(month);
        List<PmFinanceReport> reports = pmFinanceReportMapper.selectByCond(condition);
        if (reports.isEmpty()) {
            condition.setStatus(PmFinanceReport.Status.INITIALIZED);
            condition.setCreateTime(new Date());
            condition.setUpdateTime(new Date());
            pmFinanceReportMapper.insertSelective(condition);
        } else {
            PmFinanceReport pmFinanceReport = reports.get(0);
            //如果非未提交状态，判断是否项管，false拦截
            if (!PmFinanceReport.Status.INITIALIZED .equals( pmFinanceReport.getStatus())) {
                List<RUserProjectPermVo> rUserProjectPermVos = rUserProjectPermMapper.selectByUserIdAndProjectRoleId(MyTokenUtils.getCurrentUserId(), ProjectRole.Role.pm_manager_id);
                if (rUserProjectPermVos.isEmpty()) {
                    throw new UserInvalidOperateException("项目已提交或已发布，无法上传附件，请联系项目管理员...");
                }
            }

            List<PmAttachment> attachments = pmAttachmentMapper.selectByPmFinanceReportId(pmFinanceReport.getId());
            if (attachments.size() > 2) {
                throw new UserInvalidOperateException("附件上传数已达到最大值，不可提交...");
            }
            condition.setId(pmFinanceReport.getId());
        }
        try {
            fileBackUpUrl = ExcelHelper.saveExcelBackup(file, filePathPrefix + backupExcelUrl);
        } catch (Exception e) {
            throw new ParamException("文件异常...");
        }

        fileBackUpUrl = fileBackUpUrl.replace(filePathPrefix, "");

        PmAttachment insert = new PmAttachment();
        insert.setName(file.getOriginalFilename());
        insert.setCreateTime(new Date());
        insert.setUrl(fileBackUpUrl);
        insert.setSize(size);
        insert.setPmFinanceReportId(condition.getId());
        pmAttachmentMapper.insertSelective(insert);

        return insert;
    }

    @Override
    public int deletePmAttachmentByPmAttachmentId(Long id) {
        return pmAttachmentMapper.deleteByPrimaryKey(id);
    }
}
