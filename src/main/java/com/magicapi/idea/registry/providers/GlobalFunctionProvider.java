package com.magicapi.idea.registry.providers;

import com.magicapi.idea.completion.model.ApiMethod;
import com.magicapi.idea.completion.model.Parameter;
import com.magicapi.idea.registry.ModuleRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 全局函数提供器
 * 
 * 负责注册和管理所有Magic API的全局函数
 * 包括聚合函数、数学函数、字符串函数、日期函数、数组函数、工具函数、调试函数等
 */
public class GlobalFunctionProvider {
    
    private final ModuleRegistry registry;
    
    // 全局函数定义缓存
    private final Map<String, List<ApiMethod>> functionCategories = new HashMap<>();
    
    public GlobalFunctionProvider(@NotNull ModuleRegistry registry) {
        this.registry = registry;
    }
    
    /**
     * 注册所有全局函数
     */
    public void registerAllFunctions() {
        // 注册聚合函数
        registerAggregateFunctions();
        
        // 注册数学函数
        registerMathFunctions();
        
        // 注册字符串函数
        registerStringFunctions();
        
        // 注册日期函数
        registerDateFunctions();
        
        // 注册数组构造函数
        registerArrayFunctions();
        
        // 注册工具函数
        registerUtilityFunctions();
        
        // 注册调试函数
        registerDebugFunctions();
    }
    
    /**
     * 注册聚合函数
     */
    private void registerAggregateFunctions() {
        List<ApiMethod> functions = new ArrayList<>();
        
        // 统计聚合函数
        functions.add(createFunction("count", "Integer", "统计记录数量",
                createParam("data", "Array", "数据集合", true)
        ));
        
        functions.add(createFunction("sum", "Number", "求和",
                createParam("data", "Array", "数值集合", true),
                createParam("field", "String", "字段名(可选)", false)
        ));
        
        functions.add(createFunction("max", "Number", "求最大值",
                createParam("data", "Array", "数值集合", true),
                createParam("field", "String", "字段名(可选)", false)
        ));
        
        functions.add(createFunction("min", "Number", "求最小值",
                createParam("data", "Array", "数值集合", true),
                createParam("field", "String", "字段名(可选)", false)
        ));
        
        functions.add(createFunction("avg", "Double", "求平均值",
                createParam("data", "Array", "数值集合", true),
                createParam("field", "String", "字段名(可选)", false)
        ));
        
        functions.add(createFunction("group_concat", "String", "分组连接字符串",
                createParam("data", "Array", "字符串集合", true),
                createParam("separator", "String", "分隔符", false)
        ));
        
        functions.add(createFunction("distinct", "Array", "去重",
                createParam("data", "Array", "数据集合", true),
                createParam("field", "String", "字段名(可选)", false)
        ));
        
        functions.add(createFunction("group_by", "Map", "分组",
                createParam("data", "Array", "数据集合", true),
                createParam("field", "String", "分组字段", true)
        ));
        
        functionCategories.put("aggregate", functions);
        registry.registerGlobalFunctions("aggregate", functions);
    }
    
