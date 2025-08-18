package com.magicapi.idea.navigation;

import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.magicapi.idea.lang.psi.MSFunctionDeclaration;
import com.magicapi.idea.lang.psi.MSVarDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Magic Script 查找使用处提供器
 * 实现"Find Usages"功能
 */
public class MagicScriptFindUsagesProvider implements FindUsagesProvider {
    
    @Override
    @Nullable
    public WordsScanner getWordsScanner() {
        // 返回词汇扫描器，用于建立索引
        // 可以使用默认的实现或自定义
        return null; // 使用默认实现
    }
    
    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        // 判断该元素是否支持查找使用处
        return psiElement instanceof PsiNamedElement;
    }
    
    @Override
    @Nullable
    public String getHelpId(@NotNull PsiElement psiElement) {
        // 返回帮助ID
        return null;
    }
    
    @Override
    @NotNull
    public String getType(@NotNull PsiElement element) {
        // 返回元素类型的描述
        if (element instanceof MSFunctionDeclaration) {
            return "function";
        } else if (element instanceof MSVarDeclaration) {
            return "variable";
        } else if (element instanceof PsiNamedElement) {
            return "identifier";
        }
        return "element";
    }
    
    @Override
    @NotNull
    public String getDescriptiveName(@NotNull PsiElement element) {
        // 返回元素的描述性名称
        if (element instanceof PsiNamedElement) {
            String name = ((PsiNamedElement) element).getName();
            return name != null ? name : "unnamed";
        }
        return "unnamed";
    }
    
    @Override
    @NotNull
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        // 返回元素在树中显示的文本
        if (element instanceof MSFunctionDeclaration) {
            MSFunctionDeclaration function = (MSFunctionDeclaration) element;
            String name = function.getName();
            if (name != null) {
                // 显示函数签名
                return "function " + name + "()";
            }
        } else if (element instanceof MSVarDeclaration) {
            MSVarDeclaration var = (MSVarDeclaration) element;
            String name = var.getName();
            if (name != null) {
                return "var " + name;
            }
        } else if (element instanceof PsiNamedElement) {
            String name = ((PsiNamedElement) element).getName();
            if (name != null) {
                return name;
            }
        }
        
        return element.getText();
    }
}