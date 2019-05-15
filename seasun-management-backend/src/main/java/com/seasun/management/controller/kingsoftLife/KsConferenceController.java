package com.seasun.management.controller.kingsoftLife;

import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.model.KsConference;
import com.seasun.management.service.kingsoftLife.KsConferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "apis/auth/app/ks-life/")
public class KsConferenceController {

    private static final Logger logger = LoggerFactory.getLogger(KsConferenceController.class);

    @Autowired
    private KsConferenceService ksConferenceService;

    @RequestMapping(value = "/conferences", method = RequestMethod.GET)
    public ResponseEntity getAllConferenceStatus(@RequestParam String date, @RequestParam Integer floor,
                                                 @RequestParam Integer building) {
        logger.info("begin getAllConferenceStatus ...");
        CommonResponse commonResponse = ksConferenceService.getAllConferences(date, floor, building);
        logger.info("end getAllConferenceStatus ...");
        return ResponseEntity.ok(commonResponse);
    }


    @RequestMapping(value = "/conference", method = RequestMethod.GET)
    public ResponseEntity getConference(@RequestParam(required = false) String location, @RequestParam(required = false) String common) {
        logger.info("begin getConference ...");
        CommonResponse commonResponse = ksConferenceService.searchConference(location, common);
        logger.info("end getConference ...");
        return ResponseEntity.ok(commonResponse);
    }


    @RequestMapping(value = "/conference/{id}", method = RequestMethod.GET)
    public ResponseEntity getConferenceById(@PathVariable Long id) {
        logger.info("begin getConferenceById ...");
        CommonResponse commonResponse = ksConferenceService.conferenceDetail(id);
        logger.info("end getConferenceById ...");
        return ResponseEntity.ok(commonResponse);
    }

    @RequestMapping(value = "/conference-reserve", method = RequestMethod.POST)
    public ResponseEntity reserveConference(@RequestBody KsConference ksConference) {
        logger.info("begin reserveConference ...");
        CommonResponse commonResponse = ksConferenceService.reserveConference(ksConference);
        logger.info("end reserveConference ...");
        return ResponseEntity.ok(commonResponse);
    }

    @RequestMapping(value = "/my-reserved-conferences", method = RequestMethod.GET)
    public ResponseEntity getMyReservation(String date, int pageNo, int pageSize) {
        logger.info("begin reserveConference ...");
        CommonResponse commonResponse = ksConferenceService.myConference(date, pageNo, pageSize);
        logger.info("end reserveConference ...");
        return ResponseEntity.ok(commonResponse);
    }

    @RequestMapping(value = "/conference-cancel/{id}", method = RequestMethod.POST)
    public ResponseEntity cancelReservation(@PathVariable Long id) {
        logger.info("begin cancelReservation ...");
        CommonResponse commonResponse = ksConferenceService.cancelConference(id);
        logger.info("end cancelReservation ...");
        return ResponseEntity.ok(commonResponse);
    }

    @RequestMapping(value = "/conference-detail", method = RequestMethod.GET)
    public ResponseEntity getConferenceDetail(@RequestParam Long roomId, @RequestParam String date) {
        logger.info("begin getConferenceDetail ...");
        CommonResponse commonResponse = ksConferenceService.getConferenceDetail(roomId, date);
        logger.info("end getConferenceDetail...");
        return ResponseEntity.ok(commonResponse);
    }
}