    /**
     * 注册数学函数
     */
    private void registerMathFunctions() {
        List<ApiMethod> functions = new ArrayList<>();
        
        // 基础数学函数
        functions.add(createFunction("round", "Number", "四舍五入",
                createParam("value", "Number", "数值", true),
                createParam("precision", "Integer", "精度(可选)", false)
        ));
        
        functions.add(createFunction("floor", "Number", "向下取整",
                createParam("value", "Number", "数值", true)
        ));
        
        functions.add(createFunction("ceil", "Number", "向上取整",
                createParam("value", "Number", "数值", true)
        ));
        
        functions.add(createFunction("abs", "Number", "绝对值",
                createParam("value", "Number", "数值", true)
        ));
        
        functions.add(createFunction("sqrt", "Double", "平方根",
                createParam("value", "Number", "数值", true)
        ));
        
        functions.add(createFunction("pow", "Double", "幂运算",
                createParam("base", "Number", "底数", true),
                createParam("exponent", "Number", "指数", true)
        ));
        
        functions.add(createFunction("log", "Double", "自然对数",
                createParam("value", "Number", "数值", true)
        ));
        
        functions.add(createFunction("log10", "Double", "常用对数",
                createParam("value", "Number", "数值", true)
        ));
        
        // 三角函数
        functions.add(createFunction("sin", "Double", "正弦",
                createParam("value", "Number", "角度(弧度)", true)
        ));
        
        functions.add(createFunction("cos", "Double", "余弦",
                createParam("value", "Number", "角度(弧度)", true)
        ));
        
        functions.add(createFunction("tan", "Double", "正切",
                createParam("value", "Number", "角度(弧度)", true)
        ));
        
        // 随机数函数
        functions.add(createFunction("random", "Double", "生成0-1之间的随机数"));
        
        functions.add(createFunction("random_int", "Integer", "生成随机整数",
                createParam("min", "Integer", "最小值", true),
                createParam("max", "Integer", "最大值", true)
        ));
        
        functions.add(createFunction("random_string", "String", "生成随机字符串",
                createParam("length", "Integer", "字符串长度", true),
                createParam("charset", "String", "字符集(可选)", false)
        ));
        
        // 百分比函数
        functions.add(createFunction("percent", "String", "转换为百分比",
                createParam("value", "Number", "数值", true),
                createParam("precision", "Integer", "小数位数(可选)", false)
        ));
        
        functionCategories.put("math", functions);
        registry.registerGlobalFunctions("math", functions);
    }
    
    /**
     * 注册字符串函数
     */
    private void registerStringFunctions() {
        List<ApiMethod> functions = new ArrayList<>();
        
        // UUID和哈希函数
        functions.add(createFunction("uuid", "String", "生成UUID"));
        
        functions.add(createFunction("uuid_simple", "String", "生成简单UUID(不含连字符)"));
        
        functions.add(createFunction("md5", "String", "计算MD5哈希",
                createParam("input", "String", "输入字符串", true)
        ));
        
        functions.add(createFunction("sha1", "String", "计算SHA1哈希",
                createParam("input", "String", "输入字符串", true)
        ));
        
        functions.add(createFunction("sha256", "String", "计算SHA256哈希",
                createParam("input", "String", "输入字符串", true)
        ));
        
        // 编码解码函数
        functions.add(createFunction("base64_encode", "String", "Base64编码",
                createParam("input", "String", "输入字符串", true)
        ));
        
        functions.add(createFunction("base64_decode", "String", "Base64解码",
                createParam("input", "String", "Base64字符串", true)
        ));
        
        functions.add(createFunction("url_encode", "String", "URL编码",
                createParam("input", "String", "输入字符串", true)
        ));
        
        functions.add(createFunction("url_decode", "String", "URL解码",
                createParam("input", "String", "URL编码字符串", true)
        ));
        
        // 字符串操作函数
        functions.add(createFunction("concat", "String", "连接字符串",
                createParam("strings", "String[]", "字符串数组", true)
        ));
        
        functions.add(createFunction("format", "String", "格式化字符串",
                createParam("template", "String", "模板字符串", true),
                createParam("args", "Object[]", "参数列表", true)
        ));
        
        functions.add(createFunction("escape_html", "String", "HTML转义",
                createParam("input", "String", "输入字符串", true)
        ));
        
        functions.add(createFunction("unescape_html", "String", "HTML反转义",
                createParam("input", "String", "HTML转义字符串", true)
        ));
        
        // 字符串验证函数
        functions.add(createFunction("is_blank", "Boolean", "检查字符串是否为空或空白",
                createParam("input", "String", "输入字符串", true)
        ));
        
        functions.add(createFunction("not_blank", "Boolean", "检查字符串非空且非空白",
                createParam("input", "String", "输入字符串", true)
        ));
        
        functions.add(createFunction("is_email", "Boolean", "验证邮箱格式",
                createParam("input", "String", "邮箱字符串", true)
        ));
        
        functions.add(createFunction("is_phone", "Boolean", "验证手机号格式",
                createParam("input", "String", "手机号字符串", true)
        ));
        
        functions.add(createFunction("is_id_card", "Boolean", "验证身份证号格式",
                createParam("input", "String", "身份证号字符串", true)
        ));
        
        functions.add(createFunction("regex_test", "Boolean", "正则表达式测试",
                createParam("input", "String", "输入字符串", true),
                createParam("pattern", "String", "正则表达式", true)
        ));
        
        functionCategories.put("string", functions);
        registry.registerGlobalFunctions("string", functions);
    }
    
