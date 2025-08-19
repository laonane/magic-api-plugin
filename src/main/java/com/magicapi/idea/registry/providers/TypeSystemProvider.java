package com.magicapi.idea.registry.providers;

import com.magicapi.idea.completion.model.ApiMethod;
import com.magicapi.idea.registry.ModuleRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 类型系统提供器
 * 
 * 负责管理Magic API的完整类型系统，包括类型定义、类型推断、类型转换等
 * 为代码补全和语法分析提供精确的类型信息支持
 */
public class TypeSystemProvider {
    
    private final ModuleRegistry registry;
    
    // 基础类型定义
    private final Set<String> primitiveTypes = new HashSet<>();
    private final Set<String> objectTypes = new HashSet<>();
    private final Set<String> collectionTypes = new HashSet<>();
    
    // 类型层次结构
    private final Map<String, Set<String>> typeHierarchy = new HashMap<>();
    private final Map<String, String> typeSuperTypes = new HashMap<>();
    
    // 类型转换规则
    private final Map<String, Set<String>> implicitConversions = new HashMap<>();
    private final Map<String, Set<String>> explicitConversions = new HashMap<>();
    
    // 类型方法映射
    private final Map<String, Set<String>> typeMethods = new HashMap<>();
    
    // 类型属性映射
    private final Map<String, Set<String>> typeProperties = new HashMap<>();
    
    // 类型别名
    private final Map<String, String> typeAliases = new HashMap<>();
    
    // 类型正则表达式
    private final Map<String, Pattern> typePatterns = new HashMap<>();
    
    // 默认值映射
    private final Map<String, Object> defaultValues = new HashMap<>();
    
    public TypeSystemProvider(@NotNull ModuleRegistry registry) {
        this.registry = registry;
    }
    
    /**
     * 初始化类型系统
     */
    public void initializeTypeSystem() {
        // 初始化基础类型
        initializePrimitiveTypes();
        
        // 初始化对象类型
        initializeObjectTypes();
        
        // 初始化集合类型
        initializeCollectionTypes();
        
        // 初始化类型层次结构
        initializeTypeHierarchy();
        
        // 初始化类型转换规则
        initializeTypeConversions();
        
        // 初始化类型方法映射
        initializeTypeMethods();
        
        // 初始化类型属性映射
        initializeTypeProperties();
        
        // 初始化类型别名
        initializeTypeAliases();
        
        // 初始化类型模式
        initializeTypePatterns();
        
        // 初始化默认值
        initializeDefaultValues();
    }
    
    /**
     * 初始化基础类型
     */
    private void initializePrimitiveTypes() {
        primitiveTypes.addAll(Arrays.asList(
                "void", "null", "undefined",
                "Boolean", "boolean",
                "Integer", "int",
                "Long", "long", 
                "Double", "double",
                "Float", "float",
                "Number",
                "String", "string",
                "Char", "char",
                "Byte", "byte"
        ));
    }
    
    /**
     * 初始化对象类型
     */
    private void initializeObjectTypes() {
        objectTypes.addAll(Arrays.asList(
                "Object",
                "Date", "Timestamp",
                "File", "InputStream", "OutputStream",
                "Pattern", "Matcher",
                "Class",
                "Exception", "Throwable",
                "UUID",
                "URL", "URI"
        ));
    }
    
    /**
     * 初始化集合类型
     */
    private void initializeCollectionTypes() {
        collectionTypes.addAll(Arrays.asList(
                "Array", "List", "ArrayList", "LinkedList",
                "Set", "HashSet", "TreeSet", "LinkedHashSet",
                "Map", "HashMap", "TreeMap", "LinkedHashMap",
                "Queue", "Deque",
                "Collection", "Iterable"
        ));
    }
    
    /**
     * 初始化类型层次结构
     */
    private void initializeTypeHierarchy() {
        // Object作为根类型
        typeHierarchy.put("Object", Collections.emptySet());
        
        // 基础类型继承关系
        setSubTypes("Object", "String", "Number", "Boolean", "Date", "Array", "Map");
        setSubTypes("Number", "Integer", "Long", "Double", "Float", "Byte");
        setSubTypes("Array", "List");
        setSubTypes("List", "ArrayList", "LinkedList");
        setSubTypes("Collection", "List", "Set", "Queue");
        setSubTypes("Set", "HashSet", "TreeSet", "LinkedHashSet");
        setSubTypes("Map", "HashMap", "TreeMap", "LinkedHashMap");
        
        // Magic API特殊类型
        setSubTypes("Object", "HttpResponse", "HttpRequestBuilder", "PageResult", 
                   "TransactionManager", "CacheableQuery", "TableQuery", "LogicDeleteQuery",
                   "DatabaseQuery", "ResponseBuilder", "MultipartFile", "HttpSession");
        
        // 构建反向映射
        for (Map.Entry<String, Set<String>> entry : typeHierarchy.entrySet()) {
            String superType = entry.getKey();
            for (String subType : entry.getValue()) {
                typeSuperTypes.put(subType, superType);
            }
        }
    }
    
