package com.magicapi.idea.lang.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Magic API 引用接口
 * 用于成员访问和方法调用的引用解析
 */
public interface MagicApiReference extends PsiElement, PsiReference {
    
    /**
     * 获取限定符（点号前面的部分）
     * @return 限定符元素
     */
    @Nullable
    PsiElement getQualifier();
    
    /**
     * 获取成员名称
     * @return 成员名称
     */
    @NotNull
    String getMemberName();
    
    /**
     * 获取限定符类型
     * @return 限定符类型
     */
    @Nullable
    String getQualifierType();
    
    /**
     * 判断是否为内置模块访问
     * @return true if accessing builtin module
     */
    boolean isBuiltinModule();
    
    /**
     * 判断是否为扩展方法调用
     * @return true if calling extension method
     */
    boolean isExtensionMethod();
}