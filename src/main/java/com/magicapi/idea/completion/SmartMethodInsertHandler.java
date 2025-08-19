package com.magicapi.idea.completion;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.magicapi.idea.completion.model.ApiMethod;
import com.magicapi.idea.completion.model.Parameter;
import org.jetbrains.annotations.NotNull;

/**
 * 智能方法插入处理器
 * 提供智能参数模板、自动补全括号、参数提示等功能
 */
public class SmartMethodInsertHandler implements InsertHandler<LookupElement> {
    
    private final ApiMethod method;
    private final boolean autoAddParentheses;
    
    public SmartMethodInsertHandler(@NotNull ApiMethod method) {
        this(method, true);
    }
    
    public SmartMethodInsertHandler(@NotNull ApiMethod method, boolean autoAddParentheses) {
        this.method = method;
        this.autoAddParentheses = autoAddParentheses;
    }
    
    @Override
    public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
        Editor editor = context.getEditor();
        Document document = editor.getDocument();
        Project project = context.getProject();
        
        if (!autoAddParentheses) {
            return;
        }
        
        // 检查是否已经有括号
        int offset = context.getTailOffset();
        CharSequence text = document.getCharsSequence();
        
        boolean hasParentheses = false;
        if (offset < text.length() && text.charAt(offset) == '(') {
            hasParentheses = true;
        }
        
        if (!hasParentheses) {
            if (method.getParameters().isEmpty()) {
                // 无参数方法，直接添加空括号
                document.insertString(offset, "()");
                editor.getCaretModel().moveToOffset(offset + 2);
            } else {
                // 有参数方法，使用模板
                insertParameterTemplate(context, project, editor);
            }
        } else {
            // 已有括号，移动光标到括号内
            if (method.getParameters().isEmpty()) {
                editor.getCaretModel().moveToOffset(offset + 2);
            } else {
                editor.getCaretModel().moveToOffset(offset + 1);
                // 可以选择性地启动参数提示
                showParameterInfo(context, project, editor);
            }
        }
    }
    
    /**
     * 插入参数模板
     */
    private void insertParameterTemplate(@NotNull InsertionContext context, 
                                       @NotNull Project project, 
                                       @NotNull Editor editor) {
        TemplateManager templateManager = TemplateManager.getInstance(project);
        Template template = createParameterTemplate(templateManager);
        
        if (template != null) {
            int offset = context.getTailOffset();
            editor.getCaretModel().moveToOffset(offset);
            templateManager.startTemplate(editor, template);
        } else {
            // 回退到简单插入
            int offset = context.getTailOffset();
            context.getDocument().insertString(offset, "()");
            editor.getCaretModel().moveToOffset(offset + 1);
        }
    }
    
    /**
     * 创建参数模板
     */
    private Template createParameterTemplate(@NotNull TemplateManager templateManager) {
        if (method.getParameters().isEmpty()) {
            return null;
        }
        
        TemplateImpl template = (TemplateImpl) templateManager.createTemplate("", "");
        template.setToReformat(true);
        
        StringBuilder templateText = new StringBuilder("(");
        
        for (int i = 0; i < method.getParameters().size(); i++) {
            Parameter param = method.getParameters().get(i);
            
            if (i > 0) {
                templateText.append(", ");
            }
            
            // 创建参数变量
            String varName = "param" + (i + 1);
            templateText.append("$").append(varName).append("$");
            
            // 设置变量默认值和描述
            String defaultValue = getParameterDefaultValue(param);
            String expression = "\"" + defaultValue + "\"";
            
            template.addVariable(varName, expression, expression, true);
        }
        
        templateText.append(")");
        template.setString(templateText.toString());
        
        return template;
    }
    
    /**
     * 获取参数默认值
     */
    private String getParameterDefaultValue(@NotNull Parameter param) {
        // 如果参数有默认值，使用默认值
        if (param.getDefaultValue() != null && !param.getDefaultValue().isEmpty()) {
            return param.getDefaultValue();
        }
        
        // 根据参数类型返回合适的默认值
        return switch (param.getType().toLowerCase()) {
            case "string" -> param.getName();
            case "integer", "int" -> "0";
            case "double", "float" -> "0.0";
            case "boolean" -> "false";
            case "array", "list" -> "[]";
            case "map", "object" -> "{}";
            default -> param.getName();
        };
    }
    
    /**
     * 显示参数信息
     */
    private void showParameterInfo(@NotNull InsertionContext context, 
                                 @NotNull Project project, 
                                 @NotNull Editor editor) {
        // 这里可以触发IntelliJ的参数信息提示
        // 具体实现依赖于IntelliJ平台的参数信息服务
    }
    
    /**
     * 检查是否应该自动添加括号
     */
    private boolean shouldAddParentheses(@NotNull InsertionContext context) {
        // 检查用户设置和上下文
        return autoAddParentheses;
    }
    
    /**
     * 创建简单的插入处理器（仅添加括号）
     */
    public static InsertHandler<LookupElement> createSimple() {
        return (context, item) -> {
            int offset = context.getTailOffset();
            CharSequence text = context.getDocument().getCharsSequence();
            
            if (offset >= text.length() || text.charAt(offset) != '(') {
                context.getDocument().insertString(offset, "()");
                context.getEditor().getCaretModel().moveToOffset(offset + 1);
            }
        };
    }
    
    /**
     * 创建链式调用友好的插入处理器
     */
    public static InsertHandler<LookupElement> createChainable(@NotNull ApiMethod method) {
        return new SmartMethodInsertHandler(method) {
            @Override
            public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
                super.handleInsert(context, item);
                
                // 如果方法可链式调用，在括号后添加点号
                if (method.isChainable()) {
                    Editor editor = context.getEditor();
                    int offset = editor.getCaretModel().getOffset();
                    
                    // 移动到括号外
                    while (offset < context.getDocument().getTextLength() && 
                           context.getDocument().getCharsSequence().charAt(offset) != ')') {
                        offset++;
                    }
                    if (offset < context.getDocument().getTextLength()) {
                        offset++; // 跳过 ')'
                    }
                    
                    editor.getCaretModel().moveToOffset(offset);
                }
            }
        };
    }
}