    /**
     * 设置子类型关系
     */
    private void setSubTypes(@NotNull String superType, @NotNull String... subTypes) {
        typeHierarchy.put(superType, new HashSet<>(Arrays.asList(subTypes)));
    }
    
    /**
     * 初始化类型转换规则
     */
    private void initializeTypeConversions() {
        // 隐式转换（自动类型提升）
        setImplicitConversions("Integer", "Long", "Double", "Float", "Number");
        setImplicitConversions("Long", "Double", "Float", "Number");
        setImplicitConversions("Float", "Double", "Number");
        setImplicitConversions("Double", "Number");
        setImplicitConversions("Byte", "Integer", "Long", "Double", "Float", "Number");
        
        // 所有类型都可以转换为String和Object
        for (String type : getAllTypes()) {
            setImplicitConversions(type, "String", "Object");
        }
        
        // 显式转换（需要调用转换方法）
        setExplicitConversions("String", "Integer", "Long", "Double", "Float", "Boolean", "Date");
        setExplicitConversions("Number", "Integer", "Long", "Double", "Float", "String", "Boolean");
        setExplicitConversions("Boolean", "String", "Integer");
        setExplicitConversions("Date", "String", "Long", "Integer");
        setExplicitConversions("Object", "String", "Integer", "Double", "Boolean", "Array", "Map");
        
        // 集合类型转换
        setExplicitConversions("Array", "List", "Set", "String");
        setExplicitConversions("List", "Array", "Set", "String");
        setExplicitConversions("Set", "Array", "List", "String");
        setExplicitConversions("Map", "Array", "List", "String");
    }
    
    /**
     * 设置隐式转换规则
     */
    private void setImplicitConversions(@NotNull String fromType, @NotNull String... toTypes) {
        implicitConversions.put(fromType, new HashSet<>(Arrays.asList(toTypes)));
    }
    
    /**
     * 设置显式转换规则
     */
    private void setExplicitConversions(@NotNull String fromType, @NotNull String... toTypes) {
        explicitConversions.put(fromType, new HashSet<>(Arrays.asList(toTypes)));
    }
    
    /**
     * 初始化类型方法映射
     */
    private void initializeTypeMethods() {
        // String类型方法
        setTypeMethods("String", "length", "charAt", "substring", "indexOf", "replace", 
                      "split", "trim", "toLowerCase", "toUpperCase", "startsWith", "endsWith",
                      "contains", "matches", "isEmpty", "isBlank");
        
        // Number类型方法
        setTypeMethods("Number", "abs", "round", "floor", "ceil", "max", "min", "pow", "sqrt");
        setTypeMethods("Integer", "abs", "max", "min", "toString", "compareTo");
        setTypeMethods("Double", "abs", "round", "floor", "ceil", "max", "min", "isNaN", "isInfinite");
        
        // Array/List类型方法
        setTypeMethods("Array", "size", "length", "get", "set", "add", "remove", "contains",
                      "indexOf", "isEmpty", "first", "last", "map", "filter", "sort", "join");
        setTypeMethods("List", "size", "get", "set", "add", "remove", "contains", "indexOf", "isEmpty");
        
        // Map类型方法
        setTypeMethods("Map", "size", "get", "put", "remove", "containsKey", "containsValue",
                      "isEmpty", "keys", "values", "entries", "clear");
        
        // Date类型方法
        setTypeMethods("Date", "getYear", "getMonth", "getDay", "getHour", "getMinute", "getSecond",
                      "addDays", "addHours", "addMinutes", "format", "isAfter", "isBefore");
        
        // Boolean类型方法
        setTypeMethods("Boolean", "and", "or", "not", "xor", "toString");
        
        // Object类型方法
        setTypeMethods("Object", "toString", "equals", "hashCode", "getClass", "isNull", "isNotNull",
                      "asString", "asInt", "asDouble", "asBoolean", "asArray", "asMap");
    }
    
