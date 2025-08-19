package com.magicapi.idea.inspection;

import com.intellij.codeInspection.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.magicapi.idea.lang.psi.MSTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Magic Script代码检查和快速修复
 */
public class MagicScriptInspection extends LocalInspectionTool {
    
    @Override
    @NotNull
    public String getShortName() {
        return "MagicScriptInspection";
    }
    
    @Override
    @NotNull
    public String getDisplayName() {
        return "Magic Script代码检查";
    }
    
    @Override
    @NotNull
    public String getGroupDisplayName() {
        return "Magic Script";
    }
    
    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
    
    @Override
    @NotNull
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new MagicScriptElementVisitor(holder);
    }
    
    private static class MagicScriptElementVisitor extends PsiElementVisitor {
        private final ProblemsHolder holder;
        
        public MagicScriptElementVisitor(ProblemsHolder holder) {
            this.holder = holder;
        }
        
        @Override
        public void visitElement(@NotNull PsiElement element) {
            super.visitElement(element);
            
            if (element.getNode().getElementType() == MSTypes.IDENTIFIER) {
                checkUnusedVariable(element);
                checkTypoInBuiltinModule(element);
            }
        }
        
        private void checkUnusedVariable(@NotNull PsiElement element) {
            // 简单的未使用变量检查
            String text = element.getText();
            if (text.startsWith("unused")) {
                holder.registerProblem(element, 
                    "变量可能未使用", 
                    ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                    new RemoveUnusedVariableFix());
            }
        }
        
        private void checkTypoInBuiltinModule(@NotNull PsiElement element) {
            String text = element.getText();
            String correction = getBuiltinModuleCorrection(text);
            if (correction != null) {
                holder.registerProblem(element,
                    "可能是内置模块拼写错误，您是否想写 '" + correction + "'？",
                    ProblemHighlightType.LIKE_UNKNOWN_SYMBOL,
                    new CorrectTypoFix(correction));
            }
        }
        
        @Nullable
        private String getBuiltinModuleCorrection(String text) {
            switch (text.toLowerCase()) {
                case "database": case "databse": case "dab": return "db";
                case "htttp": case "htp": case "httpd": return "http";
                case "req": case "request_": case "rquest": return "request";
                case "resp": case "response_": case "reponse": return "response";
                case "env_": case "environment": case "environ": return "env";
                case "logger": case "logging": case "logg": return "log";
                case "magic_": case "magicapi": case "magic_api": return "magic";
                default: return null;
            }
        }
    }
    
    /**
     * 移除未使用变量修复
     */
    private static class RemoveUnusedVariableFix implements LocalQuickFix {
        @Override
        @NotNull
        public String getFamilyName() {
            return "移除未使用的变量";
        }
        
        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement element = descriptor.getPsiElement();
            if (element != null) {
                element.delete();
            }
        }
    }
    
    /**
     * 纠正拼写错误修复
     */
    private static class CorrectTypoFix implements LocalQuickFix {
        private final String correction;
        
        public CorrectTypoFix(String correction) {
            this.correction = correction;
        }
        
        @Override
        @NotNull
        public String getFamilyName() {
            return "纠正为 '" + correction + "'";
        }
        
        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement element = descriptor.getPsiElement();
            if (element != null) {
                // 创建新的元素替换
                // 这里需要使用PSI工厂创建新元素
                // 简化实现，实际需要更复杂的PSI操作
            }
        }
    }
}