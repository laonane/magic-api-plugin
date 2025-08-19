package com.magicapi.idea.completion.context;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.tree.IElementType;
import com.magicapi.idea.lang.psi.MSTypes;
import com.magicapi.idea.lang.psi.MSVarDeclaration;
import com.magicapi.idea.lang.psi.MSFunctionDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 代码补全上下文分析器 (增强版)
 * 使用PsiTreeUtil进行精确的PSI树遍历，支持复杂表达式解析
 */
public class CompletionContextAnalyzer {
    
    /**
     * 分析补全上下文 (增强版)
     * @param element 当前PSI元素
     * @return 补全上下文信息
     */
    @NotNull
    public static CompletionContext analyzeContext(@NotNull PsiElement element) {
        // 1. 分析链式调用上下文 (优先级最高)
        CompletionContext chainContext = analyzeChainedCallContext(element);
        if (chainContext != null) {
            return chainContext;
        }
        
        // 2. 分析成员访问表达式 (如 db.select)
        CompletionContext memberContext = analyzeMemberAccessContext(element);
        if (memberContext != null) {
            return memberContext;
        }
        
        // 3. 分析函数参数上下文
        CompletionContext paramContext = analyzeFunctionParameterContext(element);
        if (paramContext != null) {
            return paramContext;
        }
        
        // 4. 分析import语句上下文
        CompletionContext importContext = analyzeImportContext(element);
        if (importContext != null) {
            return importContext;
        }
        
        // 5. 分析变量声明上下文
        CompletionContext varContext = analyzeVariableContext(element);
        if (varContext != null) {
            return varContext;
        }
        
        // 6. 默认为表达式上下文
        return new CompletionContext(CompletionContext.Type.EXPRESSION, null, null);
    }
    
    /**
     * 分析链式调用上下文 (如 db.select().map())
     * 使用PSI树遍历检查链式调用模式
     */
    @Nullable
    private static CompletionContext analyzeChainedCallContext(@NotNull PsiElement element) {
        // 检查是否在点号后面
        if (!isAfterDot(element)) {
            return null;
        }
        
        // 检查前面是否有方法调用（包含括号）
        if (!hasMethodCallBefore(element)) {
            return null;
        }
        
        // 分析链式调用的前一个方法
        ChainAnalysisResult chainResult = analyzeChainStructure(element);
        if (chainResult == null) {
            return null;
        }
        
        return new CompletionContext(
            CompletionContext.Type.MEMBER_ACCESS,
            chainResult.getBaseObject(),
            chainResult.getReturnType(),
            null,
            0,
            chainResult.getReturnType()
        );
    }
    
    /**
     * 分析成员访问上下文 (如 obj.method)
     * 使用PsiTreeUtil进行精确匹配
     */
    @Nullable
    private static CompletionContext analyzeMemberAccessContext(@NotNull PsiElement element) {
        // 检查是否在点号后面
        if (!isAfterDot(element)) {
            return null;
        }
        
        // 获取点号前的限定符
        PsiElement qualifier = getQualifierBeforeDot(element);
        if (qualifier == null) {
            return null;
        }
        
        // 推断限定符类型
        String qualifierText = qualifier.getText();
        String qualifierType = inferQualifierType(qualifier);
        
        return new CompletionContext(
            CompletionContext.Type.MEMBER_ACCESS,
            qualifierText,
            qualifierType
        );
    }
    
    /**
     * 分析函数参数上下文
     * 使用PsiTreeUtil找到函数调用和参数位置
     */
    @Nullable
    private static CompletionContext analyzeFunctionParameterContext(@NotNull PsiElement element) {
        // 查找最近的函数调用
        PsiElement functionCall = findEnclosingFunctionCall(element);
        if (functionCall == null) {
            return null;
        }
        
        // 获取函数名
        String functionName = extractFunctionName(functionCall);
        if (functionName == null) {
            return null;
        }
        
        // 计算参数位置
        int parameterIndex = calculateParameterIndex(element, functionCall);
        
        return new CompletionContext(
            CompletionContext.Type.FUNCTION_PARAMETER,
            null,
            null,
            functionName,
            parameterIndex,
            null
        );
    }
    
