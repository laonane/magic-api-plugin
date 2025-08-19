package com.magicapi.idea.completion.context;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.magicapi.idea.lang.psi.MSTypes;
import com.magicapi.idea.lang.psi.MSVarDeclaration;
import com.magicapi.idea.lang.psi.MSFunctionDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 类型推断引擎
 * 支持链式调用类型传播、变量类型跟踪、函数返回类型分析
 */
public class TypeInferenceEngine {
    
    // 类型缓存，避免重复计算
    private static final Map<String, String> TYPE_CACHE = new HashMap<>();
    
    // 链式调用类型传播缓存
    private static final Map<String, String> CHAIN_TYPE_CACHE = new HashMap<>();
    
    /**
     * 推断表达式类型
     * @param element PSI元素
     * @return 推断出的类型
     */
    @NotNull
    public static String inferExpressionType(@NotNull PsiElement element) {
        String elementText = element.getText();
        
        // 检查缓存
        String cachedType = TYPE_CACHE.get(elementText);
        if (cachedType != null) {
            return cachedType;
        }
        
        String inferredType = performTypeInference(element);
        
        // 缓存结果
        TYPE_CACHE.put(elementText, inferredType);
        
        return inferredType;
    }
    
    /**
     * 执行类型推断
     */
    @NotNull
    private static String performTypeInference(@NotNull PsiElement element) {
        String elementText = element.getText();
        
        // 1. 字面量类型推断
        String literalType = inferLiteralType(elementText);
        if (literalType != null) {
            return literalType;
        }
        
        // 2. 内置模块类型
        if (isBuiltinModule(elementText)) {
            return elementText;
        }
        
        // 3. 函数调用结果类型推断
        if (isFunctionCall(element)) {
            return inferFunctionCallType(element);
        }
        
        // 4. 变量类型推断
        String variableType = inferVariableType(element);
        if (variableType != null) {
            return variableType;
        }
        
        // 5. 成员访问类型推断
        String memberAccessType = inferMemberAccessType(element);
        if (memberAccessType != null) {
            return memberAccessType;
        }
        
        // 6. 基于命名约定推断
        return inferTypeByNamingConvention(elementText);
    }
    
    /**
     * 推断链式调用的完整类型传播
     * @param chainExpression 链式调用表达式
     * @return 最终的返回类型
     */
    @NotNull
    public static String inferChainCallType(@NotNull PsiElement chainExpression) {
        String chainText = chainExpression.getText();
        
        // 检查缓存
        String cachedType = CHAIN_TYPE_CACHE.get(chainText);
        if (cachedType != null) {
            return cachedType;
        }
        
        String finalType = performChainTypeInference(chainExpression);
        
        // 缓存结果
        CHAIN_TYPE_CACHE.put(chainText, finalType);
        
        return finalType;
    }
    
    /**
     * 执行链式调用类型推断
     */
    @NotNull
    private static String performChainTypeInference(@NotNull PsiElement chainExpression) {
        List<MethodCall> methodCalls = parseChainCalls(chainExpression);
        
        if (methodCalls.isEmpty()) {
            return "Object";
        }
        
        // 从第一个调用开始逐步推断类型
        String currentType = methodCalls.get(0).getBaseType();
        
        for (int i = 1; i < methodCalls.size(); i++) {
            MethodCall methodCall = methodCalls.get(i);
            currentType = inferMethodReturnType(currentType, methodCall.getMethodName());
        }
        
        return currentType;
    }
    
    /**
     * 解析链式调用
     */
    @NotNull
    private static List<MethodCall> parseChainCalls(@NotNull PsiElement chainExpression) {
        List<MethodCall> methodCalls = new ArrayList<>();
        String chainText = chainExpression.getText();
        
        // 简化实现：基于文本解析
        // 实际实现应该基于PSI树结构
        String[] parts = chainText.split("\\.");
        
        if (parts.length >= 2) {
            String baseObject = parts[0];
            String baseType = inferExpressionType(chainExpression.getFirstChild());
            
            methodCalls.add(new MethodCall(baseObject, baseType));
            
            for (int i = 1; i < parts.length; i++) {
                String part = parts[i];
                String methodName = extractMethodName(part);
                if (methodName != null) {
                    methodCalls.add(new MethodCall(methodName, "unknown"));
                }
            }
        }
        
        return methodCalls;
    }
    
