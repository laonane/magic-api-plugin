package com.magicapi.idea.completion.context;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.magicapi.idea.lang.psi.MSTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 代码补全上下文分析器
 * 分析当前光标位置的语法上下文，为智能补全提供依据
 */
public class CompletionContextAnalyzer {
    
    /**
     * 分析补全上下文
     * @param element 当前PSI元素
     * @return 补全上下文信息
     */
    @NotNull
    public static CompletionContext analyzeContext(@NotNull PsiElement element) {
        // 查找成员访问表达式 (如 db.select)
        PsiElement memberAccess = findMemberAccess(element);
        if (memberAccess != null) {
            return analyzeMemberAccessContext(memberAccess, element);
        }
        
        // 查找函数调用表达式 (如 function())
        PsiElement functionCall = findFunctionCall(element);
        if (functionCall != null) {
            return analyzeFunctionCallContext(functionCall, element);
        }
        
        // 查找变量声明或赋值
        PsiElement varDeclaration = findVariableDeclaration(element);
        if (varDeclaration != null) {
            return analyzeVariableContext(varDeclaration, element);
        }
        
        // 默认为表达式上下文
        return new CompletionContext(CompletionContext.Type.EXPRESSION, null, null);
    }
    
    /**
     * 查找成员访问表达式
     */
    @Nullable
    private static PsiElement findMemberAccess(@NotNull PsiElement element) {
        // 向上查找，寻找成员访问模式：identifier.identifier
        PsiElement current = element;
        while (current != null) {
            // 检查是否为 DOT 后面的 IDENTIFIER
            if (current.getNode().getElementType() == MSTypes.IDENTIFIER) {
                PsiElement prev = current.getPrevSibling();
                if (prev != null && prev.getNode().getElementType() == MSTypes.DOT) {
                    PsiElement qualifier = prev.getPrevSibling();
                    if (qualifier != null && qualifier.getNode().getElementType() == MSTypes.IDENTIFIER) {
                        return current; // 返回成员访问的右侧标识符
                    }
                }
            }
            current = current.getParent();
        }
        return null;
    }
    
    /**
     * 查找函数调用表达式
     */
    @Nullable
    private static PsiElement findFunctionCall(@NotNull PsiElement element) {
        // 查找函数调用模式：identifier(...)
        PsiElement current = element;
        while (current != null) {
            if (current.getNode().getElementType() == MSTypes.LPAREN) {
                PsiElement prev = current.getPrevSibling();
                if (prev != null && prev.getNode().getElementType() == MSTypes.IDENTIFIER) {
                    return prev; // 返回函数名标识符
                }
            }
            current = current.getParent();
        }
        return null;
    }
    
    /**
     * 查找变量声明
     */
    @Nullable
    private static PsiElement findVariableDeclaration(@NotNull PsiElement element) {
        // 查找 var 声明或赋值表达式
        return PsiTreeUtil.getParentOfType(element, 
            com.magicapi.idea.lang.psi.MSVarDeclaration.class);
    }
    
    /**
     * 分析成员访问上下文
     */
    @NotNull
    private static CompletionContext analyzeMemberAccessContext(@NotNull PsiElement memberAccess, 
                                                              @NotNull PsiElement element) {
        // 获取限定符（点号前面的部分）
        PsiElement qualifier = getQualifier(memberAccess);
        if (qualifier != null) {
            String qualifierText = qualifier.getText();
            
            // 推断限定符类型
            String qualifierType = inferQualifierType(qualifier);
            
            return new CompletionContext(
                CompletionContext.Type.MEMBER_ACCESS,
                qualifierText,
                qualifierType
            );
        }
        
        return new CompletionContext(CompletionContext.Type.EXPRESSION, null, null);
    }
    
    /**
     * 分析函数调用上下文
     */
    @NotNull
    private static CompletionContext analyzeFunctionCallContext(@NotNull PsiElement functionCall, 
                                                              @NotNull PsiElement element) {
        String functionName = functionCall.getText();
        
        // 判断是否在参数位置
        boolean inParameters = isInParameterPosition(element);
        
        return new CompletionContext(
            inParameters ? CompletionContext.Type.FUNCTION_PARAMETER : CompletionContext.Type.FUNCTION_CALL,
            functionName,
            null
        );
    }
    
    /**
     * 分析变量上下文
     */
    @NotNull
    private static CompletionContext analyzeVariableContext(@NotNull PsiElement varDeclaration, 
                                                           @NotNull PsiElement element) {
        return new CompletionContext(CompletionContext.Type.VARIABLE_DECLARATION, null, null);
    }
    
    /**
     * 获取成员访问的限定符
     */
    @Nullable
    private static PsiElement getQualifier(@NotNull PsiElement memberAccess) {
        PsiElement dot = memberAccess.getPrevSibling();
        if (dot != null && dot.getNode().getElementType() == MSTypes.DOT) {
            return dot.getPrevSibling();
        }
        return null;
    }
    
    /**
     * 推断限定符类型
     */
    @NotNull
    private static String inferQualifierType(@NotNull PsiElement qualifier) {
        String qualifierText = qualifier.getText();
        
        // 检查是否为内置模块
        if (isBuiltinModule(qualifierText)) {
            return qualifierText; // 内置模块类型就是模块名
        }
        
        // 检查是否为变量引用
        // TODO: 实现变量类型推断
        
        // 检查是否为函数调用结果
        // TODO: 实现函数返回类型推断
        
        return "unknown";
    }
    
    /**
     * 检查是否为内置模块
     */
    private static boolean isBuiltinModule(@NotNull String name) {
        return name.equals("db") || name.equals("http") || name.equals("request") || 
               name.equals("response") || name.equals("env") || name.equals("log");
    }
    
    /**
     * 判断是否在函数参数位置
     */
    private static boolean isInParameterPosition(@NotNull PsiElement element) {
        PsiElement current = element;
        while (current != null) {
            if (current.getNode().getElementType() == MSTypes.LPAREN) {
                return true;
            }
            if (current.getNode().getElementType() == MSTypes.RPAREN) {
                return false;
            }
            current = current.getParent();
        }
        return false;
    }
}