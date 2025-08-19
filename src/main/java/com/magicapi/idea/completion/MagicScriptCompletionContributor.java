package com.magicapi.idea.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.magicapi.idea.completion.context.CompletionContext;
import com.magicapi.idea.completion.context.CompletionContextAnalyzer;
import com.magicapi.idea.completion.model.ApiMethod;
import com.magicapi.idea.completion.model.MagicApiDefinitions;
import com.magicapi.idea.completion.model.MagicApiModule;
import com.magicapi.idea.completion.model.Parameter;
import com.magicapi.idea.registry.ModuleRegistry;
import com.magicapi.idea.lang.psi.MSTypes;
import com.magicapi.idea.icons.MagicScriptIcons;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.PlatformPatterns.or;

/**
 * Magic Script 智能补全贡献器
 * 使用现代化的Pattern匹配进行精确的上下文识别
 */
public class MagicScriptCompletionContributor extends CompletionContributor {
    
    // 模块注册中心
    private final ModuleRegistry moduleRegistry;
    
    // Magic Script关键字
    private static final String[] KEYWORDS = {
        "var", "function", "return", "if", "else", "for", "while", 
        "do", "break", "continue", "try", "catch", "finally", "throw",
        "import", "export", "true", "false", "null", "undefined"
    };
    
    public MagicScriptCompletionContributor() {
        this.moduleRegistry = ModuleRegistry.getInstance();
        // 成员访问补全 - 如 db.select, obj.method
        extend(CompletionType.BASIC,
               memberAccessPattern(),
               new MemberAccessCompletionProvider());
        
        // 链式调用补全 - 如 db.cache().select()  
        extend(CompletionType.BASIC,
               chainedCallPattern(),
               new ChainedMethodCompletionProvider());
        
        // 全局函数补全 - 如 count(), sum(), uuid()
        extend(CompletionType.BASIC,
               globalFunctionPattern(),
               new GlobalFunctionCompletionProvider());
        
        // 扩展方法补全 - 如 list.map(), str.isBlank()
        extend(CompletionType.BASIC,
               extensionMethodPattern(), 
               new ExtensionMethodCompletionProvider());
        
        // 参数位置补全 - 函数参数内的智能提示
        extend(CompletionType.BASIC,
               parameterPattern(),
               new ParameterCompletionProvider());
        
        // import语句补全
        extend(CompletionType.BASIC,
               importPattern(),
               new ImportCompletionProvider());
    }
    
    /**
     * 成员访问Pattern - 匹配 object.member 形式
     * 使用withParent进行精确的PSI结构匹配
     */
    private PsiElementPattern.Capture<PsiElement> memberAccessPattern() {
        return psiElement(MSTypes.IDENTIFIER)
                .withParent(
                    psiElement(MSTypes.MEMBER_ACCESS)
                )
                .afterLeaf(psiElement(MSTypes.DOT));
    }
    
    /**
     * 链式调用Pattern - 匹配 obj.method().next() 形式
     * 使用withSuperParent进行深层PSI结构匹配
     */
    private PsiElementPattern.Capture<PsiElement> chainedCallPattern() {
        return psiElement(MSTypes.IDENTIFIER)
                .withParent(psiElement(MSTypes.MEMBER_ACCESS))
                .withSuperParent(2, psiElement(MSTypes.POSTFIX_EXPRESSION))
                .afterLeaf(psiElement(MSTypes.DOT))
                .afterLeaf(psiElement(MSTypes.RPAREN));
    }
    
    /**
     * 全局函数Pattern - 在表达式开始位置
     * 使用withParent确保在正确的表达式上下文中
     */
    private PsiElementPattern.Capture<PsiElement> globalFunctionPattern() {
        return psiElement(MSTypes.IDENTIFIER)
                .withParent(psiElement(MSTypes.PRIMARY_EXPRESSION))
                .andNot(psiElement().afterLeaf(psiElement(MSTypes.DOT)));
    }
    
    /**
     * 扩展方法Pattern - 对象后面的方法调用
     * 使用withParent确保在成员访问上下文中，但不是内置模块访问
     */
    private PsiElementPattern.Capture<PsiElement> extensionMethodPattern() {
        return psiElement(MSTypes.IDENTIFIER)
                .withParent(psiElement(MSTypes.MEMBER_ACCESS))
                .afterLeaf(psiElement(MSTypes.DOT))
                .andNot(psiElement().withParent(
                    psiElement(MSTypes.MEMBER_ACCESS)
                        .withChild(psiElement(MSTypes.BUILTIN_MODULE))
                ));
    }
    
    /**
     * 参数位置Pattern - 函数调用的参数内部
     * 使用withParent确保在函数调用上下文中
     */
    private PsiElementPattern.Capture<PsiElement> parameterPattern() {
        return psiElement()
                .withParent(psiElement(MSTypes.FUNCTION_CALL))
                .afterLeaf(psiElement(MSTypes.LPAREN));
    }
    
    /**
     * import语句Pattern
     */
    private PsiElementPattern.Capture<PsiElement> importPattern() {
        return psiElement(MSTypes.STRING_LITERAL)
                .afterLeaf(psiElement(MSTypes.IMPORT));
    }
    
    /**
     * 成员访问补全提供器
     * 处理 obj.method 形式的补全，使用精确的PSI树遍历
     */
    private static class MemberAccessCompletionProvider extends CompletionProvider<CompletionParameters> {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                    @NotNull ProcessingContext context,
                                    @NotNull CompletionResultSet result) {
            
            PsiElement element = parameters.getPosition();
            
            // 使用PSI树遍历获取更精确的上下文信息
            PsiElement memberAccess = element.getParent();
            if (memberAccess == null) return;
            
            // 获取限定符（点号前面的表达式）
            PsiElement qualifier = getQualifier(memberAccess);
            if (qualifier == null) return;
            
            // 分析限定符类型
            String qualifierType = analyzeQualifierType(qualifier);
            
            // 创建精确的补全上下文
            CompletionContext completionContext = new CompletionContext(
                CompletionContext.Type.MEMBER_ACCESS, 
                qualifier.getText(), 
                qualifierType
            );
            
            // 根据限定符类型提供相应的补全
            if (completionContext.isBuiltinModuleAccess()) {
                addBuiltinModuleCompletions(result, qualifierType);
            } else {
                addExtensionMethodCompletions(result, qualifierType);
            }
        }
        
        /**
         * 获取成员访问的限定符
         */
        private PsiElement getQualifier(PsiElement memberAccess) {
            // 简化实现：获取点号前面的元素
            PsiElement current = memberAccess.getPrevSibling();
            while (current != null && current.getNode().getElementType() == com.intellij.psi.TokenType.WHITE_SPACE) {
                current = current.getPrevSibling();
            }
            return current;
        }
        
        /**
         * 分析限定符类型
         */
        private String analyzeQualifierType(PsiElement qualifier) {
            String text = qualifier.getText();
            
            // 检查是否为内置模块
            if (isBuiltinModule(text)) {
                return text;
            }
            
            // TODO: 实现更复杂的类型推断
            // 这里可以根据变量声明、函数返回类型等进行推断
            
            return "Object"; // 默认类型
        }
        
        /**
         * 检查是否为内置模块
         */
        private boolean isBuiltinModule(String text) {
            ModuleRegistry registry = ModuleRegistry.getInstance();
            return registry.hasModule(text);
        }
        
