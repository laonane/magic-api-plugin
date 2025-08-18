package com.magicapi.idea.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import com.magicapi.idea.lang.psi.MSTypes;
import com.magicapi.idea.icons.MagicScriptIcons;
import org.jetbrains.annotations.NotNull;

public class MagicScriptCompletionContributor extends CompletionContributor {
    
    // Magic Script关键字
    private static final String[] KEYWORDS = {
        "var", "function", "return", "if", "else", "for", "while", 
        "do", "break", "continue", "try", "catch", "finally", "throw",
        "import", "export", "true", "false", "null", "undefined"
    };
    
    // 内置模块
    private static final String[] BUILTIN_MODULES = {
        "db", "http", "request", "response", "env", "log"
    };
    
    public MagicScriptCompletionContributor() {
        // 关键字补全
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement(MSTypes.IDENTIFIER),
               new KeywordCompletionProvider());
               
        // 内置模块补全
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement(MSTypes.IDENTIFIER),
               new BuiltinModuleCompletionProvider());
    }
    
    private static class KeywordCompletionProvider extends CompletionProvider<CompletionParameters> {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                    @NotNull ProcessingContext context,
                                    @NotNull CompletionResultSet result) {
            for (String keyword : KEYWORDS) {
                result.addElement(
                    LookupElementBuilder.create(keyword)
                        .withIcon(MagicScriptIcons.KEYWORD)
                        .withTypeText("keyword")
                        .withBoldness(true)
                );
            }
        }
    }
    
    private static class BuiltinModuleCompletionProvider extends CompletionProvider<CompletionParameters> {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                    @NotNull ProcessingContext context,
                                    @NotNull CompletionResultSet result) {
            for (String module : BUILTIN_MODULES) {
                result.addElement(
                    LookupElementBuilder.create(module)
                        .withIcon(MagicScriptIcons.MODULE)
                        .withTypeText("builtin module")
                        .withTailText(".method()")
                );
            }
        }
    }
}