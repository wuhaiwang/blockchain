package com.seasun.management.config;

public enum DataSourceType {

    primary("springDataSource", "主库"), second("secondDataSource", "第二从库"), third("thirdDataSource", "第三从库"), fourth("fourthDataSource", "第四从库");

    private String type;

    private String name;

    DataSourceType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }


    public String getName() {
        return name;
    }

}
