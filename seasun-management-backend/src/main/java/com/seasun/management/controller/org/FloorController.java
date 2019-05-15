package com.seasun.management.controller.org;

import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.model.Floor;
import com.seasun.management.service.FloorService;
import com.seasun.management.vo.FloorMemberRequestVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apis/auth")
public class FloorController {

    @Autowired
    FloorService floorService;

    @RequestMapping(value = "/floors", method = RequestMethod.GET)
    ResponseEntity<?> findAllFloors () {
        return ResponseEntity.ok(new CommonAppResponse(0, floorService.selectAll()));
    }

    @RequestMapping(value = "/floors/{id}", method = RequestMethod.GET)
    ResponseEntity<?> findFloorById (@PathVariable Long id) {
        return ResponseEntity.ok(new CommonAppResponse(0, floorService.findOne(id)));
    }

    @RequestMapping(value = "/floors/{id}", method = RequestMethod.PUT)
    ResponseEntity<?> update (@PathVariable Long id, @RequestBody Floor floor) {
        floorService.update(id, floor);
        return ResponseEntity.ok(new CommonAppResponse(0, "success"));
    }

    @RequestMapping(value = "/floors/{floorId}/members-check/{userId}", method = RequestMethod.GET)
    ResponseEntity<?> checkMemberExistsInFloor (@PathVariable Long floorId, @PathVariable Long userId) {
        return ResponseEntity.ok(new CommonAppResponse(0, floorService.checkMemberExistsInFloor(floorId, userId)));
    }

    @RequestMapping(value = "/floors/members", method = RequestMethod.POST)
    ResponseEntity<?> insertMemberToFloor (@RequestBody FloorMemberRequestVo vo) {
        floorService.updateMemberToFloor(vo);
        return ResponseEntity.ok(new CommonAppResponse(0, "success"));
    }

    @RequestMapping(value = "/floors/members/export/{floorId}", method = RequestMethod.POST)
    ResponseEntity<?> exportFloorMember (@PathVariable Long floorId) throws IllegalAccessException {
        return ResponseEntity.ok(new CommonAppResponse(0, floorService.exportFloorMember(floorId)));
    }

}
