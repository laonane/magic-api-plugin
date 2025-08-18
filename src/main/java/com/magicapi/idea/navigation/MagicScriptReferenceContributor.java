package com.magicapi.idea.navigation;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.magicapi.idea.lang.psi.MSTypes;
import com.magicapi.idea.lang.psi.MagicScriptReference;
import org.jetbrains.annotations.NotNull;

/**
 * Magic Script 引用贡献器
 * 为标识符提供引用解析功能
 */
public class MagicScriptReferenceContributor extends PsiReferenceContributor {
    
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        // 为IDENTIFIER提供引用解析
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(MSTypes.IDENTIFIER),
            new MagicScriptReferenceProvider()
        );
    }
    
    private static class MagicScriptReferenceProvider extends PsiReferenceProvider {
        @Override
        @NotNull
        public PsiReference[] getReferencesByElement(@NotNull PsiElement element, 
                                                   @NotNull ProcessingContext context) {
            
            if (element.getNode().getElementType() == MSTypes.IDENTIFIER) {
                String text = element.getText();
                TextRange range = new TextRange(0, text.length());
                
                return new PsiReference[] {
                    new MagicScriptReference(element, range, text)
                };
            }
            
            return PsiReference.EMPTY_ARRAY;
        }
    }
}