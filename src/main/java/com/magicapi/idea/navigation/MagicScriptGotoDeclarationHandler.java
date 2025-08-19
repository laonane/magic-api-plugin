package com.magicapi.idea.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.magicapi.idea.lang.psi.MSFunctionDeclaration;
import com.magicapi.idea.lang.psi.MSVarDeclaration;
import com.magicapi.idea.lang.psi.MSTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Magic Script的Goto Declaration处理器
 * 处理Ctrl+Click或Go to Declaration操作
 */
public class MagicScriptGotoDeclarationHandler implements GotoDeclarationHandler {
    
    @Override
    @Nullable
    public PsiElement[] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, 
                                                int offset, 
                                                Editor editor) {
        if (sourceElement == null || 
            sourceElement.getNode().getElementType() != MSTypes.IDENTIFIER) {
            return PsiElement.EMPTY_ARRAY;
        }
        
        String identifierName = sourceElement.getText();
        
        // 1. 查找函数定义
        PsiElement functionTarget = findFunctionDeclaration(sourceElement, identifierName);
        if (functionTarget != null) {
            return new PsiElement[]{functionTarget};
        }
        
        // 2. 查找变量定义
        PsiElement variableTarget = findVariableDeclaration(sourceElement, identifierName);
        if (variableTarget != null) {
            return new PsiElement[]{variableTarget};
        }
        
        // 3. 处理内置模块和全局函数
        PsiElement builtinTarget = findBuiltinElement(sourceElement, identifierName);
        if (builtinTarget != null) {
            return new PsiElement[]{builtinTarget};
        }
        
        return PsiElement.EMPTY_ARRAY;
    }
    
    @Override
    @Nullable
    public String getActionText(@NotNull DataContext context) {
        return "跳转到定义";
    }
    
    /**
     * 查找函数声明
     */
    @Nullable
    private PsiElement findFunctionDeclaration(@NotNull PsiElement context, @NotNull String functionName) {
        // 在当前文件中查找函数声明
        PsiElement root = context.getContainingFile();
        if (root != null) {
            MSFunctionDeclaration[] functions = PsiTreeUtil.getChildrenOfType(root, MSFunctionDeclaration.class);
            if (functions != null) {
                for (MSFunctionDeclaration function : functions) {
                    if (functionName.equals(function.getName())) {
                        return function;
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * 查找变量声明
     */
    @Nullable
    private PsiElement findVariableDeclaration(@NotNull PsiElement context, @NotNull String variableName) {
        PsiElement current = context;
        
        // 向上遍历PSI树查找变量声明
        while (current != null) {
            // 查找当前作用域中的变量声明
            MSVarDeclaration[] variables = PsiTreeUtil.getChildrenOfType(current, MSVarDeclaration.class);
            if (variables != null) {
                for (MSVarDeclaration variable : variables) {
                    if (variableName.equals(variable.getName()) && 
                        variable.getTextOffset() < context.getTextOffset()) {
                        return variable;
                    }
                }
            }
            
            // 查找兄弟节点中的变量声明
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
        
        return null;
    }
    
    /**
     * 查找内置元素（模块、全局函数等）
     */
    @Nullable
    private PsiElement findBuiltinElement(@NotNull PsiElement context, @NotNull String elementName) {
        // 检查是否为内置模块
        if (isBuiltinModule(elementName)) {
            return new BuiltinElementVirtualPsi(context, elementName, "module");
        }
        
        // 检查是否为全局函数
        if (isBuiltinFunction(elementName)) {
            return new BuiltinElementVirtualPsi(context, elementName, "function");
        }
        
        // 检查是否为内置变量
        if (isBuiltinVariable(elementName)) {
            return new BuiltinElementVirtualPsi(context, elementName, "variable");
        }
        
        return null;
    }
    
    /**
     * 检查是否为内置模块
     */
    private boolean isBuiltinModule(@NotNull String name) {
        String[] builtinModules = {"db", "http", "request", "response", "env", "log", "magic"};
        for (String module : builtinModules) {
            if (module.equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查是否为内置函数
     */
    private boolean isBuiltinFunction(@NotNull String name) {
        // 聚合函数
        String[] aggregateFunctions = {"count", "sum", "max", "min", "avg", "group_concat"};
        for (String func : aggregateFunctions) {
            if (func.equals(name)) return true;
        }
        
        // 数学函数
        String[] mathFunctions = {"round", "floor", "ceil", "abs", "sqrt", "pow", "random"};
        for (String func : mathFunctions) {
            if (func.equals(name)) return true;
        }
        
        // 字符串函数
        String[] stringFunctions = {"uuid", "concat", "format", "md5", "sha1", "base64_encode", "base64_decode"};
        for (String func : stringFunctions) {
            if (func.equals(name)) return true;
        }
        
        // 日期函数
        String[] dateFunctions = {"now", "current_timestamp", "current_date", "date_format", "parse_date"};
        for (String func : dateFunctions) {
            if (func.equals(name)) return true;
        }
        
        // 工具函数
        String[] utilityFunctions = {"is_null", "not_null", "nvl", "coalesce", "typeof", "instanceof"};
        for (String func : utilityFunctions) {
            if (func.equals(name)) return true;
        }
        
        return false;
    }
    
    /**
     * 检查是否为内置变量
     */
    private boolean isBuiltinVariable(@NotNull String name) {
        String[] builtinVars = {"this", "arguments", "__LINE__", "__FILE__", "__METHOD__", "PI", "E"};
        for (String var : builtinVars) {
            if (var.equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 内置元素虚拟PSI
     * 用于表示内置模块、函数、变量等
     */
    private static class BuiltinElementVirtualPsi extends com.intellij.psi.impl.light.LightElement {
        private final String elementName;
        private final String elementType;
        
        public BuiltinElementVirtualPsi(@NotNull PsiElement context, 
                                       @NotNull String elementName, 
                                       @NotNull String elementType) {
            super(context.getManager(), context.getLanguage());
            this.elementName = elementName;
            this.elementType = elementType;
        }
        
        @Override
        public String toString() {
            return "内置" + elementType + ": " + elementName;
        }
        
        @Override
        public String getText() {
            return elementName;
        }
        
        @Override
        public void accept(@NotNull com.intellij.psi.PsiElementVisitor visitor) {
            visitor.visitElement(this);
        }
        
        @Override
        public PsiElement copy() {
            return this;
        }
    }
}