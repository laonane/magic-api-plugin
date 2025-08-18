package com.magicapi.idea.completion.model;

import java.util.List;
import java.util.Objects;

/**
 * Magic API 模块定义
 * 表示Magic Script中的内置模块，如db、http、request、response等
 */
public class MagicApiModule {
    private final String name;
    private final String description;
    private final List<ApiMethod> methods;
    private final String icon;

    public MagicApiModule(String name, String description, List<ApiMethod> methods, String icon) {
        this.name = name;
        this.description = description;
        this.methods = methods;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<ApiMethod> getMethods() {
        return methods;
    }

    public String getIcon() {
        return icon;
    }

    /**
     * 根据方法名查找API方法
     */
    public ApiMethod findMethod(String methodName) {
        return methods.stream()
                .filter(method -> method.getName().equals(methodName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MagicApiModule that = (MagicApiModule) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "MagicApiModule{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", methods=" + methods.size() +
                '}';
    }
}