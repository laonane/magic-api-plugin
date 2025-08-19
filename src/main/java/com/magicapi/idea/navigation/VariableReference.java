package com.magicapi.idea.navigation;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.magicapi.idea.lang.psi.MSVarDeclaration;
import com.magicapi.idea.lang.psi.MSFunctionDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 变量引用实现
 * 处理变量引用到变量声明的跳转
 */
public class VariableReference extends PsiReferenceBase<PsiElement> {
    
    private final String variableName;
    
    public VariableReference(@NotNull PsiElement element) {
        super(element, new TextRange(0, element.getTextLength()));
        this.variableName = element.getText();
    }
    
    @Override
    @Nullable
    public PsiElement resolve() {
        // 查找变量声明
        return findVariableDeclaration(myElement, variableName);
    }
    
    @Override
    @NotNull
    public Object[] getVariants() {
        // 提供当前作用域内的所有变量名
        List<String> variableNames = new ArrayList<>();
        
        // 查找当前作用域内的变量声明
        collectVariableNames(myElement, variableNames);
        
        return variableNames.toArray();
    }
    
    /**
     * 查找变量声明
     * 按作用域从内到外查找：函数参数 -> 局部变量 -> 全局变量
     */
    @Nullable
    private PsiElement findVariableDeclaration(@NotNull PsiElement context, @NotNull String variableName) {
        PsiElement current = context;
        
        // 向上遍历PSI树，查找变量声明
        while (current != null) {
            // 1. 查找函数参数（如果在函数内部）
            MSFunctionDeclaration function = PsiTreeUtil.getParentOfType(current, MSFunctionDeclaration.class);
            if (function != null) {
                PsiElement parameter = findFunctionParameter(function, variableName);
                if (parameter != null) {
                    return parameter;
                }
            }
            
            // 2. 查找局部变量声明
            List<MSVarDeclaration> localVars = PsiTreeUtil.getChildrenOfTypeAsList(current, MSVarDeclaration.class);
            for (MSVarDeclaration varDecl : localVars) {
                if (variableName.equals(varDecl.getName()) && 
                    varDecl.getTextOffset() < context.getTextOffset()) {
                    return varDecl;
                }
            }
            
            // 3. 查找兄弟节点中的变量声明（同一作用域）
            PsiElement parent = current.getParent();
            if (parent != null) {
                for (PsiElement sibling : parent.getChildren()) {
                    if (sibling instanceof MSVarDeclaration && 
                        sibling.getTextOffset() < context.getTextOffset()) {
                        MSVarDeclaration varDecl = (MSVarDeclaration) sibling;
                        if (variableName.equals(varDecl.getName())) {
                            return varDecl;
                        }
                    }
                }
            }
            
            current = current.getParent();
        }
        
        // 4. 检查是否为内置变量或常量
        if (isBuiltinVariable(variableName)) {
            return createBuiltinVariableElement(variableName);
        }
        
        return null;
    }
    
    /**
     * 查找函数参数
     */
    @Nullable
    private PsiElement findFunctionParameter(@NotNull MSFunctionDeclaration function, @NotNull String parameterName) {
        // 简化实现：检查函数声明文本中是否包含参数名
        String functionText = function.getText();
        if (functionText != null && functionText.contains(parameterName)) {
            // 这里需要更精确的参数解析，现在简化处理
            // 返回函数声明本身作为参数位置的占位符
            return function;
        }
        return null;
    }
    
    /**
     * 收集当前作用域内的变量名
     */
    private void collectVariableNames(@NotNull PsiElement context, @NotNull List<String> names) {
        PsiElement current = context;
        
        while (current != null) {
            // 收集变量声明
            List<MSVarDeclaration> varDeclarations = PsiTreeUtil.getChildrenOfTypeAsList(current, MSVarDeclaration.class);
            for (MSVarDeclaration varDecl : varDeclarations) {
                String name = varDecl.getName();
                if (name != null && !names.contains(name) && 
                    varDecl.getTextOffset() < context.getTextOffset()) {
                    names.add(name);
                }
            }
            
            // 收集函数参数
            MSFunctionDeclaration function = PsiTreeUtil.getParentOfType(current, MSFunctionDeclaration.class);
            if (function != null) {
                // 简化实现：添加常见的参数名
                if (!names.contains("request")) names.add("request");
                if (!names.contains("response")) names.add("response");
                if (!names.contains("params")) names.add("params");
            }
            
            current = current.getParent();
        }
        
        // 添加内置变量
        addBuiltinVariables(names);
    }
    
    /**
     * 添加内置变量
     */
    private void addBuiltinVariables(@NotNull List<String> names) {
        String[] builtinVars = {
            "this", "arguments", "__LINE__", "__FILE__", "__METHOD__",
            "PI", "E", "db", "http", "request", "response", "env", "log", "magic"
        };
        
        for (String var : builtinVars) {
            if (!names.contains(var)) {
                names.add(var);
            }
        }
    }
    
    /**
     * 检查是否为内置变量
     */
    private boolean isBuiltinVariable(@NotNull String variableName) {
        String[] builtinVars = {
            "this", "arguments", "__LINE__", "__FILE__", "__METHOD__",
            "PI", "E", "db", "http", "request", "response", "env", "log", "magic"
        };
        
        for (String var : builtinVars) {
            if (var.equals(variableName)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 创建内置变量的虚拟元素
     */
    @Nullable
    private PsiElement createBuiltinVariableElement(@NotNull String variableName) {
        // 这里可以创建一个虚拟的PSI元素来表示内置变量
        // 现在简化处理，返回null
        return null;
    }
}