        /**
         * 添加内置模块补全
         */
        private void addBuiltinModuleCompletions(@NotNull CompletionResultSet result, String moduleName) {
            ModuleRegistry registry = ModuleRegistry.getInstance();
            MagicApiModule module = registry.getModule(moduleName);
            
            if (module != null) {
                for (ApiMethod method : module.getMethods()) {
                    LookupElementBuilder lookupElement = LookupElementBuilder.create(method.getName())
                        .withIcon(MagicScriptIcons.METHOD)
                        .withTypeText(method.getReturnType())
                        .withTailText(method.getParameterHint())
                        .withPresentableText(method.getName())
                        .appendTailText(" - " + method.getDescription(), true)
                        .withInsertHandler(new MethodInsertHandler(method));
                    
                    // 直接添加到结果中
                    result.addElement(lookupElement);
                }
            }
        }
        
        /**
         * 添加扩展方法补全
         */
        private void addExtensionMethodCompletions(@NotNull CompletionResultSet result, String objectType) {
            // 使用ExtensionMethodProvider获取扩展方法
            ModuleRegistry registry = ModuleRegistry.getInstance();
            for (ApiMethod method : registry.getExtensionMethods(objectType)) {
                LookupElementBuilder element = LookupElementBuilder.create(method.getName())
                        .withIcon(MagicScriptIcons.METHOD)
                        .withTypeText(method.getReturnType())
                        .withTailText(method.getParameterHint())
                        .appendTailText(" - " + method.getDescription(), true)
                        .withInsertHandler(new MethodInsertHandler(method));
                
                result.addElement(element);
            }
        }
        
        // 扩展方法实现（复用之前的代码）
        private void addStringExtensionMethods(@NotNull CompletionResultSet result) {
            String[] methods = {"isBlank", "isNotBlank", "length", "substring", "indexOf", "replace", "split", "trim"};
            for (String method : methods) {
                LookupElementBuilder element = LookupElementBuilder.create(method)
                        .withIcon(MagicScriptIcons.METHOD)
                        .withTypeText(getStringMethodReturnType(method))
                        .withTailText(getStringMethodParams(method) + " - String扩展方法")
                        .withInsertHandler(new MethodInsertHandler(new ApiMethod(method, getStringMethodReturnType(method), "String扩展方法")));
                
                result.addElement(element);
            }
        }
        
        private void addNumberExtensionMethods(@NotNull CompletionResultSet result) {
            String[] methods = {"round", "floor", "ceil", "toFixed", "asPercent"};
            for (String method : methods) {
                LookupElementBuilder element = LookupElementBuilder.create(method)
                        .withIcon(MagicScriptIcons.METHOD)
                        .withTypeText("Number")
                        .withTailText("() - Number扩展方法")
                        .withInsertHandler(new MethodInsertHandler(new ApiMethod(method, "Number", "Number扩展方法")));
                
                result.addElement(element);
            }
        }
        
        private void addArrayExtensionMethods(@NotNull CompletionResultSet result) {
            String[] methods = {"map", "filter", "each", "sort", "first", "last", "size", "isEmpty"};
            for (String method : methods) {
                LookupElementBuilder element = LookupElementBuilder.create(method)
                        .withIcon(MagicScriptIcons.METHOD)
                        .withTypeText(getArrayMethodReturnType(method))
                        .withTailText(getArrayMethodParams(method) + " - Array扩展方法")
                        .withInsertHandler(new MethodInsertHandler(new ApiMethod(method, getArrayMethodReturnType(method), "Array扩展方法")));
                
                result.addElement(element);
            }
        }
        
        private void addObjectExtensionMethods(@NotNull CompletionResultSet result) {
            String[] methods = {"asInt", "asDouble", "asString", "is", "isString", "isNumber", "isNull", "toString"};
            for (String method : methods) {
                LookupElementBuilder element = LookupElementBuilder.create(method)
                        .withIcon(MagicScriptIcons.METHOD)
                        .withTypeText(getObjectMethodReturnType(method))
                        .withTailText("() - Object扩展方法")
                        .withInsertHandler(new MethodInsertHandler(new ApiMethod(method, getObjectMethodReturnType(method), "Object扩展方法")));
                
                result.addElement(element);
            }
        }
        
        // 辅助方法
        private String getStringMethodReturnType(String method) {
            return switch (method) {
                case "isBlank", "isNotBlank" -> "Boolean";
                case "length", "indexOf" -> "Integer";
                case "substring", "replace", "trim" -> "String";
                case "split" -> "Array";
                default -> "String";
            };
        }
        
        private String getStringMethodParams(String method) {
            return switch (method) {
                case "isBlank", "isNotBlank", "length", "trim" -> "()";
                case "substring" -> "(start, end?)";
                case "indexOf" -> "(searchValue)";
                case "replace" -> "(searchValue, replaceValue)";
                case "split" -> "(separator)";
                default -> "()";
            };
        }
        
        private String getArrayMethodReturnType(String method) {
            return switch (method) {
                case "map", "filter", "sort" -> "Array";
                case "first", "last" -> "Object";
                case "size" -> "Integer";
                case "isEmpty" -> "Boolean";
                case "each" -> "void";
                default -> "Object";
            };
        }
        
        private String getArrayMethodParams(String method) {
            return switch (method) {
                case "map", "filter", "each" -> "(function)";
                case "sort" -> "(comparator?)";
                case "first", "last", "size", "isEmpty" -> "()";
                default -> "()";
            };
        }
        
