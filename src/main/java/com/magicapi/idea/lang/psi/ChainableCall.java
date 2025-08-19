package com.magicapi.idea.lang.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

/**
 * 链式调用接口
 * 用于处理链式方法调用，如 db.cache().select().first()
 */
public interface ChainableCall extends PsiElement {
    
    /**
     * 获取方法名称
     * @return 当前方法名
     */
    @NotNull
    String getMethodName();
    
    /**
     * 获取参数列表
     * @return 参数表达式列表
     */
    @NotNull
    List<PsiElement> getArguments();
    
    /**
     * 获取前一个调用（链式调用中的上一个环节）
     * @return 前一个调用元素
     */
    @Nullable
    PsiElement getPreviousCall();
    
    /**
     * 获取返回类型
     * @return 当前方法的返回类型
     */
    @Nullable
    String getReturnType();
    
    /**
     * 获取完整的调用链
     * @return 从根对象到当前调用的完整链路
     */
    @NotNull
    List<ChainableCall> getCallChain();
    
    /**
     * 判断是否可以继续链式调用
     * @return true if can be chained further
     */
    boolean canContinueChain();
    
    /**
     * 获取链式调用的根对象类型
     * @return 调用链起始对象的类型
     */
    @Nullable
    String getRootObjectType();
}