    /**
     * 分析import语句上下文
     */
    @Nullable
    private static CompletionContext analyzeImportContext(@NotNull PsiElement element) {
        // 检查是否在import关键字后面
        if (isAfterKeyword(element, "import")) {
            return new CompletionContext(CompletionContext.Type.IMPORT_STATEMENT, null, null);
        }
        
        return null;
    }
    
    /**
     * 分析变量声明上下文
     */
    @Nullable
    private static CompletionContext analyzeVariableContext(@NotNull PsiElement element) {
        // 使用PsiTreeUtil查找变量声明
        MSVarDeclaration varDeclaration = PsiTreeUtil.getParentOfType(element, MSVarDeclaration.class);
        if (varDeclaration != null) {
            return new CompletionContext(CompletionContext.Type.VARIABLE_DECLARATION, null, null);
        }
        
        return null;
    }
    
    // ==================== 辅助方法 ====================
    
    /**
     * 检查元素是否在点号后面
     */
    private static boolean isAfterDot(@NotNull PsiElement element) {
        PsiElement prevSibling = skipWhitespace(element.getPrevSibling());
        return prevSibling != null && prevSibling.getNode().getElementType() == MSTypes.DOT;
    }
    
    /**
     * 检查前面是否有方法调用
     */
    private static boolean hasMethodCallBefore(@NotNull PsiElement element) {
        PsiElement current = element;
        int searchDepth = 10;
        
        while (current != null && searchDepth-- > 0) {
            PsiElement prev = skipWhitespace(current.getPrevSibling());
            
            if (prev != null && prev.getNode().getElementType() == MSTypes.RPAREN) {
                return true;
            }
            
            current = current.getParent();
        }
        
        return false;
    }
    
    /**
     * 获取点号前的限定符
     */
    @Nullable
    private static PsiElement getQualifierBeforeDot(@NotNull PsiElement element) {
        PsiElement dotElement = skipWhitespace(element.getPrevSibling());
        
        if (dotElement != null && dotElement.getNode().getElementType() == MSTypes.DOT) {
            return skipWhitespace(dotElement.getPrevSibling());
        }
        
        return null;
    }
    
    /**
     * 检查元素是否在指定关键字后面
     */
    private static boolean isAfterKeyword(@NotNull PsiElement element, @NotNull String keyword) {
        PsiElement current = element;
        int searchDepth = 5; // 限制搜索深度
        
        while (current != null && searchDepth-- > 0) {
            PsiElement prev = skipWhitespace(current.getPrevSibling());
            
            if (prev != null && keyword.equals(prev.getText())) {
                return true;
            }
            
            current = current.getParent();
        }
        
        return false;
    }
    
    /**
     * 查找包围的函数调用
     */
    @Nullable
    private static PsiElement findEnclosingFunctionCall(@NotNull PsiElement element) {
        PsiElement current = element;
        
        while (current != null) {
            // 简单查找：向上查找直到找到左括号
            if (current.getNode().getElementType() == MSTypes.LPAREN) {
                // 找到前面的标识符（函数名）
                PsiElement functionName = skipWhitespace(current.getPrevSibling());
                
                if (functionName != null && functionName.getNode().getElementType() == MSTypes.IDENTIFIER) {
                    return functionName;
                }
            }
            
            current = current.getParent();
        }
        
        return null;
    }
    
    /**
     * 跳过空白字符
     */
    @Nullable
    private static PsiElement skipWhitespace(@Nullable PsiElement element) {
        while (element != null && element.getNode().getElementType() == com.intellij.psi.TokenType.WHITE_SPACE) {
            element = element.getPrevSibling();
        }
        return element;
    }
    
