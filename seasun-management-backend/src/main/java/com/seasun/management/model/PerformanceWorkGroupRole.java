package com.seasun.management.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.seasun.management.model.PerformanceWorkGroupRole.Role.*;

public class PerformanceWorkGroupRole {

    public interface Role {
        Long performance_data_access_role_id = 1L;
        Long performance_observer_role_id = 2L;
        Long performance_human_access_role_id = 4L;
    }

    public interface Type {
        String dataManager = "dataManager";
        String observer = "observer";
        String humanConfigurator = "humanConfigurator";
    }

    public final static List<Long> roles = Collections.unmodifiableList(new ArrayList<Long>() {{
        add(performance_data_access_role_id);
        add(performance_observer_role_id);
        add(performance_human_access_role_id);
    }});

    private Long id;

    private String name;

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
}