    /**
     * 注册日期函数
     */
    private void registerDateFunctions() {
        List<ApiMethod> functions = new ArrayList<>();
        
        // 当前时间函数
        functions.add(createFunction("now", "Long", "获取当前时间戳(毫秒)"));
        
        functions.add(createFunction("current_timestamp", "Long", "获取当前时间戳(秒)"));
        
        functions.add(createFunction("current_date", "String", "获取当前日期(yyyy-MM-dd)"));
        
        functions.add(createFunction("current_time", "String", "获取当前时间(HH:mm:ss)"));
        
        functions.add(createFunction("current_datetime", "String", "获取当前日期时间(yyyy-MM-dd HH:mm:ss)"));
        
        // 日期格式化函数
        functions.add(createFunction("date_format", "String", "格式化日期",
                createParam("date", "Object", "日期对象或时间戳", true),
                createParam("pattern", "String", "格式模式", true)
        ));
        
        functions.add(createFunction("parse_date", "Date", "解析日期字符串",
                createParam("dateString", "String", "日期字符串", true),
                createParam("pattern", "String", "日期格式(可选)", false)
        ));
        
        // 日期计算函数
        functions.add(createFunction("add_days", "Date", "增加天数",
                createParam("date", "Object", "日期对象或时间戳", true),
                createParam("days", "Integer", "天数", true)
        ));
        
        functions.add(createFunction("add_hours", "Date", "增加小时",
                createParam("date", "Object", "日期对象或时间戳", true),
                createParam("hours", "Integer", "小时数", true)
        ));
        
        functions.add(createFunction("add_minutes", "Date", "增加分钟",
                createParam("date", "Object", "日期对象或时间戳", true),
                createParam("minutes", "Integer", "分钟数", true)
        ));
        
        functions.add(createFunction("add_months", "Date", "增加月份",
                createParam("date", "Object", "日期对象或时间戳", true),
                createParam("months", "Integer", "月份数", true)
        ));
        
        functions.add(createFunction("add_years", "Date", "增加年份",
                createParam("date", "Object", "日期对象或时间戳", true),
                createParam("years", "Integer", "年份数", true)
        ));
        
        // 日期比较函数
        functions.add(createFunction("date_diff", "Long", "计算日期差值(天)",
                createParam("date1", "Object", "日期1", true),
                createParam("date2", "Object", "日期2", true)
        ));
        
        functions.add(createFunction("hour_diff", "Long", "计算小时差值",
                createParam("date1", "Object", "日期1", true),
                createParam("date2", "Object", "日期2", true)
        ));
        
        functions.add(createFunction("minute_diff", "Long", "计算分钟差值",
                createParam("date1", "Object", "日期1", true),
                createParam("date2", "Object", "日期2", true)
        ));
        
        // 日期提取函数
        functions.add(createFunction("get_year", "Integer", "提取年份",
                createParam("date", "Object", "日期对象或时间戳", true)
        ));
        
        functions.add(createFunction("get_month", "Integer", "提取月份",
                createParam("date", "Object", "日期对象或时间戳", true)
        ));
        
        functions.add(createFunction("get_day", "Integer", "提取日期",
                createParam("date", "Object", "日期对象或时间戳", true)
        ));
        
        functions.add(createFunction("get_weekday", "Integer", "提取星期几(1-7)",
                createParam("date", "Object", "日期对象或时间戳", true)
        ));
        
        functions.add(createFunction("get_hour", "Integer", "提取小时",
                createParam("date", "Object", "日期对象或时间戳", true)
        ));
        
        functions.add(createFunction("get_minute", "Integer", "提取分钟",
                createParam("date", "Object", "日期对象或时间戳", true)
        ));
        
        functions.add(createFunction("get_second", "Integer", "提取秒数",
                createParam("date", "Object", "日期对象或时间戳", true)
        ));
        
        functionCategories.put("date", functions);
        registry.registerGlobalFunctions("date", functions);
    }
    
