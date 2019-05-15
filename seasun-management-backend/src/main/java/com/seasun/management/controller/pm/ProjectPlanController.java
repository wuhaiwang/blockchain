package com.seasun.management.controller.pm;

import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.service.ProjectPlanService;
import com.seasun.management.vo.PMilestoneRequestDto;
import com.seasun.management.vo.WebAnnualPlanVo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class ProjectPlanController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProjectPlanController.class);

    @Autowired
    private ProjectPlanService projectPlanService;

    @RequestMapping(value = "/apis/auth/pm-milestone", method = RequestMethod.GET)
    public ResponseEntity<?> getAllPMileStones() {
        logger.info("begin getAllPMileStones...");
        WebAnnualPlanVo result = projectPlanService.getWebAnnualPlan();
        logger.info("end getAllPMileStones...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }

    @RequestMapping(value = "/apis/auth/pm-milestone/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePMilestone(@PathVariable Long id) {
        logger.info("begin deletePMilestone...");
        projectPlanService.deletePMilestone(id);
        logger.info("end deletePMilestone...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/pm-milestone-publish", method = RequestMethod.POST)
    public ResponseEntity<?> releasePMilestone(@RequestBody Integer releaseYear) {
        logger.info("begin releasePMilestone...");
        projectPlanService.releasePMilestone(releaseYear);
        logger.info("end releasePMilestone...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/pm-milestone", method = RequestMethod.POST)
    public ResponseEntity<?> addPMilestone(@RequestBody PMilestoneRequestDto pMilestone) {
        logger.info("begin addPMilestone...");
        long id = projectPlanService.addPMilestone(pMilestone);
        logger.info("end addPMilestone...");
        return ResponseEntity.ok(new CommonAppResponse(0, id));
    }

    @RequestMapping(value = "/apis/auth/pm-milestone/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePMilestone(@RequestBody PMilestoneRequestDto pMilestone) {
        logger.info("begin updatePMilestone...");
        long id = projectPlanService.updatePMilestone(pMilestone);
        logger.info("end updatePMilestone...");
        return ResponseEntity.ok(new CommonAppResponse(0, id));
    }

}
