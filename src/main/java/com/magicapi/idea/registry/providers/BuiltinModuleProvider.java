package com.magicapi.idea.registry.providers;

import com.magicapi.idea.completion.model.ApiMethod;
import com.magicapi.idea.completion.model.MagicApiModule;
import com.magicapi.idea.completion.model.Parameter;
import com.magicapi.idea.registry.ModuleRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 内置模块提供器
 * 
 * 负责注册和管理所有Magic API的内置模块
 * 包括db、http、request、response、env、log、magic等模块
 */
public class BuiltinModuleProvider {
    
    private final ModuleRegistry registry;
    
    // 模块定义缓存
    private final Map<String, MagicApiModule> moduleDefinitions = new HashMap<>();
    
    public BuiltinModuleProvider(@NotNull ModuleRegistry registry) {
        this.registry = registry;
    }
    
    /**
     * 注册所有内置模块
     */
    public void registerAllModules() {
        // 注册数据库模块
        registerDatabaseModule();
        
        // 注册HTTP模块
        registerHttpModule();
        
        // 注册请求模块
        registerRequestModule();
        
        // 注册响应模块  
        registerResponseModule();
        
        // 注册环境模块
        registerEnvironmentModule();
        
        // 注册日志模块
        registerLogModule();
        
        // 注册Magic模块
        registerMagicModule();
    }
    
    /**
     * 注册数据库模块
     */
    private void registerDatabaseModule() {
        List<ApiMethod> methods = new ArrayList<>();
        
        // 查询方法
        methods.add(createMethod("select", "Array", "执行查询并返回结果列表",
                createParam("sql", "String", "SQL查询语句", true),
                createParam("params", "Object", "查询参数", false)
        ));
        
        methods.add(createMethod("selectOne", "Object", "执行查询并返回单个结果",
                createParam("sql", "String", "SQL查询语句", true),
                createParam("params", "Object", "查询参数", false)
        ));
        
        methods.add(createMethod("selectInt", "Integer", "执行查询并返回整数结果",
                createParam("sql", "String", "SQL查询语句", true),
                createParam("params", "Object", "查询参数", false)
        ));
        
        methods.add(createMethod("selectLong", "Long", "执行查询并返回长整数结果",
                createParam("sql", "String", "SQL查询语句", true),
                createParam("params", "Object", "查询参数", false)
        ));
        
        methods.add(createMethod("selectDouble", "Double", "执行查询并返回双精度浮点数结果",
                createParam("sql", "String", "SQL查询语句", true),
                createParam("params", "Object", "查询参数", false)
        ));
        
        methods.add(createMethod("selectValue", "Object", "执行查询并返回单个值",
                createParam("sql", "String", "SQL查询语句", true),
                createParam("params", "Object", "查询参数", false)
        ));
        
        methods.add(createMethod("count", "Integer", "统计查询结果数量",
                createParam("sql", "String", "SQL查询语句", true),
                createParam("params", "Object", "查询参数", false)
        ));
        
        // 更新方法
        methods.add(createMethod("insert", "Integer", "插入数据并返回影响行数",
                createParam("sql", "String", "SQL插入语句", true),
                createParam("params", "Object", "插入参数", false)
        ));
        
        methods.add(createMethod("update", "Integer", "更新数据并返回影响行数",
                createParam("sql", "String", "SQL更新语句", true),
                createParam("params", "Object", "更新参数", false)
        ));
        
        methods.add(createMethod("delete", "Integer", "删除数据并返回影响行数",
                createParam("sql", "String", "SQL删除语句", true),
                createParam("params", "Object", "删除参数", false)
        ));
        
        methods.add(createMethod("batchUpdate", "Integer", "批量更新数据",
                createParam("sql", "String", "SQL更新语句", true),
                createParam("paramsList", "Array", "参数列表", true)
        ));
        
        // 分页方法
        methods.add(createMethod("page", "PageResult", "分页查询数据",
                createParam("sql", "String", "SQL查询语句", true),
                createParam("params", "Object", "查询参数", false),
                createParam("pageNum", "Integer", "页码", true),
                createParam("pageSize", "Integer", "每页大小", true)
        ));
        
        // 事务方法
        methods.add(createMethod("transaction", "TransactionManager", "开启事务",
                createParam("callback", "Function", "事务回调函数", true)
        ));
        
        // 缓存方法
        methods.add(createMethod("cache", "CacheableQuery", "创建可缓存查询",
                createParam("key", "String", "缓存键", true),
                createParam("ttl", "Integer", "过期时间(秒)", false)
        ));
        
        methods.add(createMethod("deleteCache", "Boolean", "删除缓存",
                createParam("key", "String", "缓存键", true)
        ));
        
        // 单表操作方法
        methods.add(createMethod("table", "TableQuery", "单表查询构建器",
                createParam("tableName", "String", "表名", true)
        ));
        
        methods.add(createMethod("logic", "LogicDeleteQuery", "逻辑删除查询构建器",
                createParam("tableName", "String", "表名", true)
        ));
        
        // 列名转换方法
        methods.add(createMethod("camel", "DatabaseQuery", "字段名转驼峰命名",
                createParam("sql", "String", "SQL语句", true)
        ));
        
        methods.add(createMethod("pascal", "DatabaseQuery", "字段名转帕斯卡命名",
                createParam("sql", "String", "SQL语句", true)
        ));
        
        methods.add(createMethod("normal", "DatabaseQuery", "字段名保持原样",
                createParam("sql", "String", "SQL语句", true)
        ));
        
        methods.add(createMethod("upper", "DatabaseQuery", "字段名转大写",
                createParam("sql", "String", "SQL语句", true)
        ));
        
        methods.add(createMethod("lower", "DatabaseQuery", "字段名转小写",
                createParam("sql", "String", "SQL语句", true)
        ));
        
        MagicApiModule dbModule = new MagicApiModule("db", "数据库操作模块，提供SQL查询、更新、分页、事务等功能", methods, "database");
        moduleDefinitions.put("db", dbModule);
        registry.registerModule("db", dbModule);
    }
    
