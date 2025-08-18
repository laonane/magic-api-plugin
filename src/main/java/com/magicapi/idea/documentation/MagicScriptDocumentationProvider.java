package com.magicapi.idea.documentation;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.magicapi.idea.completion.model.ApiMethod;
import com.magicapi.idea.completion.model.MagicApiDefinitions;
import com.magicapi.idea.completion.model.MagicApiModule;
import com.magicapi.idea.completion.model.Parameter;
import com.magicapi.idea.lang.psi.MSTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Magic Script 文档提供器
 * 为内置模块和方法提供悬停文档
 */
public class MagicScriptDocumentationProvider extends AbstractDocumentationProvider {
    
    @Override
    @Nullable
    public String generateDoc(@NotNull PsiElement element, @Nullable PsiElement originalElement) {
        
        // 检查是否为标识符
        if (element.getNode().getElementType() != MSTypes.IDENTIFIER) {
            return null;
        }
        
        String elementText = element.getText();
        
        // 检查是否为内置模块
        if (MagicApiDefinitions.isValidModule(elementText)) {
            return generateModuleDoc(elementText);
        }
        
        // 检查是否为方法调用
        String methodDoc = generateMethodDoc(element);
        if (methodDoc != null) {
            return methodDoc;
        }
        
        return null;
    }
    
    /**
     * 生成模块文档
     */
    @Nullable
    private String generateModuleDoc(@NotNull String moduleName) {
        MagicApiModule module = MagicApiDefinitions.getModule(moduleName);
        if (module == null) {
            return null;
        }
        
        StringBuilder doc = new StringBuilder();
        doc.append("<html><body>");
        doc.append("<h3>").append(moduleName).append(" 模块</h3>");
        doc.append("<p>").append(module.getDescription()).append("</p>");
        
        doc.append("<h4>可用方法:</h4>");
        doc.append("<ul>");
        for (ApiMethod method : module.getMethods()) {
            doc.append("<li><code>").append(method.getName()).append("()</code> - ");
            doc.append(method.getDescription()).append("</li>");
        }
        doc.append("</ul>");
        
        doc.append("</body></html>");
        return doc.toString();
    }
    
    /**
     * 生成方法文档
     */
    @Nullable
    private String generateMethodDoc(@NotNull PsiElement element) {
        // 尝试从上下文推断方法信息
        PsiElement prev = element.getPrevSibling();
        if (prev != null && prev.getNode().getElementType() == MSTypes.DOT) {
            PsiElement qualifier = prev.getPrevSibling();
            if (qualifier != null && qualifier.getNode().getElementType() == MSTypes.IDENTIFIER) {
                String moduleName = qualifier.getText();
                String methodName = element.getText();
                
                MagicApiModule module = MagicApiDefinitions.getModule(moduleName);
                if (module != null) {
                    ApiMethod method = module.findMethod(methodName);
                    if (method != null) {
                        return generateApiMethodDoc(method, moduleName);
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * 生成API方法文档
     */
    @NotNull
    private String generateApiMethodDoc(@NotNull ApiMethod method, @NotNull String moduleName) {
        StringBuilder doc = new StringBuilder();
        doc.append("<html><body>");
        
        // 方法签名
        doc.append("<h3>").append(moduleName).append(".").append(method.getName()).append("</h3>");
        doc.append("<p><code>").append(method.getSignature()).append("</code></p>");
        
        // 方法描述
        doc.append("<p>").append(method.getDescription()).append("</p>");
        
        // 参数说明
        if (!method.getParameters().isEmpty()) {
            doc.append("<h4>参数:</h4>");
            doc.append("<ul>");
            for (Parameter param : method.getParameters()) {
                doc.append("<li>");
                doc.append("<code>").append(param.getName()).append("</code>");
                doc.append(" (").append(param.getType()).append(")");
                if (!param.isRequired()) {
                    doc.append(" <i>可选</i>");
                }
                if (param.getDefaultValue() != null) {
                    doc.append(" = ").append(param.getDefaultValue());
                }
                if (param.getDescription() != null && !param.getDescription().isEmpty()) {
                    doc.append(" - ").append(param.getDescription());
                }
                doc.append("</li>");
            }
            doc.append("</ul>");
        }
        
        // 返回值
        if (method.getReturnType() != null && !method.getReturnType().isEmpty()) {
            doc.append("<h4>返回值:</h4>");
            doc.append("<p><code>").append(method.getReturnType()).append("</code>");
            if (method.getReturnDescription() != null && !method.getReturnDescription().isEmpty()) {
                doc.append(" - ").append(method.getReturnDescription());
            }
            doc.append("</p>");
        }
        
        // 示例
        if (method.getExample() != null && !method.getExample().isEmpty()) {
            doc.append("<h4>示例:</h4>");
            doc.append("<pre><code>").append(method.getExample()).append("</code></pre>");
        }
        
        doc.append("</body></html>");
        return doc.toString();
    }
    
    @Override
    @Nullable
    public String getQuickNavigateInfo(@NotNull PsiElement element, @NotNull PsiElement originalElement) {
        // 提供快速导航信息（通常显示在弹出框中）
        if (element.getNode().getElementType() == MSTypes.IDENTIFIER) {
            String elementText = element.getText();
            
            if (MagicApiDefinitions.isValidModule(elementText)) {
                MagicApiModule module = MagicApiDefinitions.getModule(elementText);
                if (module != null) {
                    return elementText + " 模块: " + module.getDescription();
                }
            }
        }
        
        return null;
    }
}