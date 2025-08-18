package com.magicapi.idea.completion.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Magic API 定义提供器
 * 提供硬编码的Magic API模块和方法定义
 */
public class MagicApiDefinitions {
    
    private static final Map<String, MagicApiModule> MODULES = new HashMap<>();
    
    static {
        initializeModules();
    }
    
    private static void initializeModules() {
        // 数据库模块
        MODULES.put("db", createDatabaseModule());
        
        // HTTP模块
        MODULES.put("http", createHttpModule());
        
        // 请求模块
        MODULES.put("request", createRequestModule());
        
        // 响应模块
        MODULES.put("response", createResponseModule());
        
        // 环境模块
        MODULES.put("env", createEnvironmentModule());
        
        // 日志模块
        MODULES.put("log", createLogModule());
    }
    
    private static MagicApiModule createDatabaseModule() {
        List<ApiMethod> methods = Arrays.asList(
            new ApiMethod("select", "执行查询SQL，返回结果集", 
                Arrays.asList(
                    new Parameter("sql", "String", true, "SQL查询语句"),
                    new Parameter("params", "Object...", false, "SQL参数", "[]")
                ),
                "List<Map<String,Object>>", "查询结果列表",
                "db.select('SELECT * FROM users WHERE age > ?', 18)", false),
                
            new ApiMethod("selectOne", "执行查询SQL，返回单个结果", 
                Arrays.asList(
                    new Parameter("sql", "String", true, "SQL查询语句"),
                    new Parameter("params", "Object...", false, "SQL参数", "[]")
                ),
                "Map<String,Object>", "查询结果对象",
                "db.selectOne('SELECT * FROM users WHERE id = ?', 1)", false),
                
            new ApiMethod("selectInt", "执行查询SQL，返回整数值", 
                Arrays.asList(
                    new Parameter("sql", "String", true, "SQL查询语句"),
                    new Parameter("params", "Object...", false, "SQL参数", "[]")
                ),
                "Integer", "查询结果整数",
                "db.selectInt('SELECT COUNT(*) FROM users')", false),
                
            new ApiMethod("selectValue", "执行查询SQL，返回单个值", 
                Arrays.asList(
                    new Parameter("sql", "String", true, "SQL查询语句"),
                    new Parameter("params", "Object...", false, "SQL参数", "[]")
                ),
                "Object", "查询结果值",
                "db.selectValue('SELECT name FROM users WHERE id = ?', 1)", false),
                
            new ApiMethod("insert", "执行插入SQL", 
                Arrays.asList(
                    new Parameter("sql", "String", true, "SQL插入语句"),
                    new Parameter("params", "Object...", false, "SQL参数", "[]")
                ),
                "Integer", "影响行数",
                "db.insert('INSERT INTO users(name, age) VALUES(?, ?)', 'Tom', 25)", false),
                
            new ApiMethod("update", "执行更新SQL", 
                Arrays.asList(
                    new Parameter("sql", "String", true, "SQL更新语句"),
                    new Parameter("params", "Object...", false, "SQL参数", "[]")
                ),
                "Integer", "影响行数",
                "db.update('UPDATE users SET age = ? WHERE id = ?', 26, 1)", false),
                
            new ApiMethod("delete", "执行删除SQL", 
                Arrays.asList(
                    new Parameter("sql", "String", true, "SQL删除语句"),
                    new Parameter("params", "Object...", false, "SQL参数", "[]")
                ),
                "Integer", "影响行数",
                "db.delete('DELETE FROM users WHERE id = ?', 1)", false),
                
            new ApiMethod("page", "分页查询", 
                Arrays.asList(
                    new Parameter("sql", "String", true, "SQL查询语句"),
                    new Parameter("params", "Object...", false, "SQL参数", "[]")
                ),
                "PageResult", "分页结果",
                "db.page('SELECT * FROM users')", false),
                
            new ApiMethod("transaction", "执行事务", 
                Arrays.asList(
                    new Parameter("callback", "Function", true, "事务回调函数")
                ),
                "Object", "事务结果",
                "db.transaction(() => { /* 事务逻辑 */ })", false)
        );
        
        return new MagicApiModule("db", "数据库操作模块", methods, "database");
    }
    