    /**
     * 注册数组构造函数
     */
    private void registerArrayFunctions() {
        List<ApiMethod> functions = new ArrayList<>();
        
        // 数组构造函数
        functions.add(createFunction("new_array", "Array", "创建新数组",
                createParam("elements", "Object[]", "元素列表(可选)", false)
        ));
        
        functions.add(createFunction("new_list", "Array", "创建新列表",
                createParam("elements", "Object[]", "元素列表(可选)", false)
        ));
        
        functions.add(createFunction("new_map", "Map", "创建新Map",
                createParam("entries", "Object[]", "键值对(可选)", false)
        ));
        
        functions.add(createFunction("new_set", "Set", "创建新Set",
                createParam("elements", "Object[]", "元素列表(可选)", false)
        ));
        
        // 数组填充函数
        functions.add(createFunction("range", "Array", "创建数值范围数组",
                createParam("start", "Integer", "起始值", true),
                createParam("end", "Integer", "结束值", true),
                createParam("step", "Integer", "步长(可选)", false)
        ));
        
        functions.add(createFunction("repeat", "Array", "重复元素创建数组",
                createParam("element", "Object", "重复元素", true),
                createParam("count", "Integer", "重复次数", true)
        ));
        
        functions.add(createFunction("sequence", "Array", "创建递增序列",
                createParam("count", "Integer", "元素数量", true),
                createParam("start", "Integer", "起始值(可选)", false),
                createParam("step", "Integer", "步长(可选)", false)
        ));
        
        // 数组转换函数
        functions.add(createFunction("to_array", "Array", "转换为数组",
                createParam("obj", "Object", "对象", true)
        ));
        
        functions.add(createFunction("to_list", "Array", "转换为列表",
                createParam("obj", "Object", "对象", true)
        ));
        
        functions.add(createFunction("to_set", "Set", "转换为Set",
                createParam("obj", "Object", "对象", true)
        ));
        
        functions.add(createFunction("to_map", "Map", "转换为Map",
                createParam("obj", "Object", "对象", true)
        ));
        
        functionCategories.put("array", functions);
        registry.registerGlobalFunctions("array", functions);
    }
    
