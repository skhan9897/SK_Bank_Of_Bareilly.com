package com.skbank.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class SystemSetting implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long settingId;
    private String settingKey;
    private String settingValue;
    private String description;
    private Timestamp updatedAt;

    public SystemSetting() {}

    public Long getSettingId() { return settingId; }
    public void setSettingId(Long settingId) { this.settingId = settingId; }

    public String getSettingKey() { return settingKey; }
    public void setSettingKey(String settingKey) { this.settingKey = settingKey; }

    public String getSettingValue() { return settingValue; }
    public void setSettingValue(String settingValue) { this.settingValue = settingValue; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