    /**
     * 提取函数名
     */
    @Nullable
    private static String extractFunctionName(@NotNull PsiElement functionCall) {
        if (functionCall.getNode().getElementType() == MSTypes.IDENTIFIER) {
            return functionCall.getText();
        }
        return null;
    }
    
    /**
     * 计算参数位置索引
     */
    private static int calculateParameterIndex(@NotNull PsiElement element, @NotNull PsiElement functionCall) {
        // 简化实现：计算逗号数量来确定参数位置
        PsiElement current = element;
        int commaCount = 0;
        
        // 向前搜索逗号
        while (current != null && !current.equals(functionCall)) {
            if (current.getNode().getElementType() == MSTypes.COMMA) {
                commaCount++;
            }
            current = PsiTreeUtil.prevLeaf(current);
        }
        
        return commaCount;
    }
    
    /**
     * 分析链式调用结构
     */
    @Nullable
    private static ChainAnalysisResult analyzeChainStructure(@NotNull PsiElement element) {
        // 向前查找链式调用结构
        String baseObject = null;
        String lastMethod = null;
        
        // 从当前位置向前搜索
        PsiElement current = element;
        PsiElement dotElement = skipWhitespace(current.getPrevSibling());
        
        if (dotElement == null || dotElement.getNode().getElementType() != MSTypes.DOT) {
            return null;
        }
        
        // 查找右括号（表示方法调用）
        PsiElement rparenElement = skipWhitespace(dotElement.getPrevSibling());
        if (rparenElement == null || rparenElement.getNode().getElementType() != MSTypes.RPAREN) {
            return null;
        }
        
        // 向前查找匹配的左括号和方法名
        PsiElement searchCurrent = rparenElement;
        int parenCount = 1;
        
        while (searchCurrent != null && parenCount > 0) {
            searchCurrent = skipWhitespace(searchCurrent.getPrevSibling());
            if (searchCurrent == null) break;
            
            IElementType type = searchCurrent.getNode().getElementType();
            if (type == MSTypes.RPAREN) {
                parenCount++;
            } else if (type == MSTypes.LPAREN) {
                parenCount--;
            }
        }
        
        if (parenCount == 0 && searchCurrent != null) {
            // 找到方法名
            PsiElement methodElement = skipWhitespace(searchCurrent.getPrevSibling());
            if (methodElement != null && methodElement.getNode().getElementType() == MSTypes.IDENTIFIER) {
                lastMethod = methodElement.getText();
                
                // 继续向前查找基础对象
                PsiElement dotBeforeMethod = skipWhitespace(methodElement.getPrevSibling());
                if (dotBeforeMethod != null && dotBeforeMethod.getNode().getElementType() == MSTypes.DOT) {
                    PsiElement baseObjectElement = skipWhitespace(dotBeforeMethod.getPrevSibling());
                    if (baseObjectElement != null && baseObjectElement.getNode().getElementType() == MSTypes.IDENTIFIER) {
                        baseObject = baseObjectElement.getText();
                    }
                }
            }
        }
        
        if (baseObject != null && lastMethod != null) {
            String returnType = inferMethodReturnType(baseObject, lastMethod);
            return new ChainAnalysisResult(baseObject, lastMethod, returnType);
        }
        
        return null;
    }
    
    /**
     * 提取方法名
     */
    @Nullable
    private static String extractMethodName(@NotNull PsiElement methodElement) {
        if (methodElement.getNode().getElementType() == MSTypes.IDENTIFIER) {
            return methodElement.getText();
        }
        return null;
    }
    
    /**
     * 推断方法返回类型
     */
    @NotNull
    private static String inferMethodReturnType(@NotNull String baseObject, @NotNull String methodName) {
        // 内置模块方法返回类型推断
        if (isBuiltinModule(baseObject)) {
            return inferBuiltinModuleMethodReturnType(baseObject, methodName);
        }
        
        // 扩展方法返回类型推断
        return inferExtensionMethodReturnType(methodName);
    }
    
