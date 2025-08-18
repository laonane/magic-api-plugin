package com.magicapi.idea.completion.model;

import java.util.Objects;

/**
 * 参数定义
 * 表示API方法的参数信息
 */
public class Parameter {
    private final String name;
    private final String type;
    private final boolean required;
    private final String description;
    private final String defaultValue;

    public Parameter(String name, String type, boolean required, String description, String defaultValue) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public Parameter(String name, String type, boolean required, String description) {
        this(name, type, required, description, null);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isRequired() {
        return required;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * 获取参数的完整描述
     */
    public String getFullDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(": ").append(type);
        
        if (!required) {
            sb.append(" (可选)");
        }
        
        if (defaultValue != null && !defaultValue.isEmpty()) {
            sb.append(" = ").append(defaultValue);
        }
        
        if (description != null && !description.isEmpty()) {
            sb.append(" - ").append(description);
        }
        
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parameter parameter = (Parameter) o;
        return required == parameter.required &&
                Objects.equals(name, parameter.name) &&
                Objects.equals(type, parameter.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, required);
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", required=" + required +
                '}';
    }
}