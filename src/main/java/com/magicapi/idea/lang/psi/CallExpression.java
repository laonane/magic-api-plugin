package com.magicapi.idea.lang.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

/**
 * 函数调用表达式接口
 * 用于处理方法调用和参数信息
 */
public interface CallExpression extends PsiElement {
    
    /**
     * 获取参数列表
     * @return 参数表达式列表
     */
    @NotNull
    List<PsiElement> getArguments();
    
    /**
     * 获取参数数量
     * @return 参数个数
     */
    int getArgumentCount();
    
    /**
     * 获取期望的参数列表（用于智能提示）
     * @return 期望的参数信息
     */
    @Nullable
    List<String> getExpectedParameters();
    
    /**
     * 获取函数名称
     * @return 函数名称
     */
    @Nullable
    String getFunctionName();
    
    /**
     * 判断是否支持链式调用
     * @return true if chainable
     */
    boolean isChainable();
    
    /**
     * 获取返回类型
     * @return 方法返回类型
     */
    @Nullable
    String getReturnType();
}