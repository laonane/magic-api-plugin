package com.magicapi.idea.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import com.magicapi.idea.lang.psi.MSTypes;
import com.magicapi.idea.icons.MagicScriptIcons;
import org.jetbrains.annotations.NotNull;

/**
 * 简化版 Magic Script 智能补全贡献器
 * 提供基本的关键字和内置模块补全功能
 */
public class SimpleMagicScriptCompletionContributor extends CompletionContributor {
    
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
    
    // 数据库模块方法
    private static final String[] DB_METHODS = {
        "select", "selectOne", "selectInt", "selectValue", 
        "insert", "update", "delete", "transaction", "page", "cache"
    };
    
    // HTTP模块方法
    private static final String[] HTTP_METHODS = {
        "get", "post", "put", "delete", "patch", "head", "options"
    };
    
    // Request模块方法
    private static final String[] REQUEST_METHODS = {
        "getParameter", "getHeader", "getBody", "getMethod", 
        "getPath", "getCookie", "getSession"
    };
    
    // Response模块方法
    private static final String[] RESPONSE_METHODS = {
        "json", "text", "setHeader", "setStatus", "setCookie"
    };
    
    public SimpleMagicScriptCompletionContributor() {
        // 基本补全 - 匹配所有标识符位置
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement(MSTypes.IDENTIFIER),
               new BasicCompletionProvider());
    }
    
    /**
     * 基本补全提供器
     */
    private static class BasicCompletionProvider extends CompletionProvider<CompletionParameters> {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                    @NotNull ProcessingContext context,
                                    @NotNull CompletionResultSet result) {
            
            String prefix = result.getPrefixMatcher().getPrefix();
            
            // 检查是否在点号后面（成员访问）
            String textBeforeCursor = getTextBeforeCursor(parameters);
            
            if (textBeforeCursor.endsWith("db.")) {
                // DB模块方法补全
                for (String method : DB_METHODS) {
                    result.addElement(LookupElementBuilder.create(method)
                        .withIcon(MagicScriptIcons.DATABASE)
                        .withTypeText("db")
                        .withTailText("()")
                        .withInsertHandler(createSimpleInsertHandler()));
                }
            } else if (textBeforeCursor.endsWith("http.")) {
                // HTTP模块方法补全
                for (String method : HTTP_METHODS) {
                    result.addElement(LookupElementBuilder.create(method)
                        .withIcon(MagicScriptIcons.HTTP)
                        .withTypeText("http")
                        .withTailText("()")
                        .withInsertHandler(createSimpleInsertHandler()));
                }
            } else if (textBeforeCursor.endsWith("request.")) {
                // Request模块方法补全
                for (String method : REQUEST_METHODS) {
                    result.addElement(LookupElementBuilder.create(method)
                        .withIcon(MagicScriptIcons.MODULE)
                        .withTypeText("request")
                        .withTailText("()")
                        .withInsertHandler(createSimpleInsertHandler()));
                }
            } else if (textBeforeCursor.endsWith("response.")) {
                // Response模块方法补全
                for (String method : RESPONSE_METHODS) {
                    result.addElement(LookupElementBuilder.create(method)
                        .withIcon(MagicScriptIcons.MODULE)
                        .withTypeText("response")
                        .withTailText("()")
                        .withInsertHandler(createSimpleInsertHandler()));
                }
            } else {
                // 关键字补全
                for (String keyword : KEYWORDS) {
                    if (keyword.startsWith(prefix.toLowerCase())) {
                        result.addElement(LookupElementBuilder.create(keyword)
                            .withIcon(MagicScriptIcons.KEYWORD)
                            .withBoldness(true));
                    }
                }
                
                // 内置模块补全
                for (String module : BUILTIN_MODULES) {
                    if (module.startsWith(prefix.toLowerCase())) {
                        result.addElement(LookupElementBuilder.create(module)
                            .withIcon(MagicScriptIcons.BUILTIN)
                            .withTypeText("builtin module"));
                    }
                }
            }
        }
        
        private String getTextBeforeCursor(CompletionParameters parameters) {
            String text = parameters.getEditor().getDocument().getText();
            int offset = parameters.getOffset();
            int start = Math.max(0, offset - 20); // 获取前20个字符
            return text.substring(start, offset);
        }
        
        /**
         * 创建简单的插入处理器，添加括号
         */
        private InsertHandler<LookupElement> createSimpleInsertHandler() {
            return (context, item) -> {
                int offset = context.getTailOffset();
                context.getDocument().insertString(offset, "()");
                context.getEditor().getCaretModel().moveToOffset(offset + 1);
            };
        }
    }
}