    /**
     * 推断内置模块方法返回类型
     */
    @NotNull
    private static String inferBuiltinModuleMethodReturnType(@NotNull String moduleName, @NotNull String methodName) {
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
    @NotNull
    private static String inferDatabaseMethodReturnType(@NotNull String methodName) {
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
    @NotNull
    private static String inferHttpMethodReturnType(@NotNull String methodName) {
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
    @NotNull
    private static String inferExtensionMethodReturnType(@NotNull String methodName) {
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
     * 推断限定符类型 (增强版)
     * 使用PsiTreeUtil进行精确的类型推断
     */
    @NotNull
    private static String inferQualifierType(@NotNull PsiElement qualifier) {
        String qualifierText = qualifier.getText();
        
        // 1. 检查是否为内置模块
        if (isBuiltinModule(qualifierText)) {
            return qualifierText;
        }
        
        // 2. 检查是否为字面量
        String literalType = inferLiteralType(qualifierText);
        if (literalType != null) {
            return literalType;
        }
        
        // 3. 检查是否为函数调用结果
        if (qualifierText.contains("(") && qualifierText.contains(")")) {
            return inferFunctionCallReturnType(qualifier);
        }
        
        // 4. 检查是否为变量引用
        String variableType = inferVariableType(qualifier);
        if (variableType != null) {
            return variableType;
        }
        
        // 5. 基于命名约定推断
        return inferTypeByNamingConvention(qualifierText);
    }
    
    /**
     * 推断字面量类型
     */
    @Nullable
    private static String inferLiteralType(@NotNull String text) {
        if (text.startsWith("\"") && text.endsWith("\"")) {
            return "String";
        }
        if (text.matches("\\d+")) {
            return "Integer";
        }
        if (text.matches("\\d+\\.\\d+")) {
            return "Double";
        }
        if ("true".equals(text) || "false".equals(text)) {
            return "Boolean";
        }
        if ("null".equals(text)) {
            return "Object";
        }
        if (text.startsWith("[") && text.endsWith("]")) {
            return "Array";
        }
        if (text.startsWith("{") && text.endsWith("}")) {
            return "Map";
        }
        return null;
    }
    
    /**
     * 推断函数调用返回类型
     */
    @NotNull
    private static String inferFunctionCallReturnType(@NotNull PsiElement functionCallElement) {
        String functionCallText = functionCallElement.getText();
        
        // 数据库相关函数
        if (functionCallText.contains("db.select") || functionCallText.contains("db.query")) {
            return "Array";
        }
        if (functionCallText.contains("db.selectOne") || functionCallText.contains("db.findOne")) {
            return "Object";
        }
        if (functionCallText.contains("db.count") || functionCallText.contains("db.selectInt")) {
            return "Number";
        }
        
        // HTTP相关函数
        if (functionCallText.contains("http.get") || functionCallText.contains("http.post")) {
            return "HttpResponse";
        }
        
        // 字符串函数
        if (functionCallText.contains("concat") || functionCallText.contains("format") || 
            functionCallText.contains("uuid") || functionCallText.contains("md5")) {
            return "String";
        }
        
        // 数组构造函数
        if (functionCallText.contains("new_array") || functionCallText.contains("new_list")) {
            return "Array";
        }
        
        // Map构造函数
        if (functionCallText.contains("new_map") || functionCallText.contains("new_hash")) {
            return "Map";
        }
        
        return "Object";
    }
    
    /**
     * 推断变量类型
     * 通过查找变量声明或赋值语句
     */
    @Nullable
    private static String inferVariableType(@NotNull PsiElement variable) {
        // 使用PsiTreeUtil查找变量声明
        MSVarDeclaration varDeclaration = findVariableDeclaration(variable.getText(), variable);
        
        if (varDeclaration != null) {
            // 分析变量的初始化表达式
            return analyzeInitializerType(varDeclaration);
        }
        
        return null;
    }
    
    /**
     * 查找变量声明
     */
    @Nullable
    private static MSVarDeclaration findVariableDeclaration(@NotNull String variableName, @NotNull PsiElement context) {
        // 向上查找作用域中的变量声明
        PsiElement current = context;
        
        while (current != null) {
            // 查找同级或父级作用域中的变量声明
            List<MSVarDeclaration> declarations = PsiTreeUtil.getChildrenOfTypeAsList(current, MSVarDeclaration.class);
            
            for (MSVarDeclaration declaration : declarations) {
                if (variableName.equals(declaration.getName())) {
                    return declaration;
                }
            }
            
            // 查找函数参数
            MSFunctionDeclaration functionDecl = PsiTreeUtil.getParentOfType(current, MSFunctionDeclaration.class);
            if (functionDecl != null) {
                // 检查函数参数中是否有同名变量
                // 这里简化处理，实际实现需要解析函数参数
            }
            
            current = current.getParent();
        }
        
        return null;
    }
    
    /**
     * 分析初始化表达式类型
     */
    @Nullable
    private static String analyzeInitializerType(@NotNull MSVarDeclaration varDeclaration) {
        // 这里需要分析变量的初始化表达式
        // 简化实现：通过变量名推断
        String varName = varDeclaration.getName();
        if (varName != null) {
            return inferTypeByNamingConvention(varName);
        }
        
        return null;
    }
    
    /**
     * 基于命名约定推断类型
     */
    @NotNull
    private static String inferTypeByNamingConvention(@NotNull String name) {
        String lowerName = name.toLowerCase();
        
        if (lowerName.contains("str") || lowerName.contains("name") || 
            lowerName.contains("text") || lowerName.contains("message") ||
            lowerName.contains("desc") || lowerName.contains("title")) {
            return "String";
        }
        
        if (lowerName.contains("list") || lowerName.contains("array") || 
            lowerName.contains("items") || lowerName.endsWith("s")) {
            return "Array";
        }
        
        if (lowerName.contains("map") || lowerName.contains("dict") || 
            lowerName.contains("config") || lowerName.contains("params") ||
            lowerName.contains("props") || lowerName.contains("attrs")) {
            return "Map";
        }
        
        if (lowerName.contains("count") || lowerName.contains("num") || 
            lowerName.contains("size") || lowerName.contains("id") ||
            lowerName.contains("index") || lowerName.contains("length")) {
            return "Number";
        }
        
        if (lowerName.contains("date") || lowerName.contains("time") || 
            lowerName.contains("timestamp") || lowerName.contains("created") ||
            lowerName.contains("updated") || lowerName.contains("modified")) {
            return "Date";
        }
        
        if (lowerName.contains("flag") || lowerName.contains("is") || 
            lowerName.contains("has") || lowerName.contains("can") ||
            lowerName.contains("should") || lowerName.contains("enable")) {
            return "Boolean";
        }
        
        return "Object";
    }
    
    /**
     * 检查是否为内置模块
     */
    private static boolean isBuiltinModule(@NotNull String name) {
        return name.equals("db") || name.equals("http") || name.equals("request") || 
               name.equals("response") || name.equals("env") || name.equals("log") ||
               name.equals("magic");
    }
    
    /**
     * 链式调用分析结果
     */
    private static class ChainAnalysisResult {
        private final String baseObject;
        private final String methodName;
        private final String returnType;
        
        public ChainAnalysisResult(@NotNull String baseObject, @NotNull String methodName, @NotNull String returnType) {
            this.baseObject = baseObject;
            this.methodName = methodName;
            this.returnType = returnType;
        }
        
        @NotNull
        public String getBaseObject() {
            return baseObject;
        }
        
        @NotNull
        public String getMethodName() {
            return methodName;
        }
        
        @NotNull
        public String getReturnType() {
            return returnType;
        }
    }
}