    /**
     * 注册HTTP模块
     */
    private void registerHttpModule() {
        List<ApiMethod> methods = new ArrayList<>();
        
        // 连接和配置方法
        methods.add(createMethod("connect", "HttpRequestBuilder", "创建HTTP连接配置",
                createParam("url", "String", "请求URL", true),
                createParam("timeout", "Integer", "超时时间(毫秒)", false)
        ));
        
        methods.add(createMethod("param", "HttpRequestBuilder", "添加查询参数",
                createParam("name", "String", "参数名", true),
                createParam("value", "String", "参数值", true)
        ));
        
        methods.add(createMethod("data", "HttpRequestBuilder", "设置表单数据",
                createParam("data", "Object", "表单数据", true)
        ));
        
        methods.add(createMethod("header", "HttpRequestBuilder", "设置请求头",
                createParam("name", "String", "头名称", true),
                createParam("value", "String", "头值", true)
        ));
        
        methods.add(createMethod("body", "HttpRequestBuilder", "设置请求体",
                createParam("body", "String", "请求体内容", true)
        ));
        
        methods.add(createMethod("entity", "HttpRequestBuilder", "设置请求实体",
                createParam("entity", "Object", "实体对象", true)
        ));
        
        methods.add(createMethod("contentType", "HttpRequestBuilder", "设置Content-Type",
                createParam("contentType", "String", "内容类型", true)
        ));
        
        methods.add(createMethod("timeout", "HttpRequestBuilder", "设置超时时间",
                createParam("timeout", "Integer", "超时时间(毫秒)", true)
        ));
        
        // HTTP方法
        methods.add(createMethod("get", "HttpResponse", "发送GET请求",
                createParam("url", "String", "请求URL", true),
                createParam("params", "Object", "查询参数", false)
        ));
        
        methods.add(createMethod("post", "HttpResponse", "发送POST请求",
                createParam("url", "String", "请求URL", true),
                createParam("data", "Object", "请求数据", false)
        ));
        
        methods.add(createMethod("put", "HttpResponse", "发送PUT请求",
                createParam("url", "String", "请求URL", true),
                createParam("data", "Object", "请求数据", false)
        ));
        
        methods.add(createMethod("delete", "HttpResponse", "发送DELETE请求",
                createParam("url", "String", "请求URL", true),
                createParam("params", "Object", "查询参数", false)
        ));
        
        methods.add(createMethod("patch", "HttpResponse", "发送PATCH请求",
                createParam("url", "String", "请求URL", true),
                createParam("data", "Object", "请求数据", false)
        ));
        
        methods.add(createMethod("head", "HttpResponse", "发送HEAD请求",
                createParam("url", "String", "请求URL", true)
        ));
        
        methods.add(createMethod("options", "HttpResponse", "发送OPTIONS请求",
                createParam("url", "String", "请求URL", true)
        ));
        
        MagicApiModule httpModule = new MagicApiModule("http", "HTTP请求模块，提供各种HTTP方法和配置选项", methods, "http");
        moduleDefinitions.put("http", httpModule);
        registry.registerModule("http", httpModule);
    }
    
