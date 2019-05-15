package com.seasun.management.controller.org;


import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.dto.OrgMaxMemberChangeLogDto;
import com.seasun.management.dto.ProjectMaxMemberDto;
import com.seasun.management.service.OrgMaxMemberChangeLogService;
import com.seasun.management.service.ProjectService;
import com.seasun.management.vo.ProjectMaxMemberVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/apis/auth")
public class ProjectMaxMemberChangeLogController {
    @Autowired
    ProjectService projectService;

    @Autowired
    OrgMaxMemberChangeLogService orgMaxMemberChangeLogService;

    private static final Logger logger= LoggerFactory.getLogger(ProjectMaxMemberChangeLogController.class);

    @RequestMapping(value = "/org/max-member-change-log",method = RequestMethod.POST)
    public ResponseEntity<?> addMaxMemberChangeLog(@RequestParam Long projectId, @RequestParam Integer newMaxMember, @RequestParam String reason,
                                                   @RequestParam(required = false) MultipartFile attachment, @RequestParam(required = false) Long operatorId){
        logger.info("begin insert change maxMember log");
        String attachmentUrl = orgMaxMemberChangeLogService.importAttachment(attachment);
        if(attachmentUrl!=null){
            orgMaxMemberChangeLogService.addLog(projectId,newMaxMember,reason,attachmentUrl,operatorId);
        }else{
            logger.info("import attachmentFile fail...");
        }
        logger.info("end insert change maxMember log");
        return ResponseEntity.ok(new CommonResponse(0,"更新日志信息成功"));
    }
    @RequestMapping(value = "/org/max-member-log",method = RequestMethod.GET)
    public ResponseEntity<?> getOrgMaxMemberChangeLog(@RequestParam(required = true)Long projectId){
        logger.info("begin select changeMaxMemberLog date...");
        List<OrgMaxMemberChangeLogDto> result = orgMaxMemberChangeLogService.selectMaxMemberChangeLog(projectId);
        logger.info("end select changeMaxMemberLog date..");
            return ResponseEntity.ok(new CommonResponse(0,"success",result));
    }
    //暂时不用
    @RequestMapping(value="/select-maxMember-change-logs",method = RequestMethod.GET)
    public ResponseEntity<?> getOrgMaxMemberChangeLogs(){
        logger.info("begin select changeMaxMemberLogs date...");
        List<OrgMaxMemberChangeLogDto> result = orgMaxMemberChangeLogService.selectMaxMemberChangeLogs();
        logger.info("end select changeMaxMemberLogs date...");
        if(result.isEmpty()){
            return ResponseEntity.ok(new CommonResponse(-1,"获取日志信息失败"));
        }else{
            return ResponseEntity.ok(new CommonResponse(0,"success",result));
        }
    }

    @RequestMapping(value = "/org/max-member-list",method = RequestMethod.GET)
    public ResponseEntity<?> getProjectMaxMembers(){
        logger.info("begin get projectMaxMembers...");
        List<ProjectMaxMemberDto> projectMaxMemberVos = orgMaxMemberChangeLogService.getProjectMaxMemberVos();
        logger.info("end get projectMaxMembers...");
        if(projectMaxMemberVos.isEmpty()){
            return ResponseEntity.ok(new CommonResponse(-1,"获取平台列表失败"));
        }else{
            return ResponseEntity.ok(new CommonResponse(0,"success",projectMaxMemberVos));
        }
    }
}