    /**
     * 注册工具函数
     */
    private void registerUtilityFunctions() {
        List<ApiMethod> functions = new ArrayList<>();
        
        // 空值检查函数
        functions.add(createFunction("is_null", "Boolean", "检查是否为null",
                createParam("obj", "Object", "对象", true)
        ));
        
        functions.add(createFunction("not_null", "Boolean", "检查是否非null",
                createParam("obj", "Object", "对象", true)
        ));
        
        functions.add(createFunction("ifnull", "Object", "空值替换",
                createParam("obj", "Object", "对象", true),
                createParam("defaultValue", "Object", "默认值", true)
        ));
        
        functions.add(createFunction("coalesce", "Object", "返回第一个非null值",
                createParam("values", "Object[]", "值列表", true)
        ));
        
        // 类型检查函数
        functions.add(createFunction("typeof", "String", "获取类型名称",
                createParam("obj", "Object", "对象", true)
        ));
        
        functions.add(createFunction("instanceof", "Boolean", "检查实例类型",
                createParam("obj", "Object", "对象", true),
                createParam("type", "String", "类型名", true)
        ));
        
        functions.add(createFunction("is_string", "Boolean", "检查是否为字符串",
                createParam("obj", "Object", "对象", true)
        ));
        
        functions.add(createFunction("is_number", "Boolean", "检查是否为数字",
                createParam("obj", "Object", "对象", true)
        ));
        
        functions.add(createFunction("is_boolean", "Boolean", "检查是否为布尔值",
                createParam("obj", "Object", "对象", true)
        ));
        
        functions.add(createFunction("is_array", "Boolean", "检查是否为数组",
                createParam("obj", "Object", "对象", true)
        ));
        
        functions.add(createFunction("is_map", "Boolean", "检查是否为Map",
                createParam("obj", "Object", "对象", true)
        ));
        
        functions.add(createFunction("is_date", "Boolean", "检查是否为日期",
                createParam("obj", "Object", "对象", true)
        ));
        
        // 类型转换函数
        functions.add(createFunction("to_string", "String", "转换为字符串",
                createParam("obj", "Object", "对象", true)
        ));
        
        functions.add(createFunction("to_int", "Integer", "转换为整数",
                createParam("obj", "Object", "对象", true),
                createParam("defaultValue", "Integer", "默认值(可选)", false)
        ));
        
        functions.add(createFunction("to_double", "Double", "转换为双精度浮点数",
                createParam("obj", "Object", "对象", true),
                createParam("defaultValue", "Double", "默认值(可选)", false)
        ));
        
        functions.add(createFunction("to_boolean", "Boolean", "转换为布尔值",
                createParam("obj", "Object", "对象", true)
        ));
        
        functions.add(createFunction("to_long", "Long", "转换为长整数",
                createParam("obj", "Object", "对象", true),
                createParam("defaultValue", "Long", "默认值(可选)", false)
        ));
        
        // 比较函数
        functions.add(createFunction("equals", "Boolean", "比较两个对象是否相等",
                createParam("obj1", "Object", "对象1", true),
                createParam("obj2", "Object", "对象2", true)
        ));
        
        functions.add(createFunction("compare", "Integer", "比较两个对象",
                createParam("obj1", "Object", "对象1", true),
                createParam("obj2", "Object", "对象2", true)
        ));
        
        // JSON函数
        functions.add(createFunction("to_json", "String", "转换为JSON字符串",
                createParam("obj", "Object", "对象", true)
        ));
        
        functions.add(createFunction("parse_json", "Object", "解析JSON字符串",
                createParam("jsonStr", "String", "JSON字符串", true)
        ));
        
        functionCategories.put("utility", functions);
        registry.registerGlobalFunctions("utility", functions);
    }
    
    /**
     * 注册调试函数
     */
    private void registerDebugFunctions() {
        List<ApiMethod> functions = new ArrayList<>();
        
        // 输出函数
        functions.add(createFunction("print", "void", "输出内容",
                createParam("obj", "Object", "输出内容", true)
        ));
        
        functions.add(createFunction("println", "void", "输出内容并换行",
                createParam("obj", "Object", "输出内容", true)
        ));
        
        functions.add(createFunction("printf", "void", "格式化输出",
                createParam("format", "String", "格式字符串", true),
                createParam("args", "Object[]", "参数列表", false)
        ));
        
        // 日志函数
        functions.add(createFunction("debug", "void", "输出调试日志",
                createParam("message", "String", "日志消息", true),
                createParam("args", "Object[]", "参数列表", false)
        ));
        
        functions.add(createFunction("info", "void", "输出信息日志",
                createParam("message", "String", "日志消息", true),
                createParam("args", "Object[]", "参数列表", false)
        ));
        
        functions.add(createFunction("warn", "void", "输出警告日志",
                createParam("message", "String", "日志消息", true),
                createParam("args", "Object[]", "参数列表", false)
        ));
        
        functions.add(createFunction("error", "void", "输出错误日志",
                createParam("message", "String", "日志消息", true),
                createParam("args", "Object[]", "参数列表", false)
        ));
        
        // 断言函数
        functions.add(createFunction("assert", "void", "断言",
                createParam("condition", "Boolean", "条件表达式", true),
                createParam("message", "String", "错误消息(可选)", false)
        ));
        
        functions.add(createFunction("assert_not_null", "void", "断言非空",
                createParam("obj", "Object", "对象", true),
                createParam("message", "String", "错误消息(可选)", false)
        ));
        
        functions.add(createFunction("assert_equals", "void", "断言相等",
                createParam("expected", "Object", "期望值", true),
                createParam("actual", "Object", "实际值", true),
                createParam("message", "String", "错误消息(可选)", false)
        ));
        
        // 检查函数
        functions.add(createFunction("check", "Boolean", "检查条件",
                createParam("condition", "Boolean", "条件表达式", true),
                createParam("message", "String", "错误消息(可选)", false)
        ));
        
        functions.add(createFunction("validate", "Boolean", "验证对象",
                createParam("obj", "Object", "对象", true),
                createParam("rules", "Object", "验证规则", true)
        ));
        
        // 性能测试函数
        functions.add(createFunction("timer_start", "String", "开始计时",
                createParam("name", "String", "计时器名称", true)
        ));
        
        functions.add(createFunction("timer_end", "Long", "结束计时",
                createParam("name", "String", "计时器名称", true)
        ));
        
        functions.add(createFunction("memory_usage", "Long", "获取内存使用量"));
        
        functions.add(createFunction("gc", "void", "执行垃圾回收"));
        
        functionCategories.put("debug", functions);
        registry.registerGlobalFunctions("debug", functions);
    }
    