    /**
     * 设置类型方法
     */
    private void setTypeMethods(@NotNull String type, @NotNull String... methods) {
        typeMethods.put(type, new HashSet<>(Arrays.asList(methods)));
    }
    
    /**
     * 初始化类型属性映射
     */
    private void initializeTypeProperties() {
        // String属性
        setTypeProperties("String", "length");
        
        // Array属性
        setTypeProperties("Array", "length", "size");
        setTypeProperties("List", "size");
        
        // Map属性
        setTypeProperties("Map", "size");
        
        // Date属性
        setTypeProperties("Date", "time", "year", "month", "day", "hour", "minute", "second");
        
        // HttpResponse属性
        setTypeProperties("HttpResponse", "status", "headers", "body", "contentType");
        
        // PageResult属性
        setTypeProperties("PageResult", "records", "total", "current", "size", "pages");
    }
    
    /**
     * 设置类型属性
     */
    private void setTypeProperties(@NotNull String type, @NotNull String... properties) {
        typeProperties.put(type, new HashSet<>(Arrays.asList(properties)));
    }
    
    /**
     * 初始化类型别名
     */
    private void initializeTypeAliases() {
        // 基础类型别名
        typeAliases.put("int", "Integer");
        typeAliases.put("long", "Long");
        typeAliases.put("double", "Double");
        typeAliases.put("float", "Float");
        typeAliases.put("boolean", "Boolean");
        typeAliases.put("char", "Char");
        typeAliases.put("byte", "Byte");
        typeAliases.put("string", "String");
        
        // 集合类型别名
        typeAliases.put("list", "List");
        typeAliases.put("array", "Array");
        typeAliases.put("map", "Map");
        typeAliases.put("set", "Set");
        
        // 特殊类型别名
        typeAliases.put("date", "Date");
        typeAliases.put("object", "Object");
        typeAliases.put("number", "Number");
    }
    
    /**
     * 初始化类型识别模式
     */
    private void initializeTypePatterns() {
        // 字符串字面量模式
        typePatterns.put("String", Pattern.compile("^[\"'].*[\"']$"));
        
        // 数值字面量模式
        typePatterns.put("Integer", Pattern.compile("^-?\\d+$"));
        typePatterns.put("Long", Pattern.compile("^-?\\d+[Ll]$"));
        typePatterns.put("Double", Pattern.compile("^-?\\d*\\.\\d+$"));
        typePatterns.put("Float", Pattern.compile("^-?\\d*\\.\\d+[Ff]$"));
        
        // 布尔字面量模式
        typePatterns.put("Boolean", Pattern.compile("^(true|false)$"));
        
        // 数组字面量模式
        typePatterns.put("Array", Pattern.compile("^\\[.*\\]$"));
        
        // 对象字面量模式
        typePatterns.put("Map", Pattern.compile("^\\{.*\\}$"));
        
        // 空值模式
        typePatterns.put("null", Pattern.compile("^(null|undefined)$"));
        
        // 函数调用模式
        typePatterns.put("Function", Pattern.compile("^\\w+\\(.*\\)$"));
        
        // 变量命名模式
        typePatterns.put("Variable", Pattern.compile("^[a-zA-Z_$][a-zA-Z0-9_$]*$"));
    }
    
    /**
     * 初始化默认值
     */
    private void initializeDefaultValues() {
        defaultValues.put("String", "\"\"");
        defaultValues.put("Integer", 0);
        defaultValues.put("Long", 0L);
        defaultValues.put("Double", 0.0);
        defaultValues.put("Float", 0.0f);
        defaultValues.put("Boolean", false);
        defaultValues.put("Array", "[]");
        defaultValues.put("List", "[]");
        defaultValues.put("Map", "{}");
        defaultValues.put("Set", "[]");
        defaultValues.put("Object", null);
        defaultValues.put("Date", null);
    }
    
    // ==================== 公共API方法 ====================
    