    /**
     * 注册请求模块
     */
    private void registerRequestModule() {
        List<ApiMethod> methods = new ArrayList<>();
        
        // 参数获取
        methods.add(createMethod("getParameter", "String", "获取请求参数",
                createParam("name", "String", "参数名", true),
                createParam("defaultValue", "String", "默认值", false)
        ));
        
        methods.add(createMethod("getParameters", "Map", "获取所有请求参数"));
        
        methods.add(createMethod("getValues", "Array", "获取参数的所有值",
                createParam("name", "String", "参数名", true)
        ));
        
        // 请求头获取
        methods.add(createMethod("getHeader", "String", "获取请求头",
                createParam("name", "String", "头名称", true),
                createParam("defaultValue", "String", "默认值", false)
        ));
        
        methods.add(createMethod("getHeaders", "Map", "获取所有请求头"));
        
        // Cookie获取
        methods.add(createMethod("getCookie", "String", "获取Cookie值",
                createParam("name", "String", "Cookie名", true),
                createParam("defaultValue", "String", "默认值", false)
        ));
        
        methods.add(createMethod("getCookies", "Map", "获取所有Cookie"));
        
        // 请求体
        methods.add(createMethod("getBody", "String", "获取请求体内容"));
        
        methods.add(createMethod("getJson", "Object", "获取JSON格式的请求体"));
        
        methods.add(createMethod("getInputStream", "InputStream", "获取请求输入流"));
        
        // 文件上传
        methods.add(createMethod("getFile", "MultipartFile", "获取上传的文件",
                createParam("name", "String", "文件参数名", true)
        ));
        
        methods.add(createMethod("getFiles", "Array", "获取上传的所有文件",
                createParam("name", "String", "文件参数名", false)
        ));
        
        // 请求信息
        methods.add(createMethod("getMethod", "String", "获取请求方法"));
        
        methods.add(createMethod("getPath", "String", "获取请求路径"));
        
        methods.add(createMethod("getUrl", "String", "获取完整请求URL"));
        
        methods.add(createMethod("getQueryString", "String", "获取查询字符串"));
        
        methods.add(createMethod("getRemoteAddr", "String", "获取客户端IP地址"));
        
        methods.add(createMethod("getUserAgent", "String", "获取用户代理"));
        
        methods.add(createMethod("getReferer", "String", "获取引用页面"));
        
        methods.add(createMethod("getContentType", "String", "获取内容类型"));
        
        methods.add(createMethod("getContentLength", "Integer", "获取内容长度"));
        
        // 会话管理
        methods.add(createMethod("getSession", "HttpSession", "获取会话对象",
                createParam("create", "Boolean", "是否创建新会话", false)
        ));
        
        methods.add(createMethod("getAttribute", "Object", "获取请求属性",
                createParam("name", "String", "属性名", true)
        ));
        
        methods.add(createMethod("setAttribute", "void", "设置请求属性",
                createParam("name", "String", "属性名", true),
                createParam("value", "Object", "属性值", true)
        ));
        
        MagicApiModule requestModule = new MagicApiModule("request", "请求信息模块，提供获取HTTP请求相关信息的方法", methods, "request");
        moduleDefinitions.put("request", requestModule);
        registry.registerModule("request", requestModule);
    }
    
