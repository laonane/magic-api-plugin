package com.magicapi.idea.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.rename.RenameInputValidator;
import com.intellij.util.ProcessingContext;
import com.magicapi.idea.lang.psi.MSFunctionDeclaration;
import com.magicapi.idea.lang.psi.MSVarDeclaration;
import org.jetbrains.annotations.NotNull;

/**
 * Magic Script 重命名输入验证器
 */
public class MagicScriptRenameInputValidator implements RenameInputValidator {
    
    @NotNull
    @Override
    public ElementPattern<? extends PsiElement> getPattern() {
        return PlatformPatterns.psiElement().andOr(
            PlatformPatterns.psiElement(MSFunctionDeclaration.class),
            PlatformPatterns.psiElement(MSVarDeclaration.class)
        );
    }
    
    @Override
    public boolean isInputValid(@NotNull String newName, @NotNull PsiElement element, @NotNull ProcessingContext context) {
        // 检查新名称是否符合 Magic Script 标识符规则
        if (newName.isEmpty()) {
            return false;
        }
        
        // 必须以字母或下划线开头
        if (!Character.isLetter(newName.charAt(0)) && newName.charAt(0) != '_') {
            return false;
        }
        
        // 只能包含字母、数字、下划线
        for (int i = 1; i < newName.length(); i++) {
            char c = newName.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_') {
                return false;
            }
        }
        
        // 不能是关键字
        return !isReservedKeyword(newName);
    }
    
    private boolean isReservedKeyword(String name) {
        switch (name) {
            case "var":
            case "function":
            case "return":
            case "if":
            case "else":
            case "for":
            case "while":
            case "try":
            case "catch":
            case "import":
            case "export":
            case "true":
            case "false":
            case "null":
            case "undefined":
                return true;
            default:
                return false;
        }
    }
    
    public String getErrorMessage(@NotNull String newName, @NotNull Project project) {
        if (newName.isEmpty()) {
            return "名称不能为空";
        }
        
        if (!Character.isLetter(newName.charAt(0)) && newName.charAt(0) != '_') {
            return "名称必须以字母或下划线开头";
        }
        
        for (int i = 1; i < newName.length(); i++) {
            char c = newName.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_') {
                return "名称只能包含字母、数字和下划线";
            }
        }
        
        if (isReservedKeyword(newName)) {
            return "'" + newName + "' 是保留关键字，不能用作标识符";
        }
        
        return null;
    }
}