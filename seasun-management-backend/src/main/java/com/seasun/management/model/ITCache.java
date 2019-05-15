package com.seasun.management.model;

public class ITCache {
    private Long id;

    private String name;

    private String hashKey;

    private String entryKey;

    private String type;

    private String remark;

    public interface HashKey {
        String HRCONTACTROOTTREE = "cacheManager";
    }

    public interface EntryKey {
        String HRCONTACTROOTTREE = "buildHrWorkGroupView";
    }

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

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey == null ? null : hashKey.trim();
    }

    public String getEntryKey() {
        return entryKey;
    }

    public void setEntryKey(String entryKey) {
        this.entryKey = entryKey == null ? null : entryKey.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}