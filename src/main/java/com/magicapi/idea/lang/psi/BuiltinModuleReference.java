package com.magicapi.idea.lang.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 内置模块引用接口
 * 专门处理Magic API内置模块的访问
 */
public interface BuiltinModuleReference extends PsiElement {
    
    /**
     * 获取模块名称
     * @return 模块名称，如 "db", "http", "request" 等
     */
    @NotNull
    String getModuleName();
    
    /**
     * 获取方法名称
     * @return 方法名称
     */
    @NotNull
    String getMethodName();
    
    /**
     * 获取模块类型
     * @return 模块类型标识
     */
    @NotNull
    String getModuleType();
    
    /**
     * 解析方法引用
     * @return 解析的方法定义
     */
    @Nullable
    PsiElement resolveMethod();
    
    /**
     * 获取方法的完整签名
     * @return 方法签名
     */
    @Nullable
    String getMethodSignature();
}