    /**
     * 注册响应模块
     */
    private void registerResponseModule() {
        List<ApiMethod> methods = new ArrayList<>();
        
        // 响应内容
        methods.add(createMethod("json", "ResponseBuilder", "返回JSON响应",
                createParam("data", "Object", "响应数据", true)
        ));
        
        methods.add(createMethod("text", "ResponseBuilder", "返回文本响应",
                createParam("text", "String", "文本内容", true)
        ));
        
        methods.add(createMethod("html", "ResponseBuilder", "返回HTML响应",
                createParam("html", "String", "HTML内容", true)
        ));
        
        methods.add(createMethod("xml", "ResponseBuilder", "返回XML响应",
                createParam("xml", "String", "XML内容", true)
        ));
        
        // 重定向
        methods.add(createMethod("redirect", "ResponseBuilder", "重定向到指定URL",
                createParam("url", "String", "重定向URL", true),
                createParam("permanent", "Boolean", "是否永久重定向", false)
        ));
        
        methods.add(createMethod("forward", "ResponseBuilder", "转发到指定路径",
                createParam("path", "String", "转发路径", true)
        ));
        
        // 文件下载
        methods.add(createMethod("download", "ResponseBuilder", "文件下载响应",
                createParam("file", "Object", "文件对象或路径", true),
                createParam("filename", "String", "下载文件名", false)
        ));
        
        methods.add(createMethod("image", "ResponseBuilder", "图片响应",
                createParam("image", "Object", "图片对象或路径", true),
                createParam("format", "String", "图片格式", false)
        ));
        
        // 响应头设置
        methods.add(createMethod("setHeader", "ResponseBuilder", "设置响应头",
                createParam("name", "String", "头名称", true),
                createParam("value", "String", "头值", true)
        ));
        
        methods.add(createMethod("addHeader", "ResponseBuilder", "添加响应头",
                createParam("name", "String", "头名称", true),
                createParam("value", "String", "头值", true)
        ));
        
        methods.add(createMethod("setContentType", "ResponseBuilder", "设置内容类型",
                createParam("contentType", "String", "内容类型", true)
        ));
        
        methods.add(createMethod("setCharacterEncoding", "ResponseBuilder", "设置字符编码",
                createParam("encoding", "String", "字符编码", true)
        ));
        
        // 状态码
        methods.add(createMethod("setStatus", "ResponseBuilder", "设置状态码",
                createParam("status", "Integer", "HTTP状态码", true)
        ));
        
        methods.add(createMethod("ok", "ResponseBuilder", "设置200状态码"));
        
        methods.add(createMethod("created", "ResponseBuilder", "设置201状态码"));
        
        methods.add(createMethod("noContent", "ResponseBuilder", "设置204状态码"));
        
        methods.add(createMethod("badRequest", "ResponseBuilder", "设置400状态码"));
        
        methods.add(createMethod("unauthorized", "ResponseBuilder", "设置401状态码"));
        
        methods.add(createMethod("forbidden", "ResponseBuilder", "设置403状态码"));
        
        methods.add(createMethod("notFound", "ResponseBuilder", "设置404状态码"));
        
        methods.add(createMethod("internalServerError", "ResponseBuilder", "设置500状态码"));
        
        // Cookie设置
        methods.add(createMethod("setCookie", "ResponseBuilder", "设置Cookie",
                createParam("name", "String", "Cookie名", true),
                createParam("value", "String", "Cookie值", true),
                createParam("maxAge", "Integer", "过期时间(秒)", false),
                createParam("path", "String", "路径", false),
                createParam("domain", "String", "域名", false),
                createParam("secure", "Boolean", "是否安全", false),
                createParam("httpOnly", "Boolean", "是否HttpOnly", false)
        ));
        
        methods.add(createMethod("deleteCookie", "ResponseBuilder", "删除Cookie",
                createParam("name", "String", "Cookie名", true),
                createParam("path", "String", "路径", false),
                createParam("domain", "String", "域名", false)
        ));
        
        // 分页响应
        methods.add(createMethod("page", "ResponseBuilder", "分页数据响应",
                createParam("records", "Array", "数据列表", true),
                createParam("total", "Long", "总记录数", true),
                createParam("current", "Long", "当前页码", true),
                createParam("size", "Long", "每页大小", true)
        ));
        
        MagicApiModule responseModule = new MagicApiModule("response", "响应处理模块，提供各种HTTP响应方法", methods, "response");
        moduleDefinitions.put("response", responseModule);
        registry.registerModule("response", responseModule);
    }
    
