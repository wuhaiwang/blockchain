package com.seasun.management.dto;

public class AppInfoDto {
    private String system;

    private String version;

    private String model;

    private String appVersion;

    private String imei;

    private String codePushLabel;

    private String codePushEnvironment;

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getCodePushLabel() {
        return codePushLabel;
    }

    public void setCodePushLabel(String codePushLabel) {
        this.codePushLabel = codePushLabel;
    }

    public String getCodePushEnvironment() {
        return codePushEnvironment;
    }

    public void setCodePushEnvironment(String codePushEnvironment) {
        this.codePushEnvironment = codePushEnvironment;
    }
}