    private static MagicApiModule createHttpModule() {
        List<ApiMethod> methods = Arrays.asList(
            new ApiMethod("get", "发送GET请求", 
                Arrays.asList(
                    new Parameter("url", "String", true, "请求URL"),
                    new Parameter("headers", "Map<String,String>", false, "请求头", "{}")
                ),
                "HttpResponse", "HTTP响应对象",
                "http.get('https://api.example.com/users')", true),
                
            new ApiMethod("post", "发送POST请求", 
                Arrays.asList(
                    new Parameter("url", "String", true, "请求URL"),
                    new Parameter("body", "Object", false, "请求体", "null"),
                    new Parameter("headers", "Map<String,String>", false, "请求头", "{}")
                ),
                "HttpResponse", "HTTP响应对象",
                "http.post('https://api.example.com/users', {name: 'Tom'})", true),
                
            new ApiMethod("put", "发送PUT请求", 
                Arrays.asList(
                    new Parameter("url", "String", true, "请求URL"),
                    new Parameter("body", "Object", false, "请求体", "null"),
                    new Parameter("headers", "Map<String,String>", false, "请求头", "{}")
                ),
                "HttpResponse", "HTTP响应对象",
                "http.put('https://api.example.com/users/1', {name: 'Tom'})", true),
                
            new ApiMethod("delete", "发送DELETE请求", 
                Arrays.asList(
                    new Parameter("url", "String", true, "请求URL"),
                    new Parameter("headers", "Map<String,String>", false, "请求头", "{}")
                ),
                "HttpResponse", "HTTP响应对象",
                "http.delete('https://api.example.com/users/1')", true),
                
            new ApiMethod("patch", "发送PATCH请求", 
                Arrays.asList(
                    new Parameter("url", "String", true, "请求URL"),
                    new Parameter("body", "Object", false, "请求体", "null"),
                    new Parameter("headers", "Map<String,String>", false, "请求头", "{}")
                ),
                "HttpResponse", "HTTP响应对象",
                "http.patch('https://api.example.com/users/1', {age: 26})", true)
        );
        
        return new MagicApiModule("http", "HTTP请求模块", methods, "http");
    }
    
    private static MagicApiModule createRequestModule() {
        List<ApiMethod> methods = Arrays.asList(
            new ApiMethod("getParameter", "获取请求参数", 
                Arrays.asList(
                    new Parameter("name", "String", true, "参数名称"),
                    new Parameter("defaultValue", "String", false, "默认值", "null")
                ),
                "String", "参数值",
                "request.getParameter('id', '0')", false),
                
            new ApiMethod("getHeader", "获取请求头", 
                Arrays.asList(
                    new Parameter("name", "String", true, "请求头名称"),
                    new Parameter("defaultValue", "String", false, "默认值", "null")
                ),
                "String", "请求头值",
                "request.getHeader('Content-Type')", false),
                
            new ApiMethod("getBody", "获取请求体", 
                Arrays.asList(),
                "Object", "请求体对象",
                "request.getBody()", false),
                
            new ApiMethod("getMethod", "获取请求方法", 
                Arrays.asList(),
                "String", "请求方法",
                "request.getMethod()", false),
                
            new ApiMethod("getPath", "获取请求路径", 
                Arrays.asList(),
                "String", "请求路径",
                "request.getPath()", false),
                
            new ApiMethod("getCookie", "获取Cookie值", 
                Arrays.asList(
                    new Parameter("name", "String", true, "Cookie名称"),
                    new Parameter("defaultValue", "String", false, "默认值", "null")
                ),
                "String", "Cookie值",
                "request.getCookie('sessionId')", false),
                
            new ApiMethod("getSession", "获取Session属性", 
                Arrays.asList(
                    new Parameter("name", "String", true, "属性名称"),
                    new Parameter("defaultValue", "Object", false, "默认值", "null")
                ),
                "Object", "Session属性值",
                "request.getSession('userId')", false)
        );
        
        return new MagicApiModule("request", "请求信息模块", methods, "request");
    }
    
