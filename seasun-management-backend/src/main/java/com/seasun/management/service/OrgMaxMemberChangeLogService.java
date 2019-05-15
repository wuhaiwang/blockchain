package com.seasun.management.service;

import com.seasun.management.dto.OrgMaxMemberChangeLogDto;
import com.seasun.management.dto.ProjectMaxMemberDto;
import com.seasun.management.model.OrgMaxMemberChangeLog;
import com.seasun.management.vo.ProjectMaxMemberVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OrgMaxMemberChangeLogService {
    void addlog(OrgMaxMemberChangeLog orgMaxMemberChangeLog);
    String importAttachment(MultipartFile attachment);
    Integer addLog(Long projectId,Integer newMaxMember,String reason,String attachmentUrl,Long operatorId);
    List<OrgMaxMemberChangeLogDto> selectMaxMemberChangeLog(Long projectId);
    List<OrgMaxMemberChangeLogDto> selectMaxMemberChangeLogs();
    List<ProjectMaxMemberDto> getProjectMaxMemberVos();
}
