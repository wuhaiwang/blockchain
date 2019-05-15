package com.seasun.management.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectRole {


    public interface Role {
        Long project_manager_id = 1L;
        Long admin_id = 2L;
        Long finance_id = 4L;
        Long salary_manager_id = 8L;
        Long grade_manager_id = 9L;
        Long performance_manager_id = 10L;
        Long fm_config_manager_id = 13L;
        Long fm_project_confirmer_id = 14L;
        Long pm_manager_id = 15L;
        Long pm_assistant_id = 16L;
        Long fm_project_secondConfirmer_id = 17L;
        Long wuhan_couple_manager_id = 112L;
        Long beijing_couple_manager_id = 111L;
        Long chengdu_couple_manager_id = 109L;
        Long dalian_couple_manager_id = 110L;
        Long guangzhou_couple_manager_id = 108L;
        Long zhuhai_couple_manager_id = 107L;
        Long plat_share_manager_id = 5L;
        Long plat_share_observer_id = 18L;
    }

    public interface AppRole {
        Long manager_id = 100L;
        Long project_id = 101L;
        Long hr_id = 102L;
        Long finance_id = 103L;
        Long CEO_office_id = 104L;
    }

    //侠侣专员
    public final static List<Long> SEASUN_COUPLE_MANAGER_IDS = new ArrayList<Long>() {{
        add(Role.wuhan_couple_manager_id);
        add(Role.beijing_couple_manager_id);
        add(Role.chengdu_couple_manager_id);
        add(Role.dalian_couple_manager_id);
        add(Role.guangzhou_couple_manager_id);
        add(Role.zhuhai_couple_manager_id);
    }};

    //侠侣专员对应地区
    public final static Map<Long, String> SEASUN_COUPLE_CITY_BY_PROJECT_ROLE_ID_MAP = new HashMap<Long, String>() {{
        put(Role.wuhan_couple_manager_id, Project.City.WUHAN);
        put(Role.beijing_couple_manager_id, Project.City.BEIJING);
        put(Role.chengdu_couple_manager_id, Project.City.CHENGDU);
        put(Role.dalian_couple_manager_id, Project.City.DALIAN);
        put(Role.guangzhou_couple_manager_id, Project.City.GUANGZHOU);
        put(Role.zhuhai_couple_manager_id, Project.City.ZHUHAI);
    }};

    private Long id;

    private String name;

    private Boolean activeFlag;

    private Integer systemFlag;

    //  the following are user defined...


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    public Integer getSystemFlag() {
        return systemFlag;
    }

    public void setSystemFlag(Integer systemFlag) {
        this.systemFlag = systemFlag;
    }
}