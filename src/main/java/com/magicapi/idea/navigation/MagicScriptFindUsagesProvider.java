package com.magicapi.idea.navigation;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.TokenSet;
import com.magicapi.idea.lang.lexer.MagicScriptLexer;
import com.magicapi.idea.lang.psi.MSFunctionDeclaration;
import com.magicapi.idea.lang.psi.MSVarDeclaration;
import com.magicapi.idea.lang.psi.MSTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Magic Script查找用法提供器
 * 支持查找函数、变量等的所有引用位置
 */
public class MagicScriptFindUsagesProvider implements FindUsagesProvider {
    
    @Override
    @Nullable
    public WordsScanner getWordsScanner() {
        // 暂时返回null，使用默认的词汇扫描器
        return null;
    }
    
    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        // 支持查找函数声明和变量声明的用法
        return psiElement instanceof MSFunctionDeclaration ||
               psiElement instanceof MSVarDeclaration ||
               psiElement instanceof PsiNamedElement;
    }
    
    @Override
    @Nullable
    public String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }
    
    @Override
    @NotNull
    public String getType(@NotNull PsiElement element) {
        if (element instanceof MSFunctionDeclaration) {
            return "函数";
        } else if (element instanceof MSVarDeclaration) {
            return "变量";
        } else if (element.getNode().getElementType() == MSTypes.IDENTIFIER) {
            // 根据上下文判断类型
            PsiElement parent = element.getParent();
            if (parent instanceof MSFunctionDeclaration) {
                return "函数";
            } else if (parent instanceof MSVarDeclaration) {
                return "变量";
            } else {
                return "标识符";
            }
        }
        return "元素";
    }
    
    @Override
    @NotNull
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof PsiNamedElement) {
            String name = ((PsiNamedElement) element).getName();
            if (name != null) {
                return name;
            }
        }
        
        if (element instanceof MSFunctionDeclaration) {
            MSFunctionDeclaration function = (MSFunctionDeclaration) element;
            String name = function.getName();
            if (name != null) {
                return name + "()";
            }
        } else if (element instanceof MSVarDeclaration) {
            MSVarDeclaration variable = (MSVarDeclaration) element;
            String name = variable.getName();
            if (name != null) {
                return name;
            }
        }
        
        return element.getText();
    }
    
    @Override
    @NotNull
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        if (element instanceof MSFunctionDeclaration) {
            MSFunctionDeclaration function = (MSFunctionDeclaration) element;
            String name = function.getName();
            if (name != null) {
                if (useFullName) {
                    // 显示完整的函数签名
                    return "function " + name + "(" + getFunctionParameters(function) + ")";
                } else {
                    return name + "()";
                }
            }
        } else if (element instanceof MSVarDeclaration) {
            MSVarDeclaration variable = (MSVarDeclaration) element;
            String name = variable.getName();
            if (name != null) {
                if (useFullName) {
                    return "var " + name;
                } else {
                    return name;
                }
            }
        }
        
        return element.getText();
    }
    
    /**
     * 获取函数参数列表字符串
     */
    @NotNull
    private String getFunctionParameters(@NotNull MSFunctionDeclaration function) {
        // 简化实现：从函数文本中提取参数
        String functionText = function.getText();
        int startParen = functionText.indexOf('(');
        int endParen = functionText.indexOf(')', startParen);
        
        if (startParen != -1 && endParen != -1 && endParen > startParen) {
            return functionText.substring(startParen + 1, endParen);
        }
        
        return "";
    }
}