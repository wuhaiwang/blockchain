package com.seasun.management.controller.org;

import com.seasun.management.constant.ErrorCode;
import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.controller.response.AddRecordResponse;
import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.model.CostCenter;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.Subcompany;
import com.seasun.management.service.SubCompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/apis/auth")
public class SubcompanyController {

    private static final Logger logger = LoggerFactory.getLogger(SubcompanyController.class);

    @Autowired
    private SubCompanyService subCompanyService;

    @RequestMapping(value = "/subcompanys", method = RequestMethod.GET)
    public ResponseEntity<?> getSubcompanys() {
        logger.info("begin getSubcompanys...");
        List<Subcompany> result = subCompanyService.getSubcompanys();
        logger.info("end getSubcompanys...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }

    @RequestMapping(value = "/subcompany", method = RequestMethod.PUT)
    public ResponseEntity<?> updateSubcompany(@RequestBody Subcompany subcompany) {
        logger.info("begin updateSubcompany...");
        subCompanyService.updateSubcompany(subcompany);
        logger.info("end updateSubcompany...");
        return ResponseEntity.ok(new CommonResponse());
    }

    @RequestMapping(value = "/subcompany", method = RequestMethod.POST)
    public ResponseEntity<?> addSubcompany(@RequestBody Subcompany subcompany) {
        logger.info("begin addSubcompany...");
        subCompanyService.addSubcompany(subcompany);
        logger.info("end addSubcompany...");
        if (subcompany.getId() != null) {
            return ResponseEntity.ok(new AddRecordResponse(subcompany.getId()));
        }
        return ResponseEntity.ok(new CommonResponse(ErrorCode.PARAM_ERROR, ErrorMessage.PARAM_ERROR_MESSAGE));
    }

    @RequestMapping(value = "/subcompany", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteSubcompany(@RequestBody Subcompany subcompany) {
        logger.info("begin deleteSubcompany...");
        subCompanyService.deleteSubcompanyById(subcompany.getId());
        logger.info("end deleteSubcompany...");
        return ResponseEntity.ok(new CommonResponse());
    }

    @RequestMapping(value = "/cost-centers", method = RequestMethod.GET)
    public ResponseEntity<?> getCostCenters() {
        logger.info("begin getCostCenters...");
        List<CostCenter> result = subCompanyService.getCostCenters();
        logger.info("end getCostCenters...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }

    @RequestMapping(value = "/cost-center", method = RequestMethod.POST)
    public ResponseEntity<?> addCostCenters(@RequestBody CostCenter costCenter) {
        logger.info("begin addCostCenters...");
        subCompanyService.addCostCenters(costCenter);
        logger.info("end addCostCenters...");
        if (costCenter.getId() != null) {
            return ResponseEntity.ok(new AddRecordResponse(costCenter.getId()));
        }
        return ResponseEntity.ok(new CommonResponse(ErrorCode.PARAM_ERROR, ErrorMessage.PARAM_ERROR_MESSAGE));
    }

    @RequestMapping(value = "/cost-center", method = RequestMethod.PUT)
    public ResponseEntity<?> updateCostCenters(@RequestBody CostCenter costCenter) {
        logger.info("begin updateCostCenters...");
        subCompanyService.updateCostCenters(costCenter);
        logger.info("end updateCostCenters...");
        return ResponseEntity.ok(new CommonResponse());
    }

    @RequestMapping(value = "/simple-subcompany", method = RequestMethod.GET)
    public ResponseEntity<?> getSimpleSubcompanys() {
        logger.info("begin getsimpleSubcompanys...");
        List<IdNameBaseObject> result = subCompanyService.getSimpleSubcompanys();
        if (result == null) {
            result = new ArrayList<>();
        }
        IdNameBaseObject idNameBaseObject = new IdNameBaseObject();
        idNameBaseObject.setId(-2L);
        idNameBaseObject.setName("珠海剑心互动娱乐有限公司");
        result.add(idNameBaseObject);
        logger.info("end getSubcompanys...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }
}