    /**
     * 注册环境模块
     */
    private void registerEnvironmentModule() {
        List<ApiMethod> methods = new ArrayList<>();
        
        // 环境变量
        methods.add(createMethod("get", "String", "获取环境变量",
                createParam("name", "String", "变量名", true),
                createParam("defaultValue", "String", "默认值", false)
        ));
        
        methods.add(createMethod("getProperty", "String", "获取系统属性",
                createParam("name", "String", "属性名", true),
                createParam("defaultValue", "String", "默认值", false)
        ));
        
        methods.add(createMethod("getProfile", "String", "获取当前环境配置"));
        
        methods.add(createMethod("isProfile", "Boolean", "检查是否为指定环境",
                createParam("profile", "String", "环境名称", true)
        ));
        
        // 配置获取
        methods.add(createMethod("getConfig", "Object", "获取配置值",
                createParam("key", "String", "配置键", true),
                createParam("defaultValue", "Object", "默认值", false)
        ));
        
        methods.add(createMethod("getString", "String", "获取字符串配置",
                createParam("key", "String", "配置键", true),
                createParam("defaultValue", "String", "默认值", false)
        ));
        
        methods.add(createMethod("getInt", "Integer", "获取整数配置",
                createParam("key", "String", "配置键", true),
                createParam("defaultValue", "Integer", "默认值", false)
        ));
        
        methods.add(createMethod("getBoolean", "Boolean", "获取布尔配置",
                createParam("key", "String", "配置键", true),
                createParam("defaultValue", "Boolean", "默认值", false)
        ));
        
        methods.add(createMethod("getDouble", "Double", "获取双精度配置",
                createParam("key", "String", "配置键", true),
                createParam("defaultValue", "Double", "默认值", false)
        ));
        
        MagicApiModule envModule = new MagicApiModule("env", "环境配置模块，提供环境变量和配置文件访问功能", methods, "environment");
        moduleDefinitions.put("env", envModule);
        registry.registerModule("env", envModule);
    }
    
    /**
     * 注册日志模块
     */
    private void registerLogModule() {
        List<ApiMethod> methods = new ArrayList<>();
        
        // 日志输出方法
        methods.add(createMethod("debug", "void", "输出调试级别日志",
                createParam("message", "String", "日志消息", true),
                createParam("args", "Object[]", "格式化参数", false)
        ));
        
        methods.add(createMethod("info", "void", "输出信息级别日志",
                createParam("message", "String", "日志消息", true),
                createParam("args", "Object[]", "格式化参数", false)
        ));
        
        methods.add(createMethod("warn", "void", "输出警告级别日志",
                createParam("message", "String", "日志消息", true),
                createParam("args", "Object[]", "格式化参数", false)
        ));
        
        methods.add(createMethod("error", "void", "输出错误级别日志",
                createParam("message", "String", "日志消息", true),
                createParam("args", "Object[]", "格式化参数", false)
        ));
        
        methods.add(createMethod("trace", "void", "输出跟踪级别日志",
                createParam("message", "String", "日志消息", true),
                createParam("args", "Object[]", "格式化参数", false)
        ));
        
        // 条件日志
        methods.add(createMethod("isDebugEnabled", "Boolean", "检查是否启用调试级别"));
        
        methods.add(createMethod("isInfoEnabled", "Boolean", "检查是否启用信息级别"));
        
        methods.add(createMethod("isWarnEnabled", "Boolean", "检查是否启用警告级别"));
        
        methods.add(createMethod("isErrorEnabled", "Boolean", "检查是否启用错误级别"));
        
        methods.add(createMethod("isTraceEnabled", "Boolean", "检查是否启用跟踪级别"));
        
        // 格式化输出
        methods.add(createMethod("printf", "void", "格式化日志输出",
                createParam("level", "String", "日志级别", true),
                createParam("format", "String", "格式字符串", true),
                createParam("args", "Object[]", "参数列表", false)
        ));
        
        MagicApiModule logModule = new MagicApiModule("log", "日志输出模块，提供各种级别的日志输出功能", methods, "log");
        moduleDefinitions.put("log", logModule);
        registry.registerModule("log", logModule);
    }
    
