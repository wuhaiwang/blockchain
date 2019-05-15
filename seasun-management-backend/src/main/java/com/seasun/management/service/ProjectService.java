package com.seasun.management.service;

import com.seasun.management.dto.SubProjectInfo;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.OrderCenter;
import com.seasun.management.model.Project;
import com.seasun.management.vo.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectService {

    List<ProjectVo> getAllProject();


    List<MiniProjectVo> getAllPermProject();

    /**
     * List<Project> getAllFnShareProject();
     * List<Project> getAllFnProject();
     * List<Project> getAllDevelopingFnProject();
     * <p>
     * Note: all search api above should be merged into the following interface:
     **/
    List<Project> getProjectByCond(Project projectCond);


    ProjectVo getProjectById(Long projectId);

    void addProject(ProjectVo projectVo);

    void updateProject(ProjectVo projectVo);

    void disableProject(Long projectId);

    void activeProject(Long projectId);

    List<ProjectVo> getSharePlats();

    String uploadProjectLogo(MultipartFile file, Long projectId);

    SubProjectInfo getSubProjectById(Long projectId);

    /**
     * 查询IT系统除了平台外的项目,外包映射使用
     * @return
     */
    public List<ProjectVo> selectItProject(String name);

    List<IdNameBaseObject> getSimpleProjects(Long companyId);

    void simplifyUpdateProject (ProjectSimplifyVo projectSimplifyVo);

    List<OrderCenterVo> getOrderCenters(Boolean usedFlag);

    void updateOrderCenter(OrderCenter orderCenter);

}