    // ==================== 辅助方法 ====================
    
    /**
     * 创建函数定义
     */
    @NotNull
    private ApiMethod createFunction(@NotNull String name, @NotNull String returnType, @NotNull String description, Parameter... parameters) {
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
     * 获取指定类别的函数定义
     */
    @Nullable
    public List<ApiMethod> getFunctionsByCategory(@NotNull String category) {
        return functionCategories.get(category);
    }
    
    /**
     * 获取所有函数类别
     */
    @NotNull
    public Set<String> getFunctionCategories() {
        return Collections.unmodifiableSet(functionCategories.keySet());
    }
    
    /**
     * 获取所有函数定义
     */
    @NotNull
    public List<ApiMethod> getAllFunctions() {
        List<ApiMethod> allFunctions = new ArrayList<>();
        for (List<ApiMethod> functions : functionCategories.values()) {
            allFunctions.addAll(functions);
        }
        return allFunctions;
    }
    
    /**
     * 检查函数是否存在
     */
    public boolean hasFunction(@NotNull String functionName) {
        for (List<ApiMethod> functions : functionCategories.values()) {
            for (ApiMethod function : functions) {
                if (function.getName().equals(functionName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 根据名称查找函数
     */
    @Nullable
    public ApiMethod getFunctionByName(@NotNull String functionName) {
        for (List<ApiMethod> functions : functionCategories.values()) {
            for (ApiMethod function : functions) {
                if (function.getName().equals(functionName)) {
                    return function;
                }
            }
        }
        return null;
    }
    
    /**
     * 搜索函数
     */
    @NotNull
    public List<ApiMethod> searchFunctions(@NotNull String query) {
        List<ApiMethod> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        for (List<ApiMethod> functions : functionCategories.values()) {
            for (ApiMethod function : functions) {
                if (function.getName().toLowerCase().contains(lowerQuery) ||
                    function.getDescription().toLowerCase().contains(lowerQuery)) {
                    results.add(function);
                }
            }
        }
        
        return results;
    }
    
    /**
     * 获取缓存统计信息
     */
    @NotNull
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        
        stats.put("categories", functionCategories.size());
        
        int totalFunctions = 0;
        for (Map.Entry<String, List<ApiMethod>> entry : functionCategories.entrySet()) {
            String category = entry.getKey();
            List<ApiMethod> functions = entry.getValue();
            stats.put(category + "Count", functions.size());
            totalFunctions += functions.size();
        }
        
        stats.put("totalFunctions", totalFunctions);
        
        return stats;
    }
}