package com.magicapi.idea.completion.model;

import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * API方法定义
 * 表示Magic API模块中的具体方法
 */
public class ApiMethod {
    private final String name;
    private final String description;
    private final List<Parameter> parameters;
    private final String returnType;
    private final String returnDescription;
    private final String example;
    private final boolean chainable;

    public ApiMethod(String name, String description, List<Parameter> parameters, 
                    String returnType, String returnDescription, String example, boolean chainable) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
        this.returnType = returnType;
        this.returnDescription = returnDescription;
        this.example = example;
        this.chainable = chainable;
    }
    
    // 简化构造函数，用于临时测试
    public ApiMethod(String name, String returnType, String description) {
        this(name, description, Collections.emptyList(), returnType, "", "", false);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getReturnDescription() {
        return returnDescription;
    }

    public String getExample() {
        return example;
    }

    public boolean isChainable() {
        return chainable;
    }

    /**
     * 获取方法签名字符串
     */
    public String getSignature() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("(");
        
        for (int i = 0; i < parameters.size(); i++) {
            Parameter param = parameters.get(i);
            if (i > 0) sb.append(", ");
            sb.append(param.getType()).append(" ").append(param.getName());
            if (!param.isRequired()) {
                sb.append("?");
            }
        }
        
        sb.append(")");
        if (returnType != null && !returnType.isEmpty()) {
            sb.append(": ").append(returnType);
        }
        return sb.toString();
    }

    /**
     * 获取参数提示字符串
     */
    public String getParameterHint() {
        if (parameters.isEmpty()) {
            return "()";
        }
        
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < parameters.size(); i++) {
            Parameter param = parameters.get(i);
            if (i > 0) sb.append(", ");
            sb.append(param.getName());
            if (!param.isRequired()) {
                sb.append("?");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiMethod apiMethod = (ApiMethod) o;
        return Objects.equals(name, apiMethod.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "ApiMethod{" +
                "name='" + name + '\'' +
                ", parameters=" + parameters.size() +
                ", returnType='" + returnType + '\'' +
                '}';
    }
}