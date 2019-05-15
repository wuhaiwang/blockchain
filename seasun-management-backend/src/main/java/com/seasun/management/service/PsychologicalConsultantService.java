package com.seasun.management.service;

import com.seasun.management.model.UserPsychologicalConsultation;
import com.seasun.management.vo.UserPsychologicalConsultationVo;

public interface PsychologicalConsultantService {

    UserPsychologicalConsultation getUserPsychologicalConsultationPassword(Integer year, Integer month, Long userId);

    String exportPsychologicalConsultantPassword(Integer year, Integer month);

    UserPsychologicalConsultationVo getUserPsychologicalConsultationPasswordPage(Integer pageSize, Integer currentPage, String searchKey);
}
