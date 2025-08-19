package com.magicapi.idea.lang.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.Nullable;

/**
 * 变量声明PSI元素接口
 * 表示 Magic Script 中的变量声明语句
 */
public interface MSVarDeclaration extends PsiNamedElement {
    
    /**
     * 获取变量的初始化表达式
     * @return 初始化表达式，如果没有初始化则返回null
     */
    @Nullable
    PsiElement getInitializer();
    
    /**
     * 检查变量是否有初始化值
     * @return true如果变量有初始化值
     */
    boolean hasInitializer();
}