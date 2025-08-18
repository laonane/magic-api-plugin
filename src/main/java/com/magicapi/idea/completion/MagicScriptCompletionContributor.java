package com.magicapi.idea.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.magicapi.idea.completion.context.CompletionContext;
import com.magicapi.idea.completion.context.CompletionContextAnalyzer;
import com.magicapi.idea.completion.model.ApiMethod;
import com.magicapi.idea.completion.model.MagicApiDefinitions;
import com.magicapi.idea.completion.model.MagicApiModule;
import com.magicapi.idea.completion.model.Parameter;
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
    
    public MagicScriptCompletionContributor() {
        // 统一的智能补全提供器
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement(MSTypes.IDENTIFIER),
               new SmartCompletionProvider());
    }
    
    /**
     * 智能补全提供器
     * 根据上下文分析提供精准的补全建议
     */
    private static class SmartCompletionProvider extends CompletionProvider<CompletionParameters> {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                    @NotNull ProcessingContext context,
                                    @NotNull CompletionResultSet result) {
            
            PsiElement element = parameters.getPosition();
            CompletionContext completionContext = CompletionContextAnalyzer.analyzeContext(element);
            
            switch (completionContext.getType()) {
                case MEMBER_ACCESS:
                    addMemberAccessCompletions(completionContext, result);
                    break;
                case FUNCTION_PARAMETER:
                    addParameterCompletions(completionContext, result);
                    break;
                case EXPRESSION:
                default:
                    addExpressionCompletions(result);
                    break;
            }
        }
        
        /**
         * 添加成员访问补全
         */
        private void addMemberAccessCompletions(@NotNull CompletionContext context, 
                                              @NotNull CompletionResultSet result) {
            if (context.isBuiltinModuleAccess()) {
                String moduleName = context.getQualifierType();
                MagicApiModule module = MagicApiDefinitions.getModule(moduleName);
                
                if (module != null) {
                    for (ApiMethod method : module.getMethods()) {
                        LookupElementBuilder element = LookupElementBuilder.create(method.getName())
                            .withIcon(MagicScriptIcons.METHOD)
                            .withTypeText(method.getReturnType())
                            .withTailText(method.getParameterHint())
                            .withPresentableText(method.getName())
                            .appendTailText(" - " + method.getDescription(), true)
                            .withInsertHandler(new MethodInsertHandler(method));
                        
                        result.addElement(element);
                    }
                }
            }
        }
        
        /**
         * 添加参数补全
         */
        private void addParameterCompletions(@NotNull CompletionContext context, 
                                           @NotNull CompletionResultSet result) {
            // TODO: 实现参数智能提示
            // 可以根据函数签名提供参数类型提示
        }
        
        /**
         * 添加表达式补全
         */
        private void addExpressionCompletions(@NotNull CompletionResultSet result) {
            // 添加关键字补全
            addKeywordCompletions(result);
            
            // 添加内置模块补全
            addBuiltinModuleCompletions(result);
        }
        
        /**
         * 添加关键字补全
         */
        private void addKeywordCompletions(@NotNull CompletionResultSet result) {
            for (String keyword : KEYWORDS) {
                result.addElement(
                    LookupElementBuilder.create(keyword)
                        .withIcon(MagicScriptIcons.KEYWORD)
                        .withTypeText("keyword")
                        .withBoldness(true)
                );
            }
        }
        
        /**
         * 添加内置模块补全
         */
        private void addBuiltinModuleCompletions(@NotNull CompletionResultSet result) {
            for (String moduleName : MagicApiDefinitions.getModuleNames()) {
                MagicApiModule module = MagicApiDefinitions.getModule(moduleName);
                if (module != null) {
                    result.addElement(
                        LookupElementBuilder.create(moduleName)
                            .withIcon(MagicScriptIcons.MODULE)
                            .withTypeText("builtin module")
                            .withTailText("." + getFirstMethodName(module))
                            .appendTailText(" - " + module.getDescription(), true)
                    );
                }
            }
        }
        
        /**
         * 获取模块的第一个方法名（用于提示）
         */
        private String getFirstMethodName(@NotNull MagicApiModule module) {
            if (!module.getMethods().isEmpty()) {
                return module.getMethods().get(0).getName() + "()";
            }
            return "method()";
        }
    }
    
    /**
     * 自定义方法插入处理器
     * 支持参数提示和自动括号
     */
    private static class MethodInsertHandler implements InsertHandler<LookupElement> {
        private final ApiMethod method;
        
        public MethodInsertHandler(ApiMethod method) {
            this.method = method;
        }
        
        @Override
        public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
            Editor editor = context.getEditor();
            Project project = context.getProject();
            
            // 插入方法名后添加括号
            editor.getDocument().insertString(context.getTailOffset(), "()");
            
            // 如果方法有参数，则创建参数模板
            if (!method.getParameters().isEmpty()) {
                // 将光标移到括号内
                editor.getCaretModel().moveToOffset(context.getTailOffset() + 1);
                
                // 创建参数模板
                createParameterTemplate(context, editor, project);
            } else {
                // 无参数时，光标移到括号后
                editor.getCaretModel().moveToOffset(context.getTailOffset() + 2);
            }
        }
        
        private void createParameterTemplate(@NotNull InsertionContext context, 
                                           @NotNull Editor editor, 
                                           @NotNull Project project) {
            TemplateManager templateManager = TemplateManager.getInstance(project);
            TemplateImpl template = new TemplateImpl("", "", "");
            
            StringBuilder templateText = new StringBuilder();
            int paramIndex = 0;
            
            for (Parameter param : method.getParameters()) {
                if (paramIndex > 0) {
                    templateText.append(", ");
                }
                
                // 创建参数占位符
                String varName = "param" + paramIndex;
                templateText.append("$").append(varName).append("$");
                
                // 添加变量定义
                template.addVariable(varName, 
                    param.getDefaultValue() != null ? param.getDefaultValue() : param.getName(),
                    param.getDefaultValue() != null ? param.getDefaultValue() : param.getName(),
                    true);
                
                paramIndex++;
            }
            
            template.setString(templateText.toString());
            template.setToReformat(true);
            
            // 启动模板编辑
            templateManager.startTemplate(editor, template);
        }
    }
}