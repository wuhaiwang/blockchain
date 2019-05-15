package com.seasun.management.service;

import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.vo.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PerformanceFMService {

    List<PerformanceFMStageVo> getUserIdentityInfo(Long userId);

    PerformanceFMGroupsVo getFixMemberGroups(String type, Long id);

    ImportFixMemberDataResultVo verifyFixMemberData(MultipartFile file, Long platId);

    void confirmPermanentFixedMember(List<PerformanceFMConfirmVo.PermanentConfirmSolution> removedMembers,
                                     List<PerformanceFMConfirmVo.PermanentConfirmSolution> changedMembers,
                                     Long platId,
                                     String fileName);

    CommonResponse addFixMember(Long platId, Long projectId, Long userId);

    void deleteFixMember(Long platId, Long projectId, Long userId);

    boolean changeProjectConfirmer(Long platId, Long projectId, Long projectManagerId);

    boolean createFixGroup(Long projectId, Long platId, Long projectManagerId);

    List<PerformanceFixPlatVo> getAllPlats(Long projectId);

    void permanentFixMember(Long id);

    String deleteFixGroup(Long platId, Long projectId);

    CommonResponse addFixProjectPerm(Long groupId, Long userId, String type);

    void deleteFixProjectPerm(Long id, String type);

    int modifyPerfSubmit(long platId,boolean nowStatus ,int year ,int month);

}
