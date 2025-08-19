package com.magicapi.idea.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementWeigher;
import com.magicapi.idea.completion.model.ApiMethod;
import com.magicapi.idea.completion.model.Parameter;
import com.magicapi.idea.icons.MagicScriptIcons;
import com.magicapi.idea.registry.ModuleRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * 智能补全元素构建器
 * 提供优先级排序、详细文档、参数提示等高级功能
 */
public class SmartLookupElementBuilder {
    
    private final ModuleRegistry moduleRegistry;
    
    public SmartLookupElementBuilder() {
        this.moduleRegistry = ModuleRegistry.getInstance();
    }
    
    /**
     * 为API方法创建智能补全元素
     */
    @NotNull
    public LookupElement createMethodElement(@NotNull ApiMethod method, @NotNull String category) {
        LookupElementBuilder builder = LookupElementBuilder.create(method.getName())
                .withIcon(getMethodIcon(category))
                .withTypeText(method.getReturnType())
                .withTailText(method.getParameterHint())
                .withPresentableText(method.getName())
                .appendTailText(" - " + method.getDescription(), true)
                .withInsertHandler(new SmartMethodInsertHandler(method));
        
        // 添加详细文档
        if (!method.getExample().isEmpty()) {
            builder = builder.appendTailText(" 示例: " + method.getExample(), true);
        }
        
        // 直接返回builder，优先级通过其他方式处理
        return builder;
    }
    
    /**
     * 为内置模块创建补全元素
     */
    @NotNull
    public LookupElement createModuleElement(@NotNull String moduleName, @NotNull String description) {
        LookupElementBuilder builder = LookupElementBuilder.create(moduleName)
                .withIcon(MagicScriptIcons.MODULE)
                .withTypeText("module")
                .withTailText("." + getFirstMethodHint(moduleName))
                .appendTailText(" - " + description, true);
        
        return builder;
    }
    
    /**
     * 为关键字创建补全元素
     */
    @NotNull
    public LookupElement createKeywordElement(@NotNull String keyword, @NotNull String description) {
        LookupElementBuilder builder = LookupElementBuilder.create(keyword)
                .withIcon(MagicScriptIcons.KEYWORD)
                .withTypeText("keyword")
                .withBoldness(true)
                .withTailText(" - " + description);
        
        return builder;
    }
    
    /**
     * 计算方法优先级
     */
    private double calculatePriority(@NotNull ApiMethod method, @NotNull String category) {
        double basePriority = 50.0;
        
        // 根据类别调整优先级
        switch (category.toLowerCase()) {
            case "builtin":
                basePriority += 30.0; // 内置模块方法优先级最高
                break;
            case "global":
                basePriority += 20.0; // 全局函数次之
                break;
            case "extension":
                basePriority += 10.0; // 扩展方法再次
                break;
        }
        
        // 常用方法加分
        if (isCommonMethod(method.getName())) {
            basePriority += 15.0;
        }
        
        // 参数数量越少，优先级越高
        basePriority -= method.getParameters().size() * 2.0;
        
        // 是否链式调用友好
        if (method.isChainable()) {
            basePriority += 5.0;
        }
        
        return basePriority;
    }
    
    /**
     * 计算模块优先级
     */
    private double calculateModulePriority(@NotNull String moduleName) {
        return switch (moduleName) {
            case "db" -> 100.0;      // 数据库模块最常用
            case "http" -> 90.0;     // HTTP模块次之
            case "request" -> 80.0;  // 请求模块
            case "response" -> 80.0; // 响应模块
            case "log" -> 70.0;      // 日志模块
            case "env" -> 60.0;      // 环境模块
            case "magic" -> 50.0;    // Magic模块
            default -> 40.0;
        };
    }
    
    /**
     * 计算关键字优先级
     */
    private double calculateKeywordPriority(@NotNull String keyword) {
        return switch (keyword) {
            case "var", "function", "return" -> 100.0;  // 最常用关键字
            case "if", "else", "for", "while" -> 90.0;  // 控制流关键字
            case "try", "catch", "throw" -> 70.0;       // 异常处理
            case "import", "export" -> 60.0;            // 模块系统
            case "true", "false", "null" -> 50.0;       // 字面量
            default -> 40.0;
        };
    }
    
    /**
     * 检查是否为常用方法
     */
    private boolean isCommonMethod(@NotNull String methodName) {
        String[] commonMethods = {
            // 数据库常用方法
            "select", "selectOne", "insert", "update", "delete", "page",
            // HTTP常用方法
            "get", "post", "put", "delete",
            // 响应常用方法
            "json", "text", "redirect",
            // 请求常用方法
            "getParameter", "getHeader", "getBody",
            // 字符串常用方法
            "length", "substring", "indexOf", "replace", "split",
            // 数组常用方法
            "map", "filter", "forEach", "size", "get",
            // 通用方法
            "toString", "equals", "isNull", "isEmpty"
        };
        
        for (String common : commonMethods) {
            if (common.equals(methodName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取方法图标
     */
    private javax.swing.Icon getMethodIcon(@NotNull String category) {
        return switch (category.toLowerCase()) {
            case "builtin" -> MagicScriptIcons.METHOD;
            case "global" -> MagicScriptIcons.FUNCTION;
            case "extension" -> MagicScriptIcons.EXTENSION;
            default -> MagicScriptIcons.METHOD;
        };
    }
    
    /**
     * 获取模块第一个方法提示
     */
    private String getFirstMethodHint(@NotNull String moduleName) {
        var module = moduleRegistry.getModule(moduleName);
        if (module != null && !module.getMethods().isEmpty()) {
            return module.getMethods().get(0).getName() + "()";
        }
        return "method()";
    }
    
    /**
     * 版本兼容性检查
     */
    public boolean isFeatureAvailable(@NotNull String featureName) {
        return moduleRegistry.isFeatureAvailable(featureName);
    }
    
    /**
     * 获取参数文档
     */
    @NotNull
    public String getParameterDocumentation(@NotNull ApiMethod method) {
        if (method.getParameters().isEmpty()) {
            return "无参数";
        }
        
        StringBuilder doc = new StringBuilder();
        for (int i = 0; i < method.getParameters().size(); i++) {
            Parameter param = method.getParameters().get(i);
            doc.append(param.getFullDescription());
            if (i < method.getParameters().size() - 1) {
                doc.append("\n");
            }
        }
        return doc.toString();
    }
    
    /**
     * 获取方法完整文档
     */
    @NotNull
    public String getMethodDocumentation(@NotNull ApiMethod method) {
        StringBuilder doc = new StringBuilder();
        doc.append("<b>").append(method.getName()).append("</b>");
        doc.append("(").append(method.getParameterHint()).append(")");
        doc.append(": <i>").append(method.getReturnType()).append("</i><br><br>");
        
        doc.append("<b>描述:</b> ").append(method.getDescription()).append("<br>");
        
        if (!method.getParameters().isEmpty()) {
            doc.append("<b>参数:</b><br>");
            for (Parameter param : method.getParameters()) {
                doc.append("&nbsp;&nbsp;• ").append(param.getFullDescription()).append("<br>");
            }
        }
        
        if (!method.getReturnDescription().isEmpty()) {
            doc.append("<b>返回:</b> ").append(method.getReturnDescription()).append("<br>");
        }
        
        if (!method.getExample().isEmpty()) {
            doc.append("<b>示例:</b><br>");
            doc.append("<code>").append(method.getExample()).append("</code>");
        }
        
        return doc.toString();
    }
}