    /**
     * 推断字面量类型
     */
    @Nullable
    private static String inferLiteralType(@NotNull String text) {
        // 字符串字面量
        if (text.startsWith("\"") && text.endsWith("\"") || 
            text.startsWith("'") && text.endsWith("'")) {
            return "String";
        }
        
        // 数字字面量
        if (text.matches("\\d+")) {
            return "Integer";
        }
        if (text.matches("\\d+\\.\\d+")) {
            return "Double";
        }
        if (text.matches("\\d+L") || text.matches("\\d+l")) {
            return "Long";
        }
        if (text.matches("\\d+\\.\\d+f") || text.matches("\\d+\\.\\d+F")) {
            return "Float";
        }
        
        // 布尔字面量
        if ("true".equals(text) || "false".equals(text)) {
            return "Boolean";
        }
        
        // null字面量
        if ("null".equals(text)) {
            return "Object";
        }
        
        // 数组字面量
        if (text.startsWith("[") && text.endsWith("]")) {
            return "Array";
        }
        
        // 对象字面量
        if (text.startsWith("{") && text.endsWith("}")) {
            return "Map";
        }
        
        return null;
    }
    
    /**
     * 推断函数调用类型
     */
    @NotNull
    private static String inferFunctionCallType(@NotNull PsiElement functionCall) {
        String functionText = functionCall.getText();
        
        // 数据库函数
        if (functionText.contains("db.select") || functionText.contains("db.query")) {
            return "Array";
        }
        if (functionText.contains("db.selectOne") || functionText.contains("db.findOne")) {
            return "Object";
        }
        if (functionText.contains("db.selectInt") || functionText.contains("db.count")) {
            return "Integer";
        }
        if (functionText.contains("db.selectLong")) {
            return "Long";
        }
        if (functionText.contains("db.selectDouble")) {
            return "Double";
        }
        if (functionText.contains("db.selectValue")) {
            return "Object";
        }
        if (functionText.contains("db.page")) {
            return "PageResult";
        }
        
        // HTTP函数
        if (functionText.contains("http.get") || functionText.contains("http.post") ||
            functionText.contains("http.put") || functionText.contains("http.delete") ||
            functionText.contains("http.patch")) {
            return "HttpResponse";
        }
        
        // 字符串函数
        if (functionText.contains("uuid") || functionText.contains("concat") || 
            functionText.contains("format") || functionText.contains("md5") ||
            functionText.contains("sha1") || functionText.contains("base64_encode") ||
            functionText.contains("base64_decode")) {
            return "String";
        }
        
        // 数学函数
        if (functionText.contains("round") || functionText.contains("floor") || 
            functionText.contains("ceil") || functionText.contains("abs") ||
            functionText.contains("max") || functionText.contains("min") ||
            functionText.contains("sum") || functionText.contains("avg")) {
            return "Number";
        }
        
        // 日期函数
        if (functionText.contains("now") || functionText.contains("current_timestamp")) {
            return "Long";
        }
        if (functionText.contains("current_date") || functionText.contains("current_time") ||
            functionText.contains("date_format")) {
            return "String";
        }
        if (functionText.contains("parse_date") || functionText.contains("add_days") ||
            functionText.contains("add_months")) {
            return "Date";
        }
        
        // 数组构造函数
        if (functionText.contains("new_array") || functionText.contains("new_list")) {
            return "Array";
        }
        if (functionText.contains("new_map")) {
            return "Map";
        }
        if (functionText.contains("new_set")) {
            return "Set";
        }
        
        // 类型转换函数
        if (functionText.contains("asInt")) {
            return "Integer";
        }
        if (functionText.contains("asDouble")) {
            return "Double";
        }
        if (functionText.contains("asString") || functionText.contains("toString")) {
            return "String";
        }
        if (functionText.contains("asBoolean")) {
            return "Boolean";
        }
        
        return "Object";
    }
    
