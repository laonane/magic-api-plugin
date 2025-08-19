package com.magicapi.idea.lang.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 表示具有类型信息的PSI元素
 * 支持类型推断和链式调用
 */
public interface TypeAware extends PsiElement {
    
    /**
     * 获取表达式的类型
     * @return 表达式类型，如 "String", "Integer", "List<Map<String,Object>>" 等
     */
    @Nullable
    String getExpressionType();
    
    /**
     * 推断表达式类型
     * @return 推断出的类型
     */
    @Nullable
    String inferType();
    
    /**
     * 判断是否可以进行链式调用
     * @return true if this expression can be chained
     */
    boolean canChain();
    
    /**
     * 获取目标对象的类型（用于扩展方法推断）
     * @return 目标对象类型
     */
    @Nullable
    String getTargetType();
}