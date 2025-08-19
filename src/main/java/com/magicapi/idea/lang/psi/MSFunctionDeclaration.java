package com.magicapi.idea.lang.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 函数声明PSI元素接口
 * 表示 Magic Script 中的函数声明语句
 */
public interface MSFunctionDeclaration extends PsiNamedElement {
    
    /**
     * 获取函数参数列表
     * @return 参数列表
     */
    @NotNull
    List<PsiElement> getParameters();
    
    /**
     * 获取函数体
     * @return 函数体代码块
     */
    @Nullable
    PsiElement getBody();
    
    /**
     * 获取参数数量
     * @return 参数数量
     */
    int getParameterCount();
    
    /**
     * 检查函数是否有参数
     * @return true如果函数有参数
     */
    boolean hasParameters();
}