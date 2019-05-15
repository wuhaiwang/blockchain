package com.seasun.management.service.kingsoftLife;

import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.model.KsConference;

public interface KsConferenceService {

    CommonResponse getAllConferences(String date, int floor, int building);

    CommonResponse searchConference(String location, String common);

    CommonResponse reserveConference(KsConference ksConference);

    CommonResponse getConferenceDetail(long roomId, String date);

    CommonResponse conferenceDetail(long id);

    CommonResponse cancelConference(long id);

    CommonResponse myConference(String date, int pageNo, int pageSize);
}
