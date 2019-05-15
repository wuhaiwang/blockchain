package com.seasun.management.controller.org;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.OrderCenter;
import com.seasun.management.model.Project;
import com.seasun.management.service.OrgMaxMemberChangeLogService;
import com.seasun.management.service.ProjectService;
import com.seasun.management.vo.MiniProjectVo;
import com.seasun.management.vo.ProjectSimplifyVo;
import com.seasun.management.vo.OrderCenterVo;
import com.seasun.management.vo.ProjectVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    ProjectService projectService;

    @Autowired
    OrgMaxMemberChangeLogService orgMaxMemberChangeLogService;

    @RequestMapping(value = "/apis/auth/projects", method = RequestMethod.GET)
    public ResponseEntity<?> getAllProject() {
        logger.info("begin getAllProject...");
        List<ProjectVo> projects = projectService.getAllProject();
        logger.info("end getAllProject...");
        return ResponseEntity.ok(projects);
    }


    @RequestMapping(value = "/apis/auth/perm-projects", method = RequestMethod.GET)
    public ResponseEntity<?> getAllPermProject() {
        logger.info("begin getAllPermProject...");
        List<MiniProjectVo> projects = projectService.getAllPermProject();
        logger.info("end getAllPermProject...");
        return ResponseEntity.ok(projects);
    }

    /**
     * search project by condition , add your input param as condition changes.
     * Note: make sure your params is marked with (required = false), to avoid mixing other query .
     * // todo: 改为 /apis/auth/project
     *
     * @param status
     * @param financeFlag
     * @param shareFlag
     * @param hrFlag
     * @return
     */
    @RequestMapping(value = "/apis/auth/project/search", method = RequestMethod.GET)
    public ResponseEntity<?> getProjectsByCond(@RequestParam(required = false) String status, @RequestParam(required = false) Boolean financeFlag,
                                               @RequestParam(required = false) Boolean shareFlag, @RequestParam(required = false) Boolean hrFlag) {
        logger.info("begin getProjectsByCond...");
        Project projectCond = new Project();
        projectCond.setStatus(status);
        projectCond.setFinanceFlag(financeFlag);
        projectCond.setShareFlag(shareFlag);
        projectCond.setHrFlag(hrFlag);
        List<Project> projects = projectService.getProjectByCond(projectCond);
        logger.info("end getProjectsByCond...");
        return ResponseEntity.ok(projects);
    }


    @RequestMapping(value = "/apis/auth/project/{projectId}", method = RequestMethod.GET)
    public ResponseEntity<?> getProjectById(@PathVariable Long projectId) {
        logger.info("begin getProjectById...");
        ProjectVo project = projectService.getProjectById(projectId);
        logger.info("end getProjectById...");
        return ResponseEntity.ok(project);
    }

    @RequestMapping(value = "/apis/auth/simple-project", method = RequestMethod.GET)
    public ResponseEntity<?> getSimpleProjects(@RequestParam(required = false) Long companyId) {
        logger.info("begin getSimpleProjects...");
        List<IdNameBaseObject> result = projectService.getSimpleProjects(companyId);
        if(-2L == companyId){//剑心项目
            if(result == null){
                result = new ArrayList<>();
            }
            IdNameBaseObject idNameBaseObject = new IdNameBaseObject();
            idNameBaseObject.setId(-2L);
            idNameBaseObject.setName("剑心");
            result.add(idNameBaseObject);
        }
        logger.info("end getSimpleProjects...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }

    @RequestMapping(value = "/apis/auth/project", method = RequestMethod.POST)
    public ResponseEntity<?> addProject(@RequestBody ProjectVo projectVo) {
        logger.info("begin addProject...");
        projectService.addProject(projectVo);
        logger.info("end addProject...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/project", method = RequestMethod.PUT)
    public ResponseEntity<?> updateProject(@RequestBody ProjectVo projectVo) {
        logger.info("begin updateProject...");
//        orgMaxMemberChangeLogService.addLog(projectVo.getId(),projectVo.getMaxMember(),null,null, MyTokenUtils.getCurrentUserId());
        projectService.updateProject(projectVo);
        logger.info("end updateProject...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/project/{projectId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteProject(@PathVariable Long projectId) {
        logger.info("begin deleteProject...");
        projectService.disableProject(projectId);
        logger.info("end deleteProject...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/project/{projectId}", method = RequestMethod.POST)
    public ResponseEntity<?> activeProject(@PathVariable Long projectId) {
        logger.info("begin activeProject...");
        projectService.activeProject(projectId);
        logger.info("end activeProject...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    //获取所有分摊平台 todo:跟web端确认发现没用到，待确认
    @RequestMapping(value = "/apis/auth/share-plats", method = RequestMethod.GET)
    public ResponseEntity<?> getSharePlats() {
        logger.info("begin getSharePlat...");
        List<ProjectVo> projectVos = projectService.getSharePlats();
        logger.info("end getSharePlat...");
        return ResponseEntity.ok(projectVos);
    }

    @RequestMapping(value = "/apis/auth/project/logo/{projectId}", method = RequestMethod.POST)
    public ResponseEntity<?> importProjectLogo(@RequestParam MultipartFile file, @PathVariable Long projectId) {
        logger.info("begin importProjectLogo...");
        String url = projectService.uploadProjectLogo(file, projectId);
        JSONObject uploadResponse = new JSONObject();
        uploadResponse.put("code", 0);
        uploadResponse.put("url", url);
        logger.info("end importProjectLogo...");
        return ResponseEntity.ok(uploadResponse);
    }

    @RequestMapping(value = "/apis/auth/project-simplify", method = RequestMethod.POST)
    public ResponseEntity<?> simpilfyProject(ProjectSimplifyVo projectSimplifyVo) {
        logger.info("projectSimplifyVo -> {}", projectSimplifyVo);
        projectService.simplifyUpdateProject(projectSimplifyVo);
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/order-centers", method = RequestMethod.GET)
    public ResponseEntity<?> getOrderCenters(@RequestParam(required = false) Boolean usedFlag) {
        logger.info("begin getOrderCenters...");
        List<OrderCenterVo> result = projectService.getOrderCenters(usedFlag);
        logger.info("end getOrderCenters...");
        return ResponseEntity.ok(new CommonAppResponse(0,result));
    }

    @RequestMapping(value = "/apis/auth/order-center", method = RequestMethod.PUT)
    public ResponseEntity<?> updateOrderCenter(@RequestBody OrderCenter orderCenter) {
        logger.info("begin updateOrderCenter...");
        projectService.updateOrderCenter(orderCenter);
        logger.info("end updateOrderCenter...");
        return ResponseEntity.ok(new CommonResponse());
    }
}