    /**
     * 注册Magic模块
     */
    private void registerMagicModule() {
        List<ApiMethod> methods = new ArrayList<>();
        
        // 动态调用
        methods.add(createMethod("call", "Object", "调用其他Magic API接口",
                createParam("path", "String", "接口路径", true),
                createParam("method", "String", "HTTP方法", false),
                createParam("params", "Object", "请求参数", false)
        ));
        
        methods.add(createMethod("execute", "Object", "执行SQL或脚本",
                createParam("script", "String", "脚本内容", true),
                createParam("params", "Object", "执行参数", false)
        ));
        
        methods.add(createMethod("invoke", "Object", "调用Java方法",
                createParam("className", "String", "类名", true),
                createParam("methodName", "String", "方法名", true),
                createParam("args", "Object[]", "方法参数", false)
        ));
        
        // 脚本控制
        methods.add(createMethod("exit", "void", "退出脚本执行",
                createParam("code", "Integer", "退出码", false)
        ));
        
        methods.add(createMethod("sleep", "void", "暂停执行",
                createParam("milliseconds", "Long", "暂停时间(毫秒)", true)
        ));
        
        methods.add(createMethod("assert", "void", "断言检查",
                createParam("condition", "Boolean", "条件表达式", true),
                createParam("message", "String", "错误消息", false)
        ));
        
        // 上下文操作
        methods.add(createMethod("getContext", "Object", "获取执行上下文"));
        
        methods.add(createMethod("setContext", "void", "设置上下文变量",
                createParam("name", "String", "变量名", true),
                createParam("value", "Object", "变量值", true)
        ));
        
        methods.add(createMethod("removeContext", "void", "移除上下文变量",
                createParam("name", "String", "变量名", true)
        ));
        
        // 异常处理
        methods.add(createMethod("throw", "void", "抛出异常",
                createParam("message", "String", "异常消息", true),
                createParam("code", "Integer", "错误码", false)
        ));
        
        methods.add(createMethod("throwIf", "void", "条件抛出异常",
                createParam("condition", "Boolean", "条件表达式", true),
                createParam("message", "String", "异常消息", true),
                createParam("code", "Integer", "错误码", false)
        ));
        
        MagicApiModule magicModule = new MagicApiModule("magic", "Magic API核心模块，提供动态调用和脚本控制功能", methods, "magic");
        moduleDefinitions.put("magic", magicModule);
        registry.registerModule("magic", magicModule);
    }
    
    // ==================== 辅助方法 ====================
    
    /**
     * 创建API方法
     */
    @NotNull
    private ApiMethod createMethod(@NotNull String name, @NotNull String returnType, @NotNull String description, Parameter... parameters) {
        return new ApiMethod(name, description, Arrays.asList(parameters), returnType, "", "", false);
    }
    
    /**
     * 创建参数
     */
    @NotNull
    private Parameter createParam(@NotNull String name, @NotNull String type, @NotNull String description, boolean required) {
        return new Parameter(name, type, required, description, null);
    }
    
    /**
     * 创建参数（无必填要求）
     */
    @NotNull
    private Parameter createParam(@NotNull String name, @NotNull String type, @NotNull String description) {
        return createParam(name, type, description, false);
    }
    
    /**
     * 获取模块定义
     */
    @Nullable
    public MagicApiModule getModuleDefinition(@NotNull String moduleName) {
        return moduleDefinitions.get(moduleName);
    }
    
    /**
     * 获取所有模块定义
     */
    @NotNull
    public Collection<MagicApiModule> getAllModuleDefinitions() {
        return Collections.unmodifiableCollection(moduleDefinitions.values());
    }
    
    /**
     * 检查模块是否存在
     */
    public boolean hasModule(@NotNull String moduleName) {
        return moduleDefinitions.containsKey(moduleName);
    }
}