    private static MagicApiModule createResponseModule() {
        List<ApiMethod> methods = Arrays.asList(
            new ApiMethod("json", "返回JSON响应", 
                Arrays.asList(
                    new Parameter("data", "Object", true, "响应数据")
                ),
                "JsonResponse", "JSON响应对象",
                "response.json({code: 200, data: users})", true),
                
            new ApiMethod("text", "返回文本响应", 
                Arrays.asList(
                    new Parameter("text", "String", true, "响应文本")
                ),
                "TextResponse", "文本响应对象",
                "response.text('Hello World')", true),
                
            new ApiMethod("setHeader", "设置响应头", 
                Arrays.asList(
                    new Parameter("name", "String", true, "响应头名称"),
                    new Parameter("value", "String", true, "响应头值")
                ),
                "ResponseBuilder", "响应构建器",
                "response.setHeader('Content-Type', 'application/json')", true),
                
            new ApiMethod("setStatus", "设置响应状态码", 
                Arrays.asList(
                    new Parameter("status", "Integer", true, "HTTP状态码")
                ),
                "ResponseBuilder", "响应构建器",
                "response.setStatus(404)", true),
                
            new ApiMethod("setCookie", "设置Cookie", 
                Arrays.asList(
                    new Parameter("name", "String", true, "Cookie名称"),
                    new Parameter("value", "String", true, "Cookie值"),
                    new Parameter("maxAge", "Integer", false, "过期时间(秒)", "-1")
                ),
                "ResponseBuilder", "响应构建器",
                "response.setCookie('sessionId', 'abc123', 3600)", true)
        );
        
        return new MagicApiModule("response", "响应处理模块", methods, "response");
    }
    
    private static MagicApiModule createEnvironmentModule() {
        List<ApiMethod> methods = Arrays.asList(
            new ApiMethod("get", "获取环境变量", 
                Arrays.asList(
                    new Parameter("key", "String", true, "环境变量名"),
                    new Parameter("defaultValue", "String", false, "默认值", "null")
                ),
                "String", "环境变量值",
                "env.get('DATABASE_URL')", false),
                
            new ApiMethod("getProperty", "获取系统属性", 
                Arrays.asList(
                    new Parameter("key", "String", true, "属性名"),
                    new Parameter("defaultValue", "String", false, "默认值", "null")
                ),
                "String", "系统属性值",
                "env.getProperty('java.version')", false)
        );
        
        return new MagicApiModule("env", "环境变量模块", methods, "environment");
    }
    
    private static MagicApiModule createLogModule() {
        List<ApiMethod> methods = Arrays.asList(
            new ApiMethod("info", "记录INFO级别日志", 
                Arrays.asList(
                    new Parameter("message", "String", true, "日志消息"),
                    new Parameter("args", "Object...", false, "格式化参数", "[]")
                ),
                "void", "无返回值",
                "log.info('用户{}登录成功', userId)", false),
                
            new ApiMethod("debug", "记录DEBUG级别日志", 
                Arrays.asList(
                    new Parameter("message", "String", true, "日志消息"),
                    new Parameter("args", "Object...", false, "格式化参数", "[]")
                ),
                "void", "无返回值",
                "log.debug('调试信息: {}', data)", false),
                
            new ApiMethod("warn", "记录WARN级别日志", 
                Arrays.asList(
                    new Parameter("message", "String", true, "日志消息"),
                    new Parameter("args", "Object...", false, "格式化参数", "[]")
                ),
                "void", "无返回值",
                "log.warn('警告: {}', warning)", false),
                
            new ApiMethod("error", "记录ERROR级别日志", 
                Arrays.asList(
                    new Parameter("message", "String", true, "日志消息"),
                    new Parameter("args", "Object...", false, "格式化参数", "[]")
                ),
                "void", "无返回值",
                "log.error('错误: {}', error)", false)
        );
        
        return new MagicApiModule("log", "日志记录模块", methods, "log");
    }
    
    /**
     * 获取所有模块
     */
    public static Map<String, MagicApiModule> getAllModules() {
        return new HashMap<>(MODULES);
    }
    
    /**
     * 获取指定模块
     */
    public static MagicApiModule getModule(String moduleName) {
        return MODULES.get(moduleName);
    }
    
    /**
     * 获取所有模块名称
     */
    public static String[] getModuleNames() {
        return MODULES.keySet().toArray(new String[0]);
    }
    
    /**
     * 检查是否为有效的模块名
     */
    public static boolean isValidModule(String moduleName) {
        return MODULES.containsKey(moduleName);
    }
}