        private String getObjectMethodReturnType(String method) {
            return switch (method) {
                case "asInt" -> "Integer";
                case "asDouble" -> "Double";
                case "asString", "toString" -> "String";
                case "is", "isString", "isNumber", "isNull" -> "Boolean";
                default -> "Object";
            };
        }
    }
    
    /**
     * 链式方法调用补全提供器
     * 处理 obj.method().next() 形式的补全，支持返回类型推断
     */
    private static class ChainedMethodCompletionProvider extends CompletionProvider<CompletionParameters> {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                    @NotNull ProcessingContext context,
                                    @NotNull CompletionResultSet result) {
            
            PsiElement element = parameters.getPosition();
            
            // 分析链式调用的上下文
            ChainCallAnalysisResult chainResult = analyzeChainCall(element);
            if (chainResult == null) return;
            
            // 根据前一个方法的返回类型提供相应的补全
            String returnType = chainResult.getReturnType();
            String previousMethod = chainResult.getPreviousMethod();
            String baseObject = chainResult.getBaseObject();
            
            // 为链式调用添加适当的补全
            addChainedCompletions(result, returnType, previousMethod, baseObject);
        }
        
        /**
         * 分析链式调用上下文
         */
        private ChainCallAnalysisResult analyzeChainCall(PsiElement element) {
            // 向上遍历PSI树，寻找链式调用结构
            // 期望结构：identifier.method().identifier
            
            PsiElement current = element;
            
            // 1. 确认当前位置是在点号后面
            PsiElement prevSibling = current.getPrevSibling();
            while (prevSibling != null && prevSibling.getNode().getElementType() == com.intellij.psi.TokenType.WHITE_SPACE) {
                prevSibling = prevSibling.getPrevSibling();
            }
            
            if (prevSibling == null || !".".equals(prevSibling.getText())) {
                return null;
            }
            
            // 2. 寻找点号前面的函数调用表达式
            PsiElement beforeDot = prevSibling.getPrevSibling();
            while (beforeDot != null && beforeDot.getNode().getElementType() == com.intellij.psi.TokenType.WHITE_SPACE) {
                beforeDot = beforeDot.getPrevSibling();
            }
            
            if (beforeDot == null || !")".equals(beforeDot.getText())) {
                return null; // 不是函数调用
            }
            
            // 3. 分析前面的方法调用
            MethodCallInfo methodCall = extractMethodCall(beforeDot);
            if (methodCall == null) return null;
            
            // 4. 推断返回类型
            String returnType = inferReturnType(methodCall.getBaseObject(), methodCall.getMethodName());
            
            return new ChainCallAnalysisResult(
                methodCall.getBaseObject(),
                methodCall.getMethodName(), 
                returnType
            );
        }
        
        /**
         * 提取方法调用信息
         */
        private MethodCallInfo extractMethodCall(PsiElement closingParen) {
            // 向前寻找匹配的开括号
            PsiElement current = closingParen.getPrevSibling();
            int parenCount = 1;
            
            while (current != null && parenCount > 0) {
                String text = current.getText();
                if (")".equals(text)) {
                    parenCount++;
                } else if ("(".equals(text)) {
                    parenCount--;
                }
                if (parenCount > 0) {
                    current = current.getPrevSibling();
                }
            }
            
            if (current == null) return null;
            
            // 现在current指向开括号，向前找方法名
            PsiElement methodName = current.getPrevSibling();
            while (methodName != null && methodName.getNode().getElementType() == com.intellij.psi.TokenType.WHITE_SPACE) {
                methodName = methodName.getPrevSibling();
            }
            
            if (methodName == null) return null;
            
            // 找到点号前面的对象
            PsiElement dot = methodName.getPrevSibling();
            while (dot != null && dot.getNode().getElementType() == com.intellij.psi.TokenType.WHITE_SPACE) {
                dot = dot.getPrevSibling();
            }
            
            if (dot == null || !".".equals(dot.getText())) {
                return null;
            }
            
            PsiElement baseObject = dot.getPrevSibling();
            while (baseObject != null && baseObject.getNode().getElementType() == com.intellij.psi.TokenType.WHITE_SPACE) {
                baseObject = baseObject.getPrevSibling();
            }
            
            if (baseObject == null) return null;
            
            return new MethodCallInfo(baseObject.getText(), methodName.getText());
        }
        
        /**
         * 推断方法返回类型
         */
        private String inferReturnType(String baseObject, String methodName) {
            // 根据基础对象和方法名推断返回类型
            
            // 1. 内置模块方法返回类型
            if (isBuiltinModule(baseObject)) {
                return inferBuiltinModuleReturnType(baseObject, methodName);
            }
            
            // 2. 扩展方法返回类型
            return inferExtensionMethodReturnType(methodName);
        }
        
        /**
         * 推断内置模块方法返回类型
         */
        private String inferBuiltinModuleReturnType(String moduleName, String methodName) {
            switch (moduleName) {
                case "db":
                    return inferDatabaseMethodReturnType(methodName);
                case "http":
                    return inferHttpMethodReturnType(methodName);
                case "request":
                    return "Object";
                case "response":
                    return "ResponseBuilder";
                default:
                    return "Object";
            }
        }
        
        /**
         * 推断数据库方法返回类型
         */
        private String inferDatabaseMethodReturnType(String methodName) {
            switch (methodName) {
                case "select":
                case "selectByKey":
                    return "List";
                case "selectOne":
                case "selectByPrimaryKey":
                    return "Object";
                case "selectInt":
                case "selectLong":
                case "count":
                    return "Number";
                case "selectValue":
                    return "Object";
                case "page":
                    return "PageResult";
                case "cache":
                    return "CacheableQuery";
                case "transaction":
                    return "TransactionManager";
                default:
                    return "Object";
            }
        }
        
        /**
         * 推断HTTP方法返回类型
         */
        private String inferHttpMethodReturnType(String methodName) {
            switch (methodName) {
                case "get":
                case "post":
                case "put":
                case "delete":
                case "patch":
                    return "HttpResponse";
                case "connect":
                case "header":
                case "data":
                case "body":
                    return "HttpRequestBuilder";
                default:
                    return "Object";
            }
        }
        
        /**
         * 推断扩展方法返回类型
         */
        private String inferExtensionMethodReturnType(String methodName) {
            switch (methodName) {
                case "map":
                case "filter":
                case "sort":
                    return "Array";
                case "first":
                case "last":
                    return "Object";
                case "asInt":
                    return "Integer";
                case "asDouble":
                    return "Double";
                case "asString":
                case "toString":
                    return "String";
                case "size":
                    return "Integer";
                default:
                    return "Object";
            }
        }
        
        /**
         * 添加链式调用补全
         */
        private void addChainedCompletions(@NotNull CompletionResultSet result, 
                                         String returnType, 
                                         String previousMethod, 
                                         String baseObject) {
            
            switch (returnType) {
                case "List":
                case "Array":
                    addArrayChainMethods(result);
                    break;
                case "Object":
                    addObjectChainMethods(result);
                    break;
                case "String":
                    addStringChainMethods(result);
                    break;
                case "Number":
                case "Integer":
                case "Double":
                    addNumberChainMethods(result);
                    break;
                case "PageResult":
                    addPageResultChainMethods(result);
                    break;
                case "HttpResponse":
                    addHttpResponseChainMethods(result);
                    break;
                case "HttpRequestBuilder":
                    addHttpRequestBuilderChainMethods(result);
                    break;
                case "CacheableQuery":
                    addCacheableQueryChainMethods(result);
                    break;
                case "TransactionManager":
                    addTransactionManagerChainMethods(result);
                    break;
                default:
                    addGenericChainMethods(result);
                    break;
            }
        }
        
        // 各种类型的链式方法补全
        private void addArrayChainMethods(@NotNull CompletionResultSet result) {
            String[] methods = {"map", "filter", "each", "sort", "first", "last", "size", "isEmpty", "join"};
            for (String method : methods) {
                addChainMethod(result, method, getArrayMethodReturnType(method), "Array链式方法");
            }
        }
        
        private void addStringChainMethods(@NotNull CompletionResultSet result) {
            String[] methods = {"substring", "replace", "split", "trim", "toLowerCase", "toUpperCase", "length"};
            for (String method : methods) {
                addChainMethod(result, method, getStringMethodReturnType(method), "String链式方法");
            }
        }
        
        private void addNumberChainMethods(@NotNull CompletionResultSet result) {
            String[] methods = {"round", "floor", "ceil", "toFixed", "asPercent", "toString"};
            for (String method : methods) {
                addChainMethod(result, method, "Number", "Number链式方法");
            }
        }
        
        private void addObjectChainMethods(@NotNull CompletionResultSet result) {
            String[] methods = {"asInt", "asDouble", "asString", "toString", "isNull", "isNotNull"};
            for (String method : methods) {
                addChainMethod(result, method, getObjectMethodReturnType(method), "Object链式方法");
            }
        }
        
        private void addPageResultChainMethods(@NotNull CompletionResultSet result) {
            String[] methods = {"getRecords", "getTotal", "getPages", "getCurrent", "getSize"};
            String[] types = {"List", "Long", "Long", "Long", "Long"};
            for (int i = 0; i < methods.length; i++) {
                addChainMethod(result, methods[i], types[i], "分页结果方法");
            }
        }
        
        private void addHttpResponseChainMethods(@NotNull CompletionResultSet result) {
            String[] methods = {"json", "text", "bytes", "headers", "status", "cookies"};
            String[] types = {"Object", "String", "byte[]", "Map", "Integer", "Map"};
            for (int i = 0; i < methods.length; i++) {
                addChainMethod(result, methods[i], types[i], "HTTP响应方法");
            }
        }
        
        private void addHttpRequestBuilderChainMethods(@NotNull CompletionResultSet result) {
            String[] methods = {"header", "data", "body", "timeout", "get", "post", "put", "delete"};
            String[] types = {"HttpRequestBuilder", "HttpRequestBuilder", "HttpRequestBuilder", "HttpRequestBuilder", 
                             "HttpResponse", "HttpResponse", "HttpResponse", "HttpResponse"};
            for (int i = 0; i < methods.length; i++) {
                addChainMethod(result, methods[i], types[i], "HTTP请求构建方法");
            }
        }
        
        private void addCacheableQueryChainMethods(@NotNull CompletionResultSet result) {
            String[] methods = {"select", "selectOne", "selectInt", "page", "ttl", "key"};
            String[] types = {"List", "Object", "Integer", "PageResult", "CacheableQuery", "CacheableQuery"};
            for (int i = 0; i < methods.length; i++) {
                addChainMethod(result, methods[i], types[i], "可缓存查询方法");
            }
        }
        
        private void addTransactionManagerChainMethods(@NotNull CompletionResultSet result) {
            String[] methods = {"rollback", "commit", "execute"};
            String[] types = {"void", "void", "Object"};
            for (int i = 0; i < methods.length; i++) {
                addChainMethod(result, methods[i], types[i], "事务管理方法");
            }
        }
        
        private void addGenericChainMethods(@NotNull CompletionResultSet result) {
            // 通用的对象方法
            addObjectChainMethods(result);
        }
        
        /**
         * 添加链式方法补全项
         */
        private void addChainMethod(@NotNull CompletionResultSet result, 
                                  String methodName, 
                                  String returnType, 
                                  String description) {
            LookupElementBuilder element = LookupElementBuilder.create(methodName)
                .withIcon(MagicScriptIcons.METHOD)
                .withTypeText(returnType)
                .withTailText("() - " + description)
                .withInsertHandler(new MethodInsertHandler(new ApiMethod(methodName, returnType, description)));
            
            result.addElement(element);
        }
        
        // 辅助方法
        private boolean isBuiltinModule(String name) {
            ModuleRegistry registry = ModuleRegistry.getInstance();
            return registry.hasModule(name);
        }
        
        private String getArrayMethodReturnType(String method) {
            return switch (method) {
                case "map", "filter", "sort" -> "Array";
                case "first", "last" -> "Object";
                case "size" -> "Integer";
                case "isEmpty" -> "Boolean";
                case "join" -> "String";
                default -> "Object";
            };
        }
        
        private String getStringMethodReturnType(String method) {
            return switch (method) {
                case "length" -> "Integer";
                case "split" -> "Array";
                case "substring", "replace", "trim", "toLowerCase", "toUpperCase" -> "String";
                default -> "String";
            };
        }
        
        private String getObjectMethodReturnType(String method) {
            return switch (method) {
                case "asInt" -> "Integer";
                case "asDouble" -> "Double";
                case "asString", "toString" -> "String";
                case "isNull", "isNotNull" -> "Boolean";
                default -> "Object";
            };
        }
        
        /**
         * 链式调用分析结果
         */
        private static class ChainCallAnalysisResult {
            private final String baseObject;
            private final String previousMethod;
            private final String returnType;
            
            public ChainCallAnalysisResult(String baseObject, String previousMethod, String returnType) {
                this.baseObject = baseObject;
                this.previousMethod = previousMethod;
                this.returnType = returnType;
            }
            
            public String getBaseObject() { return baseObject; }
            public String getPreviousMethod() { return previousMethod; }
            public String getReturnType() { return returnType; }
        }
        
        /**
         * 方法调用信息
         */
        private static class MethodCallInfo {
            private final String baseObject;
            private final String methodName;
            
            public MethodCallInfo(String baseObject, String methodName) {
                this.baseObject = baseObject;
                this.methodName = methodName;
            }
            
            public String getBaseObject() { return baseObject; }
            public String getMethodName() { return methodName; }
        }
    }
    
    /**
     * 全局函数补全提供器
     * 处理全局可用函数的补全，包括关键字、内置模块、全局函数等
     */
    private static class GlobalFunctionCompletionProvider extends CompletionProvider<CompletionParameters> {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                    @NotNull ProcessingContext context,
                                    @NotNull CompletionResultSet result) {
            
            PsiElement element = parameters.getPosition();
            
            // 检查是否在全局作用域内（不在成员访问中）
            if (isInGlobalScope(element)) {
                // 添加关键字补全
                addKeywordCompletions(result);
                
                // 添加内置模块补全
                addBuiltinModuleCompletions(result);
                
                // 添加全局函数补全
                addGlobalFunctions(result);
                
                // 添加常用变量和常量
                addCommonVariables(result);
            }
        }
        
        /**
         * 检查是否在全局作用域内
         */
        private boolean isInGlobalScope(PsiElement element) {
            // 检查是否不在点号后面（成员访问）
            PsiElement prev = element.getPrevSibling();
            while (prev != null && prev.getNode().getElementType() == com.intellij.psi.TokenType.WHITE_SPACE) {
                prev = prev.getPrevSibling();
            }
            return prev == null || !".".equals(prev.getText());
        }
        
        private void addKeywordCompletions(@NotNull CompletionResultSet result) {
            for (String keyword : KEYWORDS) {
                result.addElement(
                    LookupElementBuilder.create(keyword)
                        .withIcon(MagicScriptIcons.KEYWORD)
                        .withTypeText("keyword")
                        .withBoldness(true)
                        .withTailText(" - " + getKeywordDescription(keyword))
                );
            }
        }
        
        private void addBuiltinModuleCompletions(@NotNull CompletionResultSet result) {
            ModuleRegistry registry = ModuleRegistry.getInstance();
            for (String moduleName : registry.getModuleNames()) {
                MagicApiModule module = registry.getModule(moduleName);
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
        
        private void addGlobalFunctions(@NotNull CompletionResultSet result) {
            // 使用GlobalFunctionProvider获取所有全局函数
            ModuleRegistry registry = ModuleRegistry.getInstance();
            for (ApiMethod function : registry.getGlobalFunctions()) {
                result.addElement(
                    LookupElementBuilder.create(function.getName())
                        .withIcon(MagicScriptIcons.FUNCTION)
                        .withTypeText(function.getReturnType())
                        .withTailText(function.getParameterHint())
                        .appendTailText(" - " + function.getDescription(), true)
                        .withInsertHandler(new MethodInsertHandler(function))
                );
            }
        }
        
        private void addAggregateFunctions(@NotNull CompletionResultSet result) {
            String[][] functions = {
                {"count", "Number", "计算数量"},
                {"sum", "Number", "求和"},
                {"max", "Number", "最大值"},
                {"min", "Number", "最小值"},
                {"avg", "Number", "平均值"},
                {"group_concat", "String", "分组连接"}
            };
            
            for (String[] func : functions) {
                result.addElement(
                    LookupElementBuilder.create(func[0])
                        .withIcon(MagicScriptIcons.FUNCTION)
                        .withTypeText(func[1])
                        .withTailText("() - " + func[2])
                        .withInsertHandler(new MethodInsertHandler(new ApiMethod(func[0], func[1], func[2])))
                );
            }
        }
        
        private void addMathFunctions(@NotNull CompletionResultSet result) {
            String[][] functions = {
                {"round", "Number", "四舍五入"},
                {"floor", "Number", "向下取整"},
                {"ceil", "Number", "向上取整"},
                {"abs", "Number", "绝对值"},
                {"sqrt", "Number", "平方根"},
                {"pow", "Number", "幂运算"},
                {"random", "Double", "随机数"},
                {"percent", "String", "百分比格式"}
            };
            
            for (String[] func : functions) {
                result.addElement(
                    LookupElementBuilder.create(func[0])
                        .withIcon(MagicScriptIcons.FUNCTION)
                        .withTypeText(func[1])
                        .withTailText("() - " + func[2])
                        .withInsertHandler(new MethodInsertHandler(new ApiMethod(func[0], func[1], func[2])))
                );
            }
        }
        
        private void addStringFunctions(@NotNull CompletionResultSet result) {
            String[][] functions = {
                {"uuid", "String", "生成UUID"},
                {"is_blank", "Boolean", "判断空字符串"},
                {"not_blank", "Boolean", "判断非空字符串"},
                {"concat", "String", "字符串连接"},
                {"format", "String", "格式化字符串"},
                {"md5", "String", "MD5加密"},
                {"sha1", "String", "SHA1加密"},
                {"base64_encode", "String", "Base64编码"},
                {"base64_decode", "String", "Base64解码"},
                {"url_encode", "String", "URL编码"},
                {"url_decode", "String", "URL解码"}
            };
            
            for (String[] func : functions) {
                result.addElement(
                    LookupElementBuilder.create(func[0])
                        .withIcon(MagicScriptIcons.FUNCTION)
                        .withTypeText(func[1])
                        .withTailText("() - " + func[2])
                        .withInsertHandler(new MethodInsertHandler(new ApiMethod(func[0], func[1], func[2])))
                );
            }
        }
        
        private void addDateTimeFunctions(@NotNull CompletionResultSet result) {
            String[][] functions = {
                {"now", "Long", "当前时间戳"},
                {"current_timestamp", "Long", "当前时间戳"},
                {"current_date", "String", "当前日期"},
                {"current_time", "String", "当前时间"},
                {"date_format", "String", "日期格式化"},
                {"parse_date", "Date", "解析日期"},
                {"add_days", "Date", "日期加天数"},
                {"add_months", "Date", "日期加月数"},
                {"diff_days", "Long", "日期差（天）"},
                {"diff_hours", "Long", "日期差（小时）"},
                {"diff_minutes", "Long", "日期差（分钟）"}
            };
            
            for (String[] func : functions) {
                result.addElement(
                    LookupElementBuilder.create(func[0])
                        .withIcon(MagicScriptIcons.FUNCTION)
                        .withTypeText(func[1])
                        .withTailText("() - " + func[2])
                        .withInsertHandler(new MethodInsertHandler(new ApiMethod(func[0], func[1], func[2])))
                );
            }
        }
        
        private void addArrayConstructors(@NotNull CompletionResultSet result) {
            String[][] functions = {
                {"new_array", "Array", "创建新数组"},
                {"new_list", "List", "创建新列表"},
                {"new_map", "Map", "创建新映射"},
                {"new_set", "Set", "创建新集合"},
                {"new_string_array", "String[]", "创建字符串数组"},
                {"new_int_array", "Integer[]", "创建整数数组"},
                {"new_double_array", "Double[]", "创建浮点数组"}
            };
            
            for (String[] func : functions) {
                result.addElement(
                    LookupElementBuilder.create(func[0])
                        .withIcon(MagicScriptIcons.FUNCTION)
                        .withTypeText(func[1])
                        .withTailText("() - " + func[2])
                        .withInsertHandler(new MethodInsertHandler(new ApiMethod(func[0], func[1], func[2])))
                );
            }
        }
        
        private void addUtilityFunctions(@NotNull CompletionResultSet result) {
            String[][] functions = {
                {"not_null", "Boolean", "非空判断"},
                {"is_null", "Boolean", "空值判断"},
                {"ifnull", "Object", "空值替换"},
                {"nvl", "Object", "空值替换"},
                {"coalesce", "Object", "第一个非空值"},
                {"typeof", "String", "获取类型"},
                {"instanceof", "Boolean", "类型检查"},
                {"sleep", "void", "线程休眠"},
                {"thread_id", "Long", "线程ID"},
                {"thread_name", "String", "线程名称"}
            };
            
            for (String[] func : functions) {
                result.addElement(
                    LookupElementBuilder.create(func[0])
                        .withIcon(MagicScriptIcons.FUNCTION)
                        .withTypeText(func[1])
                        .withTailText("() - " + func[2])
                        .withInsertHandler(new MethodInsertHandler(new ApiMethod(func[0], func[1], func[2])))
                );
            }
        }
        
        private void addDebugFunctions(@NotNull CompletionResultSet result) {
            String[][] functions = {
                {"print", "void", "控制台输出"},
                {"println", "void", "控制台输出（换行）"},
                {"printf", "void", "格式化输出"},
                {"debug", "void", "调试输出"},
                {"info", "void", "信息输出"},
                {"warn", "void", "警告输出"},
                {"error", "void", "错误输出"},
                {"dump", "String", "对象转储"},
                {"trace", "void", "堆栈跟踪"}
            };
            
            for (String[] func : functions) {
                result.addElement(
                    LookupElementBuilder.create(func[0])
                        .withIcon(MagicScriptIcons.FUNCTION)
                        .withTypeText(func[1])
                        .withTailText("() - " + func[2])
                        .withInsertHandler(new MethodInsertHandler(new ApiMethod(func[0], func[1], func[2])))
                );
            }
        }
        
        private void addCommonVariables(@NotNull CompletionResultSet result) {
            String[][] variables = {
                {"this", "Object", "当前对象"},
                {"arguments", "Array", "函数参数"},
                {"__LINE__", "Integer", "当前行号"},
                {"__FILE__", "String", "当前文件"},
                {"__METHOD__", "String", "当前方法"},
                {"PI", "Double", "圆周率"},
                {"E", "Double", "自然常数"}
            };
            
            for (String[] var : variables) {
                result.addElement(
                    LookupElementBuilder.create(var[0])
                        .withIcon(MagicScriptIcons.VARIABLE)
                        .withTypeText(var[1])
                        .withTailText(" - " + var[2])
                );
            }
        }
        
        private String getKeywordDescription(String keyword) {
            return switch (keyword) {
                case "var" -> "变量声明";
                case "function" -> "函数声明";
                case "return" -> "返回语句";
                case "if" -> "条件语句";
                case "else" -> "否则分支";
                case "for" -> "循环语句";
                case "while" -> "条件循环";
                case "do" -> "do-while循环";
                case "break" -> "跳出循环";
                case "continue" -> "继续循环";
                case "try" -> "异常处理";
                case "catch" -> "异常捕获";
                case "finally" -> "最终执行";
                case "throw" -> "抛出异常";
                case "import" -> "导入模块";
                case "export" -> "导出模块";
                case "true" -> "布尔真值";
                case "false" -> "布尔假值";
                case "null" -> "空值";
                case "undefined" -> "未定义值";
                default -> "关键字";
            };
        }
        
        private String getFirstMethodName(@NotNull MagicApiModule module) {
            if (!module.getMethods().isEmpty()) {
                return module.getMethods().get(0).getName() + "()";
            }
            return "method()";
        }
    }
    
    /**
     * 扩展方法补全提供器
     * 处理对象扩展方法的补全，根据对象类型智能推断可用的扩展方法
     */
    private static class ExtensionMethodCompletionProvider extends CompletionProvider<CompletionParameters> {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                    @NotNull ProcessingContext context,
                                    @NotNull CompletionResultSet result) {
            
            PsiElement element = parameters.getPosition();
            
            // 分析扩展方法调用上下文
            ExtensionMethodContext extensionContext = analyzeExtensionMethodContext(element);
            if (extensionContext == null) return;
            
            String objectType = extensionContext.getObjectType();
            String objectValue = extensionContext.getObjectValue();
            
            // 根据对象类型提供相应的扩展方法
            switch (objectType) {
                case "String":
                    addStringExtensionMethods(result);
                    break;
                case "Number":
                case "Integer":
                case "Double":
                case "Float":
                case "Long":
                    addNumberExtensionMethods(result);
                    break;
                case "Array":
                case "List":
                case "Collection":
                    addArrayExtensionMethods(result);
                    break;
                case "Map":
                case "HashMap":
                case "LinkedHashMap":
                    addMapExtensionMethods(result);
                    break;
                case "Date":
                case "LocalDate":
                case "LocalDateTime":
                    addDateExtensionMethods(result);
                    break;
                case "Boolean":
                    addBooleanExtensionMethods(result);
                    break;
                case "Object":
                    addObjectExtensionMethods(result);
                    break;
                default:
                    // 推断可能的类型或添加通用扩展方法
                    addInferredExtensionMethods(result, objectValue);
                    addCommonExtensionMethods(result);
                    break;
            }
        }
        
        /**
         * 分析扩展方法调用上下文
         */
        private ExtensionMethodContext analyzeExtensionMethodContext(PsiElement element) {
            // 检查是否在点号后面
            PsiElement prevSibling = element.getPrevSibling();
            while (prevSibling != null && prevSibling.getNode().getElementType() == com.intellij.psi.TokenType.WHITE_SPACE) {
                prevSibling = prevSibling.getPrevSibling();
            }
            
            if (prevSibling == null || !".".equals(prevSibling.getText())) {
                return null;
            }
            
            // 获取点号前的对象表达式
            PsiElement objectExpression = prevSibling.getPrevSibling();
            while (objectExpression != null && objectExpression.getNode().getElementType() == com.intellij.psi.TokenType.WHITE_SPACE) {
                objectExpression = objectExpression.getPrevSibling();
            }
            
            if (objectExpression == null) {
                return null;
            }
            
            // 排除内置模块访问
            String objectText = objectExpression.getText();
            if (isBuiltinModule(objectText)) {
                return null;
            }
            
            // 推断对象类型
            String objectType = inferObjectType(objectExpression);
            
            return new ExtensionMethodContext(objectText, objectType);
        }
        
        /**
         * 推断对象类型
         */
        private String inferObjectType(PsiElement objectExpression) {
            String objectText = objectExpression.getText();
            
            // 1. 字面量类型推断
            if (objectText.startsWith("\"") && objectText.endsWith("\"")) {
                return "String";
            }
            if (objectText.matches("\\d+")) {
                return "Integer";
            }
            if (objectText.matches("\\d+\\.\\d+")) {
                return "Double";
            }
            if ("true".equals(objectText) || "false".equals(objectText)) {
                return "Boolean";
            }
            if ("null".equals(objectText)) {
                return "Object";
            }
            
            // 2. 数组字面量
            if (objectText.startsWith("[") && objectText.endsWith("]")) {
                return "Array";
            }
            
            // 3. 对象字面量
            if (objectText.startsWith("{") && objectText.endsWith("}")) {
                return "Map";
            }
            
            // 4. 函数调用结果类型推断
            if (objectText.contains("(") && objectText.contains(")")) {
                return inferFunctionReturnType(objectText);
            }
            
            // 5. 变量类型推断（基于命名约定）
            return inferVariableType(objectText);
        }
        
        /**
         * 推断函数返回类型
         */
        private String inferFunctionReturnType(String functionCall) {
            // db.select() 返回 Array
            if (functionCall.contains("db.select") || functionCall.contains("db.query")) {
                return "Array";
            }
            
            // db.selectOne() 返回 Object
            if (functionCall.contains("db.selectOne") || functionCall.contains("db.findOne")) {
                return "Object";
            }
            
            // db.count() 返回 Number
            if (functionCall.contains("db.count") || functionCall.contains("db.selectInt")) {
                return "Number";
            }
            
            // http.get() 等返回 HttpResponse
            if (functionCall.contains("http.get") || functionCall.contains("http.post")) {
                return "HttpResponse";
            }
            
            // 字符串处理函数
            if (functionCall.contains("concat") || functionCall.contains("format") || 
                functionCall.contains("uuid") || functionCall.contains("md5")) {
                return "String";
            }
            
            // 数组处理函数
            if (functionCall.contains("new_array") || functionCall.contains("new_list")) {
                return "Array";
            }
            
            // Map处理函数
            if (functionCall.contains("new_map") || functionCall.contains("new_hash")) {
                return "Map";
            }
            
            return "Object";
        }
        
        /**
         * 基于命名约定推断变量类型
         */
        private String inferVariableType(String variableName) {
            // 基于命名约定推断
            String lowerName = variableName.toLowerCase();
            
            if (lowerName.contains("str") || lowerName.contains("name") || 
                lowerName.contains("text") || lowerName.contains("message")) {
                return "String";
            }
            
            if (lowerName.contains("list") || lowerName.contains("array") || 
                lowerName.contains("items") || lowerName.endsWith("s")) {
                return "Array";
            }
            
            if (lowerName.contains("map") || lowerName.contains("dict") || 
                lowerName.contains("config") || lowerName.contains("params")) {
                return "Map";
            }
            
            if (lowerName.contains("count") || lowerName.contains("num") || 
                lowerName.contains("size") || lowerName.contains("id")) {
                return "Number";
            }
            
            if (lowerName.contains("date") || lowerName.contains("time") || 
                lowerName.contains("timestamp")) {
                return "Date";
            }
            
            if (lowerName.contains("flag") || lowerName.contains("is") || 
                lowerName.contains("has") || lowerName.contains("can")) {
                return "Boolean";
            }
            
            return "Object";
        }
        
        /**
         * 根据对象值推断可能的扩展方法
         */
        private void addInferredExtensionMethods(@NotNull CompletionResultSet result, String objectValue) {
            // 如果对象值看起来像字符串，添加字符串方法
            if (objectValue.contains("str") || objectValue.contains("text")) {
                addStringExtensionMethods(result);
            }
            
            // 如果对象值看起来像数组，添加数组方法
            if (objectValue.contains("list") || objectValue.contains("array")) {
                addArrayExtensionMethods(result);
            }
            
            // 如果对象值看起来像数字，添加数字方法
            if (objectValue.contains("num") || objectValue.contains("count")) {
                addNumberExtensionMethods(result);
            }
        }
        
        /**
         * 检查是否为内置模块
         */
        private boolean isBuiltinModule(String name) {
            return "db".equals(name) || "http".equals(name) || "request".equals(name) || 
                   "response".equals(name) || "env".equals(name) || "log".equals(name) ||
                   "magic".equals(name);
        }
        
        private void addStringExtensionMethods(@NotNull CompletionResultSet result) {
            String[][] methods = {
                {"isBlank", "Boolean", "()", "判断字符串是否为空或只包含空白字符"},
                {"isNotBlank", "Boolean", "()", "判断字符串是否不为空且包含非空白字符"},
                {"length", "Integer", "()", "获取字符串长度"},
                {"substring", "String", "(start, end?)", "截取子字符串"},
                {"indexOf", "Integer", "(searchValue)", "查找子字符串位置"},
                {"lastIndexOf", "Integer", "(searchValue)", "查找子字符串最后出现位置"},
                {"replace", "String", "(searchValue, replaceValue)", "替换字符串"},
                {"replaceAll", "String", "(regex, replacement)", "正则替换"},
                {"split", "Array", "(separator)", "分割字符串为数组"},
                {"trim", "String", "()", "去除首尾空白字符"},
                {"toLowerCase", "String", "()", "转换为小写"},
                {"toUpperCase", "String", "()", "转换为大写"},
                {"startsWith", "Boolean", "(prefix)", "检查是否以指定前缀开始"},
                {"endsWith", "Boolean", "(suffix)", "检查是否以指定后缀结束"},
                {"contains", "Boolean", "(substring)", "检查是否包含子字符串"},
                {"charAt", "String", "(index)", "获取指定位置的字符"},
                {"repeat", "String", "(count)", "重复字符串指定次数"},
                {"padStart", "String", "(targetLength, padString?)", "在开头填充字符"},
                {"padEnd", "String", "(targetLength, padString?)", "在末尾填充字符"}
            };
            
            for (String[] method : methods) {
                result.addElement(
                    LookupElementBuilder.create(method[0])
                        .withIcon(MagicScriptIcons.METHOD)
                        .withTypeText(method[1])
                        .withTailText(method[2] + " - " + method[3])
                        .withInsertHandler(new MethodInsertHandler(new ApiMethod(method[0], method[1], method[3])))
                );
            }
        }
        
        private void addNumberExtensionMethods(@NotNull CompletionResultSet result) {
            String[][] methods = {
                {"round", "Number", "()", "四舍五入"},
                {"floor", "Number", "()", "向下取整"},
                {"ceil", "Number", "()", "向上取整"},
                {"abs", "Number", "()", "绝对值"},
                {"toFixed", "String", "(digits)", "保留指定小数位数"},
                {"asPercent", "String", "()", "转换为百分比格式"},
                {"max", "Number", "(other)", "取较大值"},
                {"min", "Number", "(other)", "取较小值"},
                {"pow", "Number", "(exponent)", "幂运算"},
                {"sqrt", "Number", "()", "平方根"},
                {"isFinite", "Boolean", "()", "检查是否为有限数"},
                {"isNaN", "Boolean", "()", "检查是否为NaN"},
                {"toRadians", "Double", "()", "角度转弧度"},
                {"toDegrees", "Double", "()", "弧度转角度"}
            };
            
            for (String[] method : methods) {
                result.addElement(
                    LookupElementBuilder.create(method[0])
                        .withIcon(MagicScriptIcons.METHOD)
                        .withTypeText(method[1])
                        .withTailText(method[2] + " - " + method[3])
                        .withInsertHandler(new MethodInsertHandler(new ApiMethod(method[0], method[1], method[3])))
                );
            }
        }
        
        private void addArrayExtensionMethods(@NotNull CompletionResultSet result) {
            String[][] methods = {
                {"map", "Array", "(function)", "映射数组元素"},
                {"filter", "Array", "(predicate)", "过滤数组元素"},
                {"each", "void", "(function)", "遍历数组元素"},
                {"forEach", "void", "(function)", "遍历数组元素"},
                {"reduce", "Object", "(function, initialValue?)", "归约数组"},
                {"sort", "Array", "(comparator?)", "排序数组"},
                {"reverse", "Array", "()", "反转数组"},
                {"first", "Object", "()", "获取第一个元素"},
                {"last", "Object", "()", "获取最后一个元素"},
                {"get", "Object", "(index)", "获取指定位置元素"},
                {"size", "Integer", "()", "获取数组大小"},
                {"length", "Integer", "()", "获取数组长度"},
                {"isEmpty", "Boolean", "()", "检查是否为空"},
                {"isNotEmpty", "Boolean", "()", "检查是否不为空"},
                {"contains", "Boolean", "(element)", "检查是否包含元素"},
                {"indexOf", "Integer", "(element)", "查找元素位置"},
                {"lastIndexOf", "Integer", "(element)", "查找元素最后位置"},
                {"join", "String", "(separator?)", "连接数组元素为字符串"},
                {"push", "Array", "(element)", "添加元素到末尾"},
                {"pop", "Object", "()", "移除并返回最后一个元素"},
                {"shift", "Object", "()", "移除并返回第一个元素"},
                {"unshift", "Array", "(element)", "添加元素到开头"},
                {"slice", "Array", "(start, end?)", "截取数组片段"},
                {"concat", "Array", "(other)", "连接数组"},
                {"distinct", "Array", "()", "去重"},
                {"flatten", "Array", "()", "展平嵌套数组"}
            };
            
            for (String[] method : methods) {
                result.addElement(
                    LookupElementBuilder.create(method[0])
                        .withIcon(MagicScriptIcons.METHOD)
                        .withTypeText(method[1])
                        .withTailText(method[2] + " - " + method[3])
                        .withInsertHandler(new MethodInsertHandler(new ApiMethod(method[0], method[1], method[3])))
                );
            }
        }
        
        private void addMapExtensionMethods(@NotNull CompletionResultSet result) {
            String[][] methods = {
                {"get", "Object", "(key)", "获取键对应的值"},
                {"put", "Object", "(key, value)", "设置键值对"},
                {"remove", "Object", "(key)", "移除指定键"},
                {"containsKey", "Boolean", "(key)", "检查是否包含键"},
                {"containsValue", "Boolean", "(value)", "检查是否包含值"},
                {"isEmpty", "Boolean", "()", "检查是否为空"},
                {"isNotEmpty", "Boolean", "()", "检查是否不为空"},
                {"size", "Integer", "()", "获取键值对数量"},
                {"keys", "Array", "()", "获取所有键"},
                {"values", "Array", "()", "获取所有值"},
                {"entries", "Array", "()", "获取所有键值对"},
                {"clear", "void", "()", "清空Map"},
                {"merge", "Map", "(other)", "合并其他Map"},
                {"forEach", "void", "(function)", "遍历键值对"},
                {"filter", "Map", "(predicate)", "过滤键值对"},
                {"map", "Map", "(function)", "映射键值对"}
            };
            
            for (String[] method : methods) {
                result.addElement(
                    LookupElementBuilder.create(method[0])
                        .withIcon(MagicScriptIcons.METHOD)
                        .withTypeText(method[1])
                        .withTailText(method[2] + " - " + method[3])
                        .withInsertHandler(new MethodInsertHandler(new ApiMethod(method[0], method[1], method[3])))
                );
            }
        }
        
        private void addDateExtensionMethods(@NotNull CompletionResultSet result) {
            String[][] methods = {
                {"format", "String", "(pattern)", "格式化日期"},
                {"addDays", "Date", "(days)", "增加天数"},
                {"addHours", "Date", "(hours)", "增加小时"},
                {"addMinutes", "Date", "(minutes)", "增加分钟"},
                {"addMonths", "Date", "(months)", "增加月数"},
                {"addYears", "Date", "(years)", "增加年数"},
                {"getYear", "Integer", "()", "获取年份"},
                {"getMonth", "Integer", "()", "获取月份"},
                {"getDay", "Integer", "()", "获取日期"},
                {"getHour", "Integer", "()", "获取小时"},
                {"getMinute", "Integer", "()", "获取分钟"},
                {"getSecond", "Integer", "()", "获取秒数"},
                {"getTime", "Long", "()", "获取时间戳"},
                {"toISOString", "String", "()", "转换为ISO字符串"},
                {"toDateString", "String", "()", "转换为日期字符串"},
                {"toTimeString", "String", "()", "转换为时间字符串"},
                {"isAfter", "Boolean", "(other)", "检查是否在指定日期之后"},
                {"isBefore", "Boolean", "(other)", "检查是否在指定日期之前"},
                {"isSameDay", "Boolean", "(other)", "检查是否为同一天"},
                {"diffDays", "Long", "(other)", "计算天数差"},
                {"diffHours", "Long", "(other)", "计算小时差"},
                {"diffMinutes", "Long", "(other)", "计算分钟差"}
            };
            
            for (String[] method : methods) {
                result.addElement(
                    LookupElementBuilder.create(method[0])
                        .withIcon(MagicScriptIcons.METHOD)
                        .withTypeText(method[1])
                        .withTailText(method[2] + " - " + method[3])
                        .withInsertHandler(new MethodInsertHandler(new ApiMethod(method[0], method[1], method[3])))
                );
            }
        }
        
        private void addBooleanExtensionMethods(@NotNull CompletionResultSet result) {
            String[][] methods = {
                {"and", "Boolean", "(other)", "逻辑与操作"},
                {"or", "Boolean", "(other)", "逻辑或操作"},
                {"not", "Boolean", "()", "逻辑非操作"},
                {"xor", "Boolean", "(other)", "逻辑异或操作"},
                {"toString", "String", "()", "转换为字符串"},
                {"equals", "Boolean", "(other)", "比较相等性"}
            };
            
            for (String[] method : methods) {
                result.addElement(
                    LookupElementBuilder.create(method[0])
                        .withIcon(MagicScriptIcons.METHOD)
                        .withTypeText(method[1])
                        .withTailText(method[2] + " - " + method[3])
                        .withInsertHandler(new MethodInsertHandler(new ApiMethod(method[0], method[1], method[3])))
                );
            }
        }
        
        private void addObjectExtensionMethods(@NotNull CompletionResultSet result) {
            String[][] methods = {
                {"asInt", "Integer", "()", "转换为整数"},
                {"asDouble", "Double", "()", "转换为双精度浮点数"},
                {"asFloat", "Float", "()", "转换为单精度浮点数"},
                {"asLong", "Long", "()", "转换为长整数"},
                {"asString", "String", "()", "转换为字符串"},
                {"asBoolean", "Boolean", "()", "转换为布尔值"},
                {"toString", "String", "()", "转换为字符串"},
                {"is", "Boolean", "(type)", "检查是否为指定类型"},
                {"isString", "Boolean", "()", "检查是否为字符串"},
                {"isNumber", "Boolean", "()", "检查是否为数字"},
                {"isBoolean", "Boolean", "()", "检查是否为布尔值"},
                {"isArray", "Boolean", "()", "检查是否为数组"},
                {"isMap", "Boolean", "()", "检查是否为Map"},
                {"isDate", "Boolean", "()", "检查是否为日期"},
                {"isNull", "Boolean", "()", "检查是否为null"},
                {"isNotNull", "Boolean", "()", "检查是否不为null"},
                {"isEmpty", "Boolean", "()", "检查是否为空"},
                {"isNotEmpty", "Boolean", "()", "检查是否不为空"},
                {"equals", "Boolean", "(other)", "比较相等性"},
                {"hashCode", "Integer", "()", "获取哈希码"},
                {"getClass", "String", "()", "获取类型名称"},
                {"clone", "Object", "()", "克隆对象"}
            };
            
            for (String[] method : methods) {
                result.addElement(
                    LookupElementBuilder.create(method[0])
                        .withIcon(MagicScriptIcons.METHOD)
                        .withTypeText(method[1])
                        .withTailText(method[2] + " - " + method[3])
                        .withInsertHandler(new MethodInsertHandler(new ApiMethod(method[0], method[1], method[3])))
                );
            }
        }
        
        private void addCommonExtensionMethods(@NotNull CompletionResultSet result) {
            // 所有对象都有的通用方法
            String[][] methods = {
                {"toString", "String", "()", "转换为字符串"},
                {"equals", "Boolean", "(other)", "比较相等性"},
                {"isNull", "Boolean", "()", "检查是否为null"},
                {"isNotNull", "Boolean", "()", "检查是否不为null"}
            };
            
            for (String[] method : methods) {
                result.addElement(
                    LookupElementBuilder.create(method[0])
                        .withIcon(MagicScriptIcons.METHOD)
                        .withTypeText(method[1])
                        .withTailText(method[2] + " - " + method[3])
                        .withInsertHandler(new MethodInsertHandler(new ApiMethod(method[0], method[1], method[3])))
                );
            }
        }
        
        /**
         * 扩展方法上下文信息
         */
        private static class ExtensionMethodContext {
            private final String objectValue;
            private final String objectType;
            
            public ExtensionMethodContext(String objectValue, String objectType) {
                this.objectValue = objectValue;
                this.objectType = objectType;
            }
            
            public String getObjectValue() {
                return objectValue;
            }
            
            public String getObjectType() {
                return objectType;
            }
        }
    }
    
    /**
     * 参数补全提供器
     * 在函数参数位置提供智能提示
     */
    private static class ParameterCompletionProvider extends CompletionProvider<CompletionParameters> {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                    @NotNull ProcessingContext context,
                                    @NotNull CompletionResultSet result) {
            
            PsiElement element = parameters.getPosition();
            CompletionContext completionContext = CompletionContextAnalyzer.analyzeContext(element);
            
            // 获取当前参数位置和函数签名
            if (completionContext.isParameterPosition()) {
                String functionName = completionContext.getFunctionName();
                int parameterIndex = completionContext.getParameterIndex();
                
                // 根据函数名提供参数提示
                addParameterCompletions(result, functionName, parameterIndex);
                
                // 添加常用的参数值
                addCommonParameterValues(result, parameterIndex);
            }
        }
        
        private void addParameterCompletions(@NotNull CompletionResultSet result, String functionName, int parameterIndex) {
            // 根据具体函数提供参数建议
            if ("db.select".equals(functionName)) {
                if (parameterIndex == 0) {
                    // SQL语句参数
                    result.addElement(
                        LookupElementBuilder.create("\"SELECT * FROM table\"")
                            .withIcon(MagicScriptIcons.PARAMETER)
                            .withTypeText("String")
                            .withTailText(" - SQL查询语句")
                    );
                }
            } else if ("http.get".equals(functionName)) {
                if (parameterIndex == 0) {
                    // URL参数
                    result.addElement(
                        LookupElementBuilder.create("\"https://api.example.com\"")
                            .withIcon(MagicScriptIcons.PARAMETER)
                            .withTypeText("String")
                            .withTailText(" - 请求URL")
                    );
                }
            }
        }
        
        private void addCommonParameterValues(@NotNull CompletionResultSet result, int parameterIndex) {
            // 常用的参数值
            result.addElement(
                LookupElementBuilder.create("null")
                    .withIcon(MagicScriptIcons.KEYWORD)
                    .withTypeText("null")
                    .withTailText(" - 空值")
            );
            
            result.addElement(
                LookupElementBuilder.create("true")
                    .withIcon(MagicScriptIcons.KEYWORD)
                    .withTypeText("Boolean")
                    .withTailText(" - 布尔真值")
            );
            
            result.addElement(
                LookupElementBuilder.create("false")
                    .withIcon(MagicScriptIcons.KEYWORD)
                    .withTypeText("Boolean")
                    .withTailText(" - 布尔假值")
            );
        }
    }
    
    /**
     * import语句补全提供器
     */
    private static class ImportCompletionProvider extends CompletionProvider<CompletionParameters> {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                    @NotNull ProcessingContext context,
                                    @NotNull CompletionResultSet result) {
            
            // 提供可导入的模块列表
            addImportableModules(result);
            
            // 提供相对路径导入
            addRelativeImports(result);
        }
        
        private void addImportableModules(@NotNull CompletionResultSet result) {
            // 可导入的内置模块
            String[] modules = {"db", "http", "request", "response", "env", "log", "magic"};
            for (String module : modules) {
                result.addElement(
                    LookupElementBuilder.create("\"/builtin/" + module + "\"")
                        .withIcon(MagicScriptIcons.MODULE)
                        .withTypeText("builtin module")
                        .withTailText(" - 内置" + module + "模块")
                );
            }
            
            // 常用的第三方模块
            result.addElement(
                LookupElementBuilder.create("\"/common/utils\"")
                    .withIcon(MagicScriptIcons.MODULE)
                    .withTypeText("module")
                    .withTailText(" - 通用工具模块")
            );
        }
        
        private void addRelativeImports(@NotNull CompletionResultSet result) {
            // 相对路径导入示例
            result.addElement(
                LookupElementBuilder.create("\"./utils\"")
                    .withIcon(MagicScriptIcons.MODULE)
                    .withTypeText("relative module")
                    .withTailText(" - 相对路径导入")
            );
            
            result.addElement(
                LookupElementBuilder.create("\"../common/helper\"")
                    .withIcon(MagicScriptIcons.MODULE)
                    .withTypeText("relative module")
                    .withTailText(" - 上级目录导入")
            );
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