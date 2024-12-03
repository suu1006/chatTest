package com.example.chattest.domain;

import java.util.List;
import java.util.Map;

public class SkillSettingDTO {
    public String version;

    public Map<String, List<Map>> template;

    public void setVersion(String version) {
        this.version = version;
    }

    public void setTemplate(Map<String, List<Map>> template) {
        this.template = template;
    }
}