    /**
     * 推断变量类型
     */
    @Nullable
    private static String inferVariableType(@NotNull PsiElement element) {
        String variableName = element.getText();
        
        // 查找变量声明
        MSVarDeclaration varDeclaration = findVariableDeclaration(variableName, element);
        if (varDeclaration != null) {
            return analyzeVariableDeclarationType(varDeclaration);
        }
        
        // 查找函数参数
        String parameterType = findFunctionParameterType(variableName, element);
        if (parameterType != null) {
            return parameterType;
        }
        
        return null;
    }
    
    /**
     * 推断成员访问类型
     */
    @Nullable
    private static String inferMemberAccessType(@NotNull PsiElement element) {
        // 查找父元素是否为成员访问
        PsiElement parent = element.getParent();
        if (parent != null) {
            String parentText = parent.getText();
            if (parentText.contains(".")) {
                String[] parts = parentText.split("\\.", 2);
                if (parts.length == 2) {
                    String qualifier = parts[0];
                    String member = parts[1];
                    
                    if (element.getText().equals(member)) {
                        String qualifierType = inferExpressionType(element.getPrevSibling().getPrevSibling());
                        return inferMemberType(qualifierType, member);
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * 推断成员类型
     */
    @NotNull
    private static String inferMemberType(@NotNull String objectType, @NotNull String memberName) {
        // 内置模块成员
        if (isBuiltinModule(objectType)) {
            return inferBuiltinModuleMemberType(objectType, memberName);
        }
        
        // 扩展方法
        return inferExtensionMethodType(objectType, memberName);
    }
    
    /**
     * 推断内置模块成员类型
     */
    @NotNull
    private static String inferBuiltinModuleMemberType(@NotNull String moduleName, @NotNull String memberName) {
        switch (moduleName) {
            case "db":
                return inferDatabaseMemberType(memberName);
            case "http":
                return inferHttpMemberType(memberName);
            case "request":
                return inferRequestMemberType(memberName);
            case "response":
                return inferResponseMemberType(memberName);
            default:
                return "Object";
        }
    }
    
    /**
     * 推断数据库成员类型
     */
    @NotNull
    private static String inferDatabaseMemberType(@NotNull String memberName) {
        switch (memberName) {
            case "select":
            case "selectByKey":
                return "Array";
            case "selectOne":
            case "selectByPrimaryKey":
                return "Object";
            case "selectInt":
                return "Integer";
            case "selectLong":
                return "Long";
            case "selectDouble":
                return "Double";
            case "selectValue":
                return "Object";
            case "count":
                return "Integer";
            case "page":
                return "PageResult";
            case "insert":
            case "update":
            case "delete":
                return "Integer";
            case "cache":
                return "CacheableQuery";
            case "transaction":
                return "TransactionManager";
            default:
                return "Object";
        }
    }
    
    /**
     * 推断HTTP成员类型
     */
    @NotNull
    private static String inferHttpMemberType(@NotNull String memberName) {
        switch (memberName) {
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
            case "timeout":
                return "HttpRequestBuilder";
            default:
                return "Object";
        }
    }
    
    /**
     * 推断请求成员类型
     */
    @NotNull
    private static String inferRequestMemberType(@NotNull String memberName) {
        switch (memberName) {
            case "getParameter":
            case "getHeader":
            case "getCookie":
                return "String";
            case "getParameters":
            case "getHeaders":
            case "getCookies":
                return "Map";
            case "getBody":
                return "Object";
            case "getFiles":
                return "Array";
            case "getFile":
                return "File";
            default:
                return "Object";
        }
    }
    
    /**
     * 推断响应成员类型
     */
    @NotNull
    private static String inferResponseMemberType(@NotNull String memberName) {
        switch (memberName) {
            case "json":
                return "ResponseBuilder";
            case "text":
                return "ResponseBuilder";
            case "redirect":
                return "ResponseBuilder";
            case "download":
                return "ResponseBuilder";
            case "setHeader":
            case "addHeader":
                return "ResponseBuilder";
            case "setStatus":
                return "ResponseBuilder";
            default:
                return "ResponseBuilder";
        }
    }
    
    /**
     * 推断扩展方法类型
     */
    @NotNull
    private static String inferExtensionMethodType(@NotNull String objectType, @NotNull String methodName) {
        // String扩展方法
        if ("String".equals(objectType)) {
            return inferStringExtensionMethodType(methodName);
        }
        
        // Number扩展方法
        if ("Number".equals(objectType) || "Integer".equals(objectType) || "Double".equals(objectType)) {
            return inferNumberExtensionMethodType(methodName);
        }
        
        // Array扩展方法
        if ("Array".equals(objectType) || "List".equals(objectType)) {
            return inferArrayExtensionMethodType(methodName);
        }
        
        // Map扩展方法
        if ("Map".equals(objectType)) {
            return inferMapExtensionMethodType(methodName);
        }
        
        // Date扩展方法
        if ("Date".equals(objectType)) {
            return inferDateExtensionMethodType(methodName);
        }
        
        // Boolean扩展方法
        if ("Boolean".equals(objectType)) {
            return inferBooleanExtensionMethodType(methodName);
        }
        
        // 通用Object扩展方法
        return inferObjectExtensionMethodType(methodName);
    }
    
    /**
     * 推断String扩展方法类型
     */
    @NotNull
    private static String inferStringExtensionMethodType(@NotNull String methodName) {
        switch (methodName) {
            case "isBlank":
            case "isNotBlank":
            case "startsWith":
            case "endsWith":
            case "contains":
                return "Boolean";
            case "length":
            case "indexOf":
            case "lastIndexOf":
                return "Integer";
            case "substring":
            case "replace":
            case "replaceAll":
            case "trim":
            case "toLowerCase":
            case "toUpperCase":
            case "charAt":
            case "repeat":
            case "padStart":
            case "padEnd":
                return "String";
            case "split":
                return "Array";
            default:
                return "String";
        }
    }
    
    /**
     * 推断Number扩展方法类型
     */
    @NotNull
    private static String inferNumberExtensionMethodType(@NotNull String methodName) {
        switch (methodName) {
            case "round":
            case "floor":
            case "ceil":
            case "abs":
            case "max":
            case "min":
            case "pow":
            case "sqrt":
                return "Number";
            case "toFixed":
            case "asPercent":
                return "String";
            case "isFinite":
            case "isNaN":
                return "Boolean";
            case "toRadians":
            case "toDegrees":
                return "Double";
            default:
                return "Number";
        }
    }
    
    /**
     * 推断Array扩展方法类型
     */
    @NotNull
    private static String inferArrayExtensionMethodType(@NotNull String methodName) {
        switch (methodName) {
            case "map":
            case "filter":
            case "sort":
            case "reverse":
            case "slice":
            case "concat":
            case "distinct":
            case "flatten":
                return "Array";
            case "first":
            case "last":
            case "get":
            case "pop":
            case "shift":
                return "Object";
            case "size":
            case "length":
            case "indexOf":
            case "lastIndexOf":
                return "Integer";
            case "isEmpty":
            case "isNotEmpty":
            case "contains":
                return "Boolean";
            case "join":
                return "String";
            case "push":
            case "unshift":
                return "Array";
            default:
                return "Object";
        }
    }
    
    /**
     * 推断Map扩展方法类型
     */
    @NotNull
    private static String inferMapExtensionMethodType(@NotNull String methodName) {
        switch (methodName) {
            case "get":
            case "put":
            case "remove":
                return "Object";
            case "containsKey":
            case "containsValue":
            case "isEmpty":
            case "isNotEmpty":
                return "Boolean";
            case "size":
                return "Integer";
            case "keys":
            case "values":
            case "entries":
                return "Array";
            case "merge":
            case "filter":
            case "map":
                return "Map";
            default:
                return "Object";
        }
    }
    
    /**
     * 推断Date扩展方法类型
     */
    @NotNull
    private static String inferDateExtensionMethodType(@NotNull String methodName) {
        switch (methodName) {
            case "format":
            case "toISOString":
            case "toDateString":
            case "toTimeString":
                return "String";
            case "addDays":
            case "addHours":
            case "addMinutes":
            case "addMonths":
            case "addYears":
                return "Date";
            case "getYear":
            case "getMonth":
            case "getDay":
            case "getHour":
            case "getMinute":
            case "getSecond":
                return "Integer";
            case "getTime":
            case "diffDays":
            case "diffHours":
            case "diffMinutes":
                return "Long";
            case "isAfter":
            case "isBefore":
            case "isSameDay":
                return "Boolean";
            default:
                return "Date";
        }
    }
    
    /**
     * 推断Boolean扩展方法类型
     */
    @NotNull
    private static String inferBooleanExtensionMethodType(@NotNull String methodName) {
        switch (methodName) {
            case "and":
            case "or":
            case "not":
            case "xor":
            case "equals":
                return "Boolean";
            case "toString":
                return "String";
            default:
                return "Boolean";
        }
    }
    
    /**
     * 推断Object扩展方法类型
     */
    @NotNull
    private static String inferObjectExtensionMethodType(@NotNull String methodName) {
        switch (methodName) {
            case "asInt":
                return "Integer";
            case "asDouble":
                return "Double";
            case "asFloat":
                return "Float";
            case "asLong":
                return "Long";
            case "asString":
            case "toString":
            case "getClass":
                return "String";
            case "asBoolean":
            case "is":
            case "isString":
            case "isNumber":
            case "isBoolean":
            case "isArray":
            case "isMap":
            case "isDate":
            case "isNull":
            case "isNotNull":
            case "isEmpty":
            case "isNotEmpty":
            case "equals":
                return "Boolean";
            case "hashCode":
                return "Integer";
            case "clone":
                return "Object";
            default:
                return "Object";
        }
    }
    
    /**
     * 推断方法返回类型
     */
    @NotNull
    public static String inferMethodReturnType(@NotNull String objectType, @NotNull String methodName) {
        // 内置模块方法
        if (isBuiltinModule(objectType)) {
            return inferBuiltinModuleMemberType(objectType, methodName);
        }
        
        // 扩展方法
        return inferExtensionMethodType(objectType, methodName);
    }
    
    // ==================== 辅助方法 ====================
    
    /**
     * 检查是否为内置模块
     */
    private static boolean isBuiltinModule(@NotNull String name) {
        return "db".equals(name) || "http".equals(name) || "request".equals(name) || 
               "response".equals(name) || "env".equals(name) || "log".equals(name) ||
               "magic".equals(name);
    }
    
    /**
     * 检查是否为函数调用
     */
    private static boolean isFunctionCall(@NotNull PsiElement element) {
        String text = element.getText();
        return text.contains("(") && text.contains(")");
    }
    
    /**
     * 提取方法名
     */
    @Nullable
    private static String extractMethodName(@NotNull String text) {
        int parenIndex = text.indexOf('(');
        return parenIndex > 0 ? text.substring(0, parenIndex) : null;
    }
    
    /**
     * 查找变量声明
     */
    @Nullable
    private static MSVarDeclaration findVariableDeclaration(@NotNull String variableName, @NotNull PsiElement context) {
        PsiElement current = context;
        
        while (current != null) {
            List<MSVarDeclaration> declarations = PsiTreeUtil.getChildrenOfTypeAsList(current, MSVarDeclaration.class);
            
            for (MSVarDeclaration declaration : declarations) {
                if (variableName.equals(declaration.getName())) {
                    return declaration;
                }
            }
            
            current = current.getParent();
        }
        
        return null;
    }
    
    /**
     * 分析变量声明类型
     */
    @Nullable
    private static String analyzeVariableDeclarationType(@NotNull MSVarDeclaration varDeclaration) {
        // 简化实现：基于变量名推断
        String varName = varDeclaration.getName();
        if (varName != null) {
            return inferTypeByNamingConvention(varName);
        }
        
        return null;
    }
    
    /**
     * 查找函数参数类型
     */
    @Nullable
    private static String findFunctionParameterType(@NotNull String parameterName, @NotNull PsiElement context) {
        MSFunctionDeclaration functionDecl = PsiTreeUtil.getParentOfType(context, MSFunctionDeclaration.class);
        if (functionDecl != null) {
            // 简化实现：基于参数名推断
            return inferTypeByNamingConvention(parameterName);
        }
        
        return null;
    }
    
    /**
     * 基于命名约定推断类型
     */
    @NotNull
    private static String inferTypeByNamingConvention(@NotNull String name) {
        String lowerName = name.toLowerCase();
        
        // 字符串类型
        if (lowerName.contains("str") || lowerName.contains("name") || 
            lowerName.contains("text") || lowerName.contains("message") ||
            lowerName.contains("desc") || lowerName.contains("title") ||
            lowerName.contains("url") || lowerName.contains("path")) {
            return "String";
        }
        
        // 数组/集合类型
        if (lowerName.contains("list") || lowerName.contains("array") || 
            lowerName.contains("items") || lowerName.endsWith("s") ||
            lowerName.contains("records") || lowerName.contains("results")) {
            return "Array";
        }
        
        // Map类型
        if (lowerName.contains("map") || lowerName.contains("dict") || 
            lowerName.contains("config") || lowerName.contains("params") ||
            lowerName.contains("props") || lowerName.contains("attrs") ||
            lowerName.contains("headers") || lowerName.contains("cookies")) {
            return "Map";
        }
        
        // 数值类型
        if (lowerName.contains("count") || lowerName.contains("num") || 
            lowerName.contains("size") || lowerName.contains("id") ||
            lowerName.contains("index") || lowerName.contains("length") ||
            lowerName.contains("total") || lowerName.contains("amount")) {
            return "Number";
        }
        
        // 日期类型
        if (lowerName.contains("date") || lowerName.contains("time") || 
            lowerName.contains("timestamp") || lowerName.contains("created") ||
            lowerName.contains("updated") || lowerName.contains("modified") ||
            lowerName.contains("expired") || lowerName.contains("start") ||
            lowerName.contains("end")) {
            return "Date";
        }
        
        // 布尔类型
        if (lowerName.contains("flag") || lowerName.contains("is") || 
            lowerName.contains("has") || lowerName.contains("can") ||
            lowerName.contains("should") || lowerName.contains("enable") ||
            lowerName.contains("active") || lowerName.contains("valid")) {
            return "Boolean";
        }
        
        return "Object";
    }
    
    /**
     * 清空类型缓存
     */
    public static void clearCache() {
        TYPE_CACHE.clear();
        CHAIN_TYPE_CACHE.clear();
    }
    
    /**
     * 方法调用信息
     */
    private static class MethodCall {
        private final String methodName;
        private final String baseType;
        
        public MethodCall(@NotNull String methodName, @NotNull String baseType) {
            this.methodName = methodName;
            this.baseType = baseType;
        }
        
        @NotNull
        public String getMethodName() {
            return methodName;
        }
        
        @NotNull
        public String getBaseType() {
            return baseType;
        }
    }
}