    /**
     * 推断表达式类型
     */
    @NotNull
    public String inferExpressionType(@NotNull String expression) {
        String trimmed = expression.trim();
        
        // 检查字面量类型
        for (Map.Entry<String, Pattern> entry : typePatterns.entrySet()) {
            if (entry.getValue().matcher(trimmed).matches()) {
                return resolveTypeAlias(entry.getKey());
            }
        }
        
        // 检查函数调用
        if (trimmed.contains("(") && trimmed.contains(")")) {
            return inferFunctionCallType(trimmed);
        }
        
        // 检查成员访问
        if (trimmed.contains(".")) {
            return inferMemberAccessType(trimmed);
        }
        
        // 基于命名约定推断
        return inferTypeByNaming(trimmed);
    }
    
    /**
     * 推断函数调用类型
     */
    @NotNull
    private String inferFunctionCallType(@NotNull String functionCall) {
        // 数据库函数
        if (functionCall.contains("db.select") || functionCall.contains("select(")) {
            return "Array";
        }
        if (functionCall.contains("db.selectOne") || functionCall.contains("selectOne(")) {
            return "Object";
        }
        if (functionCall.contains("db.selectInt") || functionCall.contains("selectInt(")) {
            return "Integer";
        }
        if (functionCall.contains("db.page") || functionCall.contains("page(")) {
            return "PageResult";
        }
        
        // HTTP函数
        if (functionCall.matches(".*http\\.(get|post|put|delete|patch)\\(.*")) {
            return "HttpResponse";
        }
        
        // 字符串函数
        if (functionCall.contains("uuid(") || functionCall.contains("concat(") ||
            functionCall.contains("format(") || functionCall.contains("md5(")) {
            return "String";
        }
        
        // 数学函数
        if (functionCall.contains("sum(") || functionCall.contains("max(") ||
            functionCall.contains("min(") || functionCall.contains("count(")) {
            return "Number";
        }
        
        // 数组构造函数
        if (functionCall.contains("new_array(") || functionCall.contains("new_list(")) {
            return "Array";
        }
        if (functionCall.contains("new_map(")) {
            return "Map";
        }
        
        return "Object";
    }
    
    /**
     * 推断成员访问类型
     */
    @NotNull
    private String inferMemberAccessType(@NotNull String memberAccess) {
        String[] parts = memberAccess.split("\\.");
        if (parts.length < 2) {
            return "Object";
        }
        
        String objectType = inferExpressionType(parts[0]);
        String memberName = parts[parts.length - 1];
        
        return getMethodReturnType(objectType, memberName);
    }
    
    /**
     * 基于命名约定推断类型
     */
    @NotNull
    private String inferTypeByNaming(@NotNull String name) {
        String lowerName = name.toLowerCase();
        
        // 字符串类型
        if (lowerName.contains("name") || lowerName.contains("text") || 
            lowerName.contains("str") || lowerName.contains("message") ||
            lowerName.contains("url") || lowerName.contains("path")) {
            return "String";
        }
        
        // 数组类型
        if (lowerName.contains("list") || lowerName.contains("array") || 
            lowerName.endsWith("s") || lowerName.contains("items") ||
            lowerName.contains("records") || lowerName.contains("results")) {
            return "Array";
        }
        
        // Map类型
        if (lowerName.contains("map") || lowerName.contains("dict") ||
            lowerName.contains("config") || lowerName.contains("params") ||
            lowerName.contains("headers") || lowerName.contains("attrs")) {
            return "Map";
        }
        
        // 数值类型
        if (lowerName.contains("count") || lowerName.contains("num") ||
            lowerName.contains("size") || lowerName.contains("id") ||
            lowerName.contains("index") || lowerName.contains("total")) {
            return "Integer";
        }
        
        // 日期类型
        if (lowerName.contains("date") || lowerName.contains("time") ||
            lowerName.contains("timestamp") || lowerName.contains("created") ||
            lowerName.contains("updated")) {
            return "Date";
        }
        
        // 布尔类型
        if (lowerName.contains("is") || lowerName.contains("has") ||
            lowerName.contains("can") || lowerName.contains("flag") ||
            lowerName.contains("enable") || lowerName.contains("valid")) {
            return "Boolean";
        }
        
        return "Object";
    }
    
    /**
     * 获取方法返回类型
     */
    @NotNull
    public String getMethodReturnType(@NotNull String objectType, @NotNull String methodName) {
        String resolvedType = resolveTypeAlias(objectType);
        
        // 检查是否为内置模块
        if (isBuiltinModule(resolvedType)) {
            return getBuiltinModuleMethodReturnType(resolvedType, methodName);
        }
        
        // 检查扩展方法
        return getExtensionMethodReturnType(resolvedType, methodName);
    }
    
    /**
     * 获取内置模块方法返回类型
     */
    @NotNull
    private String getBuiltinModuleMethodReturnType(@NotNull String moduleName, @NotNull String methodName) {
        switch (moduleName) {
            case "db":
                return getDatabaseMethodReturnType(methodName);
            case "http":
                return getHttpMethodReturnType(methodName);
            case "request":
                return getRequestMethodReturnType(methodName);
            case "response":
                return getResponseMethodReturnType(methodName);
            default:
                return "Object";
        }
    }
    
    /**
     * 获取数据库方法返回类型
     */
    @NotNull
    private String getDatabaseMethodReturnType(@NotNull String methodName) {
        switch (methodName) {
            case "select":
            case "selectByKey":
                return "Array";
            case "selectOne":
            case "selectByPrimaryKey":
                return "Object";
            case "selectInt":
            case "count":
                return "Integer";
            case "selectLong":
                return "Long";
            case "selectDouble":
                return "Double";
            case "selectValue":
                return "Object";
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
     * 获取HTTP方法返回类型
     */
    @NotNull
    private String getHttpMethodReturnType(@NotNull String methodName) {
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
            case "timeout":
                return "HttpRequestBuilder";
            default:
                return "Object";
        }
    }
    
    /**
     * 获取请求方法返回类型
     */
    @NotNull
    private String getRequestMethodReturnType(@NotNull String methodName) {
        switch (methodName) {
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
                return "MultipartFile";
            default:
                return "Object";
        }
    }
    
    /**
     * 获取响应方法返回类型
     */
    @NotNull
    private String getResponseMethodReturnType(@NotNull String methodName) {
        // response模块的所有方法都返回ResponseBuilder
        return "ResponseBuilder";
    }
    
    /**
     * 获取扩展方法返回类型
     */
    @NotNull
    private String getExtensionMethodReturnType(@NotNull String objectType, @NotNull String methodName) {
        String resolvedType = resolveTypeAlias(objectType);
        
        // String扩展方法
        if ("String".equals(resolvedType)) {
            return getStringExtensionMethodReturnType(methodName);
        }
        
        // Number扩展方法
        if (isNumberType(resolvedType)) {
            return getNumberExtensionMethodReturnType(methodName);
        }
        
        // Array扩展方法
        if (isArrayType(resolvedType)) {
            return getArrayExtensionMethodReturnType(methodName);
        }
        
        // Map扩展方法
        if ("Map".equals(resolvedType)) {
            return getMapExtensionMethodReturnType(methodName);
        }
        
        // Date扩展方法
        if ("Date".equals(resolvedType)) {
            return getDateExtensionMethodReturnType(methodName);
        }
        
        // Boolean扩展方法
        if ("Boolean".equals(resolvedType)) {
            return getBooleanExtensionMethodReturnType(methodName);
        }
        
        // Object扩展方法
        return getObjectExtensionMethodReturnType(methodName);
    }
    
    /**
     * 获取String扩展方法返回类型
     */
    @NotNull
    private String getStringExtensionMethodReturnType(@NotNull String methodName) {
        switch (methodName) {
            case "isBlank":
            case "isNotBlank":
            case "isEmpty":
            case "isNotEmpty":
            case "startsWith":
            case "endsWith":
            case "contains":
            case "matches":
            case "isNumeric":
            case "isAlpha":
                return "Boolean";
            case "length":
            case "size":
            case "indexOf":
            case "lastIndexOf":
                return "Integer";
            case "split":
            case "lines":
            case "words":
                return "Array";
            default:
                return "String";
        }
    }
    
    /**
     * 获取Number扩展方法返回类型
     */
    @NotNull
    private String getNumberExtensionMethodReturnType(@NotNull String methodName) {
        switch (methodName) {
            case "toFixed":
            case "asPercent":
            case "asCurrency":
            case "toBinary":
            case "toHex":
                return "String";
            case "isFinite":
            case "isNaN":
            case "isInteger":
            case "isPositive":
            case "isNegative":
            case "isZero":
            case "isEven":
            case "isOdd":
            case "between":
                return "Boolean";
            case "toRadians":
            case "toDegrees":
                return "Double";
            default:
                return "Number";
        }
    }
    
    /**
     * 获取Array扩展方法返回类型
     */
    @NotNull
    private String getArrayExtensionMethodReturnType(@NotNull String methodName) {
        switch (methodName) {
            case "size":
            case "length":
            case "indexOf":
            case "lastIndexOf":
            case "count":
                return "Integer";
            case "isEmpty":
            case "isNotEmpty":
            case "contains":
            case "every":
            case "some":
            case "none":
                return "Boolean";
            case "join":
            case "mkString":
                return "String";
            case "first":
            case "last":
            case "get":
            case "pop":
            case "shift":
            case "find":
            case "max":
            case "min":
                return "Object";
            case "sum":
                return "Number";
            case "average":
                return "Double";
            default:
                return "Array";
        }
    }
    
    /**
     * 获取Map扩展方法返回类型
     */
    @NotNull
    private String getMapExtensionMethodReturnType(@NotNull String methodName) {
        switch (methodName) {
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
            case "keySet":
            case "valueSet":
            case "entrySet":
                return "Set";
            case "get":
            case "put":
            case "remove":
            case "getOrDefault":
            case "putIfAbsent":
            case "computeIfAbsent":
            case "computeIfPresent":
                return "Object";
            default:
                return "Map";
        }
    }
    
    /**
     * 获取Date扩展方法返回类型
     */
    @NotNull
    private String getDateExtensionMethodReturnType(@NotNull String methodName) {
        switch (methodName) {
            case "format":
            case "toISOString":
            case "toDateString":
            case "toTimeString":
            case "toString":
                return "String";
            case "isAfter":
            case "isBefore":
            case "isEqual":
            case "isSameDay":
            case "isSameMonth":
            case "isSameYear":
                return "Boolean";
            case "getYear":
            case "getMonth":
            case "getDay":
            case "getWeekday":
            case "getHour":
            case "getMinute":
            case "getSecond":
            case "getMillisecond":
            case "getDayOfYear":
            case "getWeekOfYear":
                return "Integer";
            case "getTime":
            case "diffYears":
            case "diffMonths":
            case "diffWeeks":
            case "diffDays":
            case "diffHours":
            case "diffMinutes":
            case "diffSeconds":
            case "diffMilliseconds":
                return "Long";
            default:
                return "Date";
        }
    }
    
    /**
     * 获取Boolean扩展方法返回类型
     */
    @NotNull
    private String getBooleanExtensionMethodReturnType(@NotNull String methodName) {
        switch (methodName) {
            case "toString":
            case "toYesNo":
            case "toOnOff":
            case "toTrueFalse":
                return "String";
            case "toInt":
                return "Integer";
            case "then":
            case "otherwise":
            case "ifTrue":
            case "ifFalse":
                return "Object";
            case "compare":
                return "Integer";
            default:
                return "Boolean";
        }
    }
    
    /**
     * 获取Object扩展方法返回类型
     */
    @NotNull
    private String getObjectExtensionMethodReturnType(@NotNull String methodName) {
        switch (methodName) {
            case "asString":
            case "toString":
            case "getClass":
            case "getType":
            case "toJson":
            case "toXml":
            case "toYaml":
                return "String";
            case "asInt":
                return "Integer";
            case "asLong":
                return "Long";
            case "asDouble":
                return "Double";
            case "asFloat":
                return "Float";
            case "asBoolean":
            case "is":
            case "isString":
            case "isNumber":
            case "isInteger":
            case "isFloat":
            case "isBoolean":
            case "isArray":
            case "isList":
            case "isMap":
            case "isSet":
            case "isDate":
            case "isFunction":
            case "isNull":
            case "isNotNull":
            case "isEmpty":
            case "isNotEmpty":
            case "isBlank":
            case "isNotBlank":
            case "equals":
            case "hasProperty":
            case "hasMethod":
                return "Boolean";
            case "hashCode":
                return "Integer";
            case "asDate":
                return "Date";
            case "asArray":
                return "Array";
            case "asMap":
                return "Map";
            case "clone":
            case "deepClone":
            case "orElse":
            case "orElseGet":
            case "orElseThrow":
            case "getProperty":
            case "setProperty":
            case "invokeMethod":
                return "Object";
            default:
                return "Object";
        }
    }
    
    // ==================== 辅助方法 ====================
    
    /**
     * 解析类型别名
     */
    @NotNull
    public String resolveTypeAlias(@NotNull String type) {
        return typeAliases.getOrDefault(type, type);
    }
    
    /**
     * 检查是否为内置模块
     */
    private boolean isBuiltinModule(@NotNull String name) {
        return "db".equals(name) || "http".equals(name) || "request".equals(name) || 
               "response".equals(name) || "env".equals(name) || "log".equals(name) ||
               "magic".equals(name);
    }
    
    /**
     * 检查是否为数值类型
     */
    public boolean isNumberType(@NotNull String type) {
        String resolvedType = resolveTypeAlias(type);
        return "Number".equals(resolvedType) || "Integer".equals(resolvedType) || 
               "Long".equals(resolvedType) || "Double".equals(resolvedType) || 
               "Float".equals(resolvedType) || "Byte".equals(resolvedType);
    }
    
    /**
     * 检查是否为数组类型
     */
    public boolean isArrayType(@NotNull String type) {
        String resolvedType = resolveTypeAlias(type);
        return "Array".equals(resolvedType) || "List".equals(resolvedType) || 
               "ArrayList".equals(resolvedType) || "LinkedList".equals(resolvedType);
    }
    
    /**
     * 检查是否为集合类型
     */
    public boolean isCollectionType(@NotNull String type) {
        String resolvedType = resolveTypeAlias(type);
        return collectionTypes.contains(resolvedType);
    }
    
    /**
     * 检查是否为基础类型
     */
    public boolean isPrimitiveType(@NotNull String type) {
        String resolvedType = resolveTypeAlias(type);
        return primitiveTypes.contains(resolvedType);
    }
    
    /**
     * 检查类型兼容性
     */
    public boolean isCompatible(@NotNull String fromType, @NotNull String toType) {
        String resolvedFromType = resolveTypeAlias(fromType);
        String resolvedToType = resolveTypeAlias(toType);
        
        // 相同类型
        if (resolvedFromType.equals(resolvedToType)) {
            return true;
        }
        
        // 检查隐式转换
        Set<String> implicitTargets = implicitConversions.get(resolvedFromType);
        if (implicitTargets != null && implicitTargets.contains(resolvedToType)) {
            return true;
        }
        
        // 检查继承关系
        return isSubtypeOf(resolvedFromType, resolvedToType);
    }
    
    /**
     * 检查是否为子类型
     */
    public boolean isSubtypeOf(@NotNull String subType, @NotNull String superType) {
        if (subType.equals(superType)) {
            return true;
        }
        
        String currentSuperType = typeSuperTypes.get(subType);
        while (currentSuperType != null) {
            if (currentSuperType.equals(superType)) {
                return true;
            }
            currentSuperType = typeSuperTypes.get(currentSuperType);
        }
        
        return false;
    }
    
    /**
     * 获取所有类型
     */
    @NotNull
    public Set<String> getAllTypes() {
        Set<String> allTypes = new HashSet<>();
        allTypes.addAll(primitiveTypes);
        allTypes.addAll(objectTypes);
        allTypes.addAll(collectionTypes);
        allTypes.addAll(typeHierarchy.keySet());
        return allTypes;
    }
    
    /**
     * 获取类型的默认值
     */
    @Nullable
    public Object getDefaultValue(@NotNull String type) {
        String resolvedType = resolveTypeAlias(type);
        return defaultValues.get(resolvedType);
    }
    
    /**
     * 获取类型的方法列表
     */
    @NotNull
    public Set<String> getTypeMethods(@NotNull String type) {
        String resolvedType = resolveTypeAlias(type);
        return typeMethods.getOrDefault(resolvedType, Collections.emptySet());
    }
    
    /**
     * 获取类型的属性列表
     */
    @NotNull
    public Set<String> getTypeProperties(@NotNull String type) {
        String resolvedType = resolveTypeAlias(type);
        return typeProperties.getOrDefault(resolvedType, Collections.emptySet());
    }
    
    /**
     * 获取统计信息
     */
    @NotNull
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        
        stats.put("primitiveTypes", primitiveTypes.size());
        stats.put("objectTypes", objectTypes.size());
        stats.put("collectionTypes", collectionTypes.size());
        stats.put("typeHierarchy", typeHierarchy.size());
        stats.put("typeAliases", typeAliases.size());
        stats.put("typePatterns", typePatterns.size());
        stats.put("implicitConversions", implicitConversions.size());
        stats.put("explicitConversions", explicitConversions.size());
        stats.put("typeMethods", typeMethods.size());
        stats.put("typeProperties", typeProperties.size());
        
        int totalTypes = primitiveTypes.size() + objectTypes.size() + collectionTypes.size();
        stats.put("totalTypes", totalTypes);
        
        return stats;
    }
}