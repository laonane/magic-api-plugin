package com.magicapi.idea.registry.providers;

import com.magicapi.idea.completion.model.ApiMethod;
import com.magicapi.idea.completion.model.Parameter;
import com.magicapi.idea.registry.ModuleRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 扩展方法提供器
 * 
 * 负责注册和管理所有Magic API的类型扩展方法
 * 包括String、Number、Array、Map、Date、Boolean、Object等类型的扩展方法
 */
public class ExtensionMethodProvider {
    
    private final ModuleRegistry registry;
    
    // 扩展方法定义缓存
    private final Map<String, List<ApiMethod>> extensionMethods = new HashMap<>();
    
    public ExtensionMethodProvider(@NotNull ModuleRegistry registry) {
        this.registry = registry;
    }
    
    /**
     * 注册所有扩展方法
     */
    public void registerAllMethods() {
        // 注册String扩展方法
        registerStringExtensionMethods();
        
        // 注册Number扩展方法
        registerNumberExtensionMethods();
        
        // 注册Array扩展方法
        registerArrayExtensionMethods();
        
        // 注册Map扩展方法
        registerMapExtensionMethods();
        
        // 注册Date扩展方法
        registerDateExtensionMethods();
        
        // 注册Boolean扩展方法
        registerBooleanExtensionMethods();
        
        // 注册Object扩展方法
        registerObjectExtensionMethods();
    }
    
    /**
     * 注册String扩展方法
     */
    private void registerStringExtensionMethods() {
        List<ApiMethod> methods = new ArrayList<>();
        
        // 字符串检查方法
        methods.add(createMethod("isBlank", "Boolean", "检查字符串是否为空或仅包含空白字符"));
        
        methods.add(createMethod("isNotBlank", "Boolean", "检查字符串不为空且不仅包含空白字符"));
        
        methods.add(createMethod("isEmpty", "Boolean", "检查字符串是否为空"));
        
        methods.add(createMethod("isNotEmpty", "Boolean", "检查字符串不为空"));
        
        methods.add(createMethod("hasText", "Boolean", "检查字符串是否包含文本内容"));
        
        methods.add(createMethod("startsWith", "Boolean", "检查是否以指定前缀开始",
                createParam("prefix", "String", "前缀", true),
                createParam("ignoreCase", "Boolean", "忽略大小写", false)
        ));
        
        methods.add(createMethod("endsWith", "Boolean", "检查是否以指定后缀结束",
                createParam("suffix", "String", "后缀", true),
                createParam("ignoreCase", "Boolean", "忽略大小写", false)
        ));
        
        methods.add(createMethod("contains", "Boolean", "检查是否包含指定子字符串",
                createParam("substring", "String", "子字符串", true),
                createParam("ignoreCase", "Boolean", "忽略大小写", false)
        ));
        
        // 字符串属性方法
        methods.add(createMethod("length", "Integer", "获取字符串长度"));
        
        methods.add(createMethod("size", "Integer", "获取字符串长度"));
        
        methods.add(createMethod("charAt", "String", "获取指定位置的字符",
                createParam("index", "Integer", "字符位置", true)
        ));
        
        methods.add(createMethod("indexOf", "Integer", "查找子字符串首次出现的位置",
                createParam("substring", "String", "子字符串", true),
                createParam("fromIndex", "Integer", "起始位置", false)
        ));
        
        methods.add(createMethod("lastIndexOf", "Integer", "查找子字符串最后出现的位置",
                createParam("substring", "String", "子字符串", true),
                createParam("fromIndex", "Integer", "起始位置", false)
        ));
        
        // 字符串操作方法
        methods.add(createMethod("substring", "String", "提取子字符串",
                createParam("start", "Integer", "起始位置", true),
                createParam("end", "Integer", "结束位置", false)
        ));
        
        methods.add(createMethod("substr", "String", "提取指定长度的子字符串",
                createParam("start", "Integer", "起始位置", true),
                createParam("length", "Integer", "长度", false)
        ));
        
        methods.add(createMethod("left", "String", "提取左侧指定长度的字符",
                createParam("length", "Integer", "长度", true)
        ));
        
        methods.add(createMethod("right", "String", "提取右侧指定长度的字符",
                createParam("length", "Integer", "长度", true)
        ));
        
        methods.add(createMethod("mid", "String", "提取中间部分字符串",
                createParam("start", "Integer", "起始位置", true),
                createParam("length", "Integer", "长度", true)
        ));
        
        // 字符串替换方法
        methods.add(createMethod("replace", "String", "替换所有匹配的子字符串",
                createParam("target", "String", "目标子字符串", true),
                createParam("replacement", "String", "替换字符串", true)
        ));
        
        methods.add(createMethod("replaceAll", "String", "使用正则表达式替换",
                createParam("regex", "String", "正则表达式", true),
                createParam("replacement", "String", "替换字符串", true)
        ));
        
        methods.add(createMethod("replaceFirst", "String", "替换第一个匹配的子字符串",
                createParam("regex", "String", "正则表达式", true),
                createParam("replacement", "String", "替换字符串", true)
        ));
        
        // 字符串格式化方法
        methods.add(createMethod("trim", "String", "去除首尾空白字符"));
        
        methods.add(createMethod("trimLeft", "String", "去除左侧空白字符"));
        
        methods.add(createMethod("trimRight", "String", "去除右侧空白字符"));
        
        methods.add(createMethod("strip", "String", "去除首尾指定字符",
                createParam("chars", "String", "要去除的字符", false)
        ));
        
        methods.add(createMethod("toLowerCase", "String", "转换为小写"));
        
        methods.add(createMethod("toUpperCase", "String", "转换为大写"));
        
        methods.add(createMethod("capitalize", "String", "首字母大写"));
        
        methods.add(createMethod("toCamelCase", "String", "转换为驼峰命名"));
        
        methods.add(createMethod("toPascalCase", "String", "转换为帕斯卡命名"));
        
        methods.add(createMethod("toSnakeCase", "String", "转换为下划线命名"));
        
        methods.add(createMethod("toKebabCase", "String", "转换为短横线命名"));
        
        // 字符串填充方法
        methods.add(createMethod("padStart", "String", "在开始位置填充字符",
                createParam("targetLength", "Integer", "目标长度", true),
                createParam("padString", "String", "填充字符", false)
        ));
        
        methods.add(createMethod("padEnd", "String", "在结束位置填充字符",
                createParam("targetLength", "Integer", "目标长度", true),
                createParam("padString", "String", "填充字符", false)
        ));
        
        methods.add(createMethod("repeat", "String", "重复字符串",
                createParam("count", "Integer", "重复次数", true)
        ));
        
        // 字符串分割方法
        methods.add(createMethod("split", "Array", "分割字符串",
                createParam("delimiter", "String", "分隔符", true),
                createParam("limit", "Integer", "最大分割数", false)
        ));
        
        methods.add(createMethod("splitByRegex", "Array", "使用正则表达式分割字符串",
                createParam("regex", "String", "正则表达式", true),
                createParam("limit", "Integer", "最大分割数", false)
        ));
        
        methods.add(createMethod("lines", "Array", "按行分割字符串"));
        
        methods.add(createMethod("words", "Array", "分割为单词数组"));
        
        // 字符串验证方法
        methods.add(createMethod("matches", "Boolean", "检查是否匹配正则表达式",
                createParam("regex", "String", "正则表达式", true)
        ));
        
        methods.add(createMethod("isNumeric", "Boolean", "检查是否为数字"));
        
        methods.add(createMethod("isAlpha", "Boolean", "检查是否只包含字母"));
        
        methods.add(createMethod("isAlphaNumeric", "Boolean", "检查是否只包含字母和数字"));
        
        methods.add(createMethod("isEmail", "Boolean", "检查是否为有效邮箱格式"));
        
        methods.add(createMethod("isPhone", "Boolean", "检查是否为有效手机号"));
        
        methods.add(createMethod("isUrl", "Boolean", "检查是否为有效URL"));
        
        methods.add(createMethod("isIpAddress", "Boolean", "检查是否为有效IP地址"));
        
        extensionMethods.put("String", methods);
        registry.registerExtensionMethods("String", methods);
    }
    
    /**
     * 注册Number扩展方法
     */
    private void registerNumberExtensionMethods() {
        List<ApiMethod> methods = new ArrayList<>();
        
        // 数学运算方法
        methods.add(createMethod("abs", "Number", "获取绝对值"));
        
        methods.add(createMethod("round", "Number", "四舍五入",
                createParam("precision", "Integer", "小数位数", false)
        ));
        
        methods.add(createMethod("floor", "Number", "向下取整"));
        
        methods.add(createMethod("ceil", "Number", "向上取整"));
        
        methods.add(createMethod("max", "Number", "与另一个数比较取最大值",
                createParam("other", "Number", "另一个数", true)
        ));
        
        methods.add(createMethod("min", "Number", "与另一个数比较取最小值",
                createParam("other", "Number", "另一个数", true)
        ));
        
        methods.add(createMethod("pow", "Number", "幂运算",
                createParam("exponent", "Number", "指数", true)
        ));
        
        methods.add(createMethod("sqrt", "Double", "平方根"));
        
        methods.add(createMethod("cbrt", "Double", "立方根"));
        
        // 格式化方法
        methods.add(createMethod("toFixed", "String", "格式化为定长小数",
                createParam("digits", "Integer", "小数位数", true)
        ));
        
        methods.add(createMethod("toPrecision", "String", "格式化为指定精度",
                createParam("precision", "Integer", "精度", true)
        ));
        
        methods.add(createMethod("toExponential", "String", "转换为科学记数法",
                createParam("fractionDigits", "Integer", "小数位数", false)
        ));
        
        methods.add(createMethod("asPercent", "String", "转换为百分比格式",
                createParam("precision", "Integer", "小数位数", false)
        ));
        
        methods.add(createMethod("asCurrency", "String", "转换为货币格式",
                createParam("currency", "String", "货币符号", false),
                createParam("precision", "Integer", "小数位数", false)
        ));
        
        // 数值检查方法
        methods.add(createMethod("isFinite", "Boolean", "检查是否为有限数"));
        
        methods.add(createMethod("isInfinite", "Boolean", "检查是否为无穷大"));
        
        methods.add(createMethod("isNaN", "Boolean", "检查是否为NaN"));
        
        methods.add(createMethod("isInteger", "Boolean", "检查是否为整数"));
        
        methods.add(createMethod("isPositive", "Boolean", "检查是否为正数"));
        
        methods.add(createMethod("isNegative", "Boolean", "检查是否为负数"));
        
        methods.add(createMethod("isZero", "Boolean", "检查是否为零"));
        
        methods.add(createMethod("isEven", "Boolean", "检查是否为偶数"));
        
        methods.add(createMethod("isOdd", "Boolean", "检查是否为奇数"));
        
        // 范围检查方法
        methods.add(createMethod("between", "Boolean", "检查是否在指定范围内",
                createParam("min", "Number", "最小值", true),
                createParam("max", "Number", "最大值", true),
                createParam("inclusive", "Boolean", "是否包含边界值", false)
        ));
        
        methods.add(createMethod("clamp", "Number", "限制数值在指定范围内",
                createParam("min", "Number", "最小值", true),
                createParam("max", "Number", "最大值", true)
        ));
        
        // 角度转换方法
        methods.add(createMethod("toRadians", "Double", "度转弧度"));
        
        methods.add(createMethod("toDegrees", "Double", "弧度转度"));
        
        // 进制转换方法
        methods.add(createMethod("toBinary", "String", "转换为二进制字符串"));
        
        methods.add(createMethod("toOctal", "String", "转换为八进制字符串"));
        
        methods.add(createMethod("toHex", "String", "转换为十六进制字符串"));
        
        methods.add(createMethod("toBase", "String", "转换为指定进制",
                createParam("radix", "Integer", "进制数", true)
        ));
        
        // 注册到不同的数值类型
        extensionMethods.put("Number", methods);
        extensionMethods.put("Integer", methods);
        extensionMethods.put("Long", methods);
        extensionMethods.put("Double", methods);
        extensionMethods.put("Float", methods);
        
        registry.registerExtensionMethods("Number", methods);
        registry.registerExtensionMethods("Integer", methods);
        registry.registerExtensionMethods("Long", methods);
        registry.registerExtensionMethods("Double", methods);
        registry.registerExtensionMethods("Float", methods);
    }
    
    /**
     * 注册Array扩展方法
     */
    private void registerArrayExtensionMethods() {
        List<ApiMethod> methods = new ArrayList<>();
        
        // 数组属性方法
        methods.add(createMethod("size", "Integer", "获取数组大小"));
        
        methods.add(createMethod("length", "Integer", "获取数组长度"));
        
        methods.add(createMethod("isEmpty", "Boolean", "检查数组是否为空"));
        
        methods.add(createMethod("isNotEmpty", "Boolean", "检查数组是否不为空"));
        
        // 数组访问方法
        methods.add(createMethod("get", "Object", "获取指定位置的元素",
                createParam("index", "Integer", "索引位置", true)
        ));
        
        methods.add(createMethod("set", "Array", "设置指定位置的元素",
                createParam("index", "Integer", "索引位置", true),
                createParam("value", "Object", "新值", true)
        ));
        
        methods.add(createMethod("first", "Object", "获取第一个元素"));
        
        methods.add(createMethod("last", "Object", "获取最后一个元素"));
        
        methods.add(createMethod("nth", "Object", "获取第n个元素",
                createParam("n", "Integer", "位置(从1开始)", true)
        ));
        
        methods.add(createMethod("at", "Object", "获取指定位置的元素(支持负索引)",
                createParam("index", "Integer", "索引位置", true)
        ));
        
        // 数组查找方法
        methods.add(createMethod("contains", "Boolean", "检查是否包含指定元素",
                createParam("element", "Object", "元素", true)
        ));
        
        methods.add(createMethod("indexOf", "Integer", "查找元素首次出现的位置",
                createParam("element", "Object", "元素", true),
                createParam("fromIndex", "Integer", "起始位置", false)
        ));
        
        methods.add(createMethod("lastIndexOf", "Integer", "查找元素最后出现的位置",
                createParam("element", "Object", "元素", true),
                createParam("fromIndex", "Integer", "起始位置", false)
        ));
        
        methods.add(createMethod("find", "Object", "查找符合条件的第一个元素",
                createParam("predicate", "Function", "判断函数", true)
        ));
        
        methods.add(createMethod("findIndex", "Integer", "查找符合条件的第一个元素的索引",
                createParam("predicate", "Function", "判断函数", true)
        ));
        
        methods.add(createMethod("findAll", "Array", "查找所有符合条件的元素",
                createParam("predicate", "Function", "判断函数", true)
        ));
        
        // 数组操作方法
        methods.add(createMethod("push", "Array", "在末尾添加元素",
                createParam("elements", "Object[]", "要添加的元素", true)
        ));
        
        methods.add(createMethod("pop", "Object", "移除并返回最后一个元素"));
        
        methods.add(createMethod("unshift", "Array", "在开头添加元素",
                createParam("elements", "Object[]", "要添加的元素", true)
        ));
        
        methods.add(createMethod("shift", "Object", "移除并返回第一个元素"));
        
        methods.add(createMethod("insert", "Array", "在指定位置插入元素",
                createParam("index", "Integer", "插入位置", true),
                createParam("elements", "Object[]", "要插入的元素", true)
        ));
        
        methods.add(createMethod("remove", "Array", "移除指定位置的元素",
                createParam("index", "Integer", "位置", true),
                createParam("count", "Integer", "移除数量", false)
        ));
        
        methods.add(createMethod("removeElement", "Array", "移除指定元素",
                createParam("element", "Object", "要移除的元素", true),
                createParam("all", "Boolean", "是否移除所有匹配", false)
        ));
        
        methods.add(createMethod("clear", "Array", "清空数组"));
        
        // 数组转换方法
        methods.add(createMethod("map", "Array", "映射转换每个元素",
                createParam("mapper", "Function", "映射函数", true)
        ));
        
        methods.add(createMethod("filter", "Array", "过滤符合条件的元素",
                createParam("predicate", "Function", "判断函数", true)
        ));
        
        methods.add(createMethod("reduce", "Object", "归约操作",
                createParam("reducer", "Function", "归约函数", true),
                createParam("initialValue", "Object", "初始值", false)
        ));
        
        methods.add(createMethod("flatMap", "Array", "平铺映射",
                createParam("mapper", "Function", "映射函数", true)
        ));
        
        methods.add(createMethod("flatten", "Array", "平铺数组",
                createParam("depth", "Integer", "平铺深度", false)
        ));
        
        // 数组排序方法
        methods.add(createMethod("sort", "Array", "排序",
                createParam("comparator", "Function", "比较函数", false)
        ));
        
        methods.add(createMethod("sortBy", "Array", "按字段排序",
                createParam("field", "String", "排序字段", true),
                createParam("ascending", "Boolean", "是否升序", false)
        ));
        
        methods.add(createMethod("reverse", "Array", "反转数组"));
        
        methods.add(createMethod("shuffle", "Array", "随机打乱"));
        
        // 数组切片方法
        methods.add(createMethod("slice", "Array", "切片",
                createParam("start", "Integer", "起始位置", true),
                createParam("end", "Integer", "结束位置", false)
        ));
        
        methods.add(createMethod("take", "Array", "取前n个元素",
                createParam("count", "Integer", "数量", true)
        ));
        
        methods.add(createMethod("skip", "Array", "跳过前n个元素",
                createParam("count", "Integer", "数量", true)
        ));
        
        methods.add(createMethod("limit", "Array", "限制数组大小",
                createParam("maxSize", "Integer", "最大大小", true)
        ));
        
        // 数组合并方法
        methods.add(createMethod("concat", "Array", "连接其他数组",
                createParam("arrays", "Array[]", "要连接的数组", true)
        ));
        
        methods.add(createMethod("union", "Array", "求并集",
                createParam("other", "Array", "另一个数组", true)
        ));
        
        methods.add(createMethod("intersect", "Array", "求交集",
                createParam("other", "Array", "另一个数组", true)
        ));
        
        methods.add(createMethod("difference", "Array", "求差集",
                createParam("other", "Array", "另一个数组", true)
        ));
        
        methods.add(createMethod("zip", "Array", "组合两个数组",
                createParam("other", "Array", "另一个数组", true)
        ));
        
        // 数组去重方法
        methods.add(createMethod("distinct", "Array", "去重"));
        
        methods.add(createMethod("distinctBy", "Array", "按字段去重",
                createParam("field", "String", "去重字段", true)
        ));
        
        methods.add(createMethod("unique", "Array", "去重(别名)"));
        
        // 数组统计方法
        methods.add(createMethod("count", "Integer", "统计元素数量",
                createParam("predicate", "Function", "判断函数", false)
        ));
        
        methods.add(createMethod("sum", "Number", "求和",
                createParam("mapper", "Function", "映射函数", false)
        ));
        
        methods.add(createMethod("average", "Double", "求平均值",
                createParam("mapper", "Function", "映射函数", false)
        ));
        
        methods.add(createMethod("max", "Object", "求最大值",
                createParam("comparator", "Function", "比较函数", false)
        ));
        
        methods.add(createMethod("min", "Object", "求最小值",
                createParam("comparator", "Function", "比较函数", false)
        ));
        
        // 数组连接方法
        methods.add(createMethod("join", "String", "连接为字符串",
                createParam("separator", "String", "分隔符", false)
        ));
        
        methods.add(createMethod("mkString", "String", "连接为字符串",
                createParam("prefix", "String", "前缀", false),
                createParam("separator", "String", "分隔符", false),
                createParam("suffix", "String", "后缀", false)
        ));
        
        // 数组判断方法
        methods.add(createMethod("every", "Boolean", "检查所有元素是否符合条件",
                createParam("predicate", "Function", "判断函数", true)
        ));
        
        methods.add(createMethod("some", "Boolean", "检查是否有元素符合条件",
                createParam("predicate", "Function", "判断函数", true)
        ));
        
        methods.add(createMethod("none", "Boolean", "检查是否没有元素符合条件",
                createParam("predicate", "Function", "判断函数", true)
        ));
        
        // 数组遍历方法
        methods.add(createMethod("forEach", "void", "遍历每个元素",
                createParam("action", "Function", "操作函数", true)
        ));
        
        methods.add(createMethod("each", "Array", "遍历每个元素(返回原数组)",
                createParam("action", "Function", "操作函数", true)
        ));
        
        extensionMethods.put("Array", methods);
        extensionMethods.put("List", methods);
        
        registry.registerExtensionMethods("Array", methods);
        registry.registerExtensionMethods("List", methods);
    }
    
    /**
     * 注册Map扩展方法
     */
    private void registerMapExtensionMethods() {
        List<ApiMethod> methods = new ArrayList<>();
        
        // Map基本操作
        methods.add(createMethod("get", "Object", "获取指定键的值",
                createParam("key", "Object", "键", true),
                createParam("defaultValue", "Object", "默认值", false)
        ));
        
        methods.add(createMethod("put", "Object", "设置键值对",
                createParam("key", "Object", "键", true),
                createParam("value", "Object", "值", true)
        ));
        
        methods.add(createMethod("remove", "Object", "移除指定键",
                createParam("key", "Object", "键", true)
        ));
        
        methods.add(createMethod("clear", "Map", "清空Map"));
        
        // Map查询方法
        methods.add(createMethod("containsKey", "Boolean", "检查是否包含指定键",
                createParam("key", "Object", "键", true)
        ));
        
        methods.add(createMethod("containsValue", "Boolean", "检查是否包含指定值",
                createParam("value", "Object", "值", true)
        ));
        
        methods.add(createMethod("isEmpty", "Boolean", "检查Map是否为空"));
        
        methods.add(createMethod("isNotEmpty", "Boolean", "检查Map是否不为空"));
        
        methods.add(createMethod("size", "Integer", "获取Map大小"));
        
        // Map集合操作
        methods.add(createMethod("keys", "Array", "获取所有键"));
        
        methods.add(createMethod("values", "Array", "获取所有值"));
        
        methods.add(createMethod("entries", "Array", "获取所有键值对"));
        
        methods.add(createMethod("keySet", "Set", "获取键集合"));
        
        methods.add(createMethod("valueSet", "Set", "获取值集合"));
        
        methods.add(createMethod("entrySet", "Set", "获取键值对集合"));
        
        // Map合并操作
        methods.add(createMethod("merge", "Map", "合并其他Map",
                createParam("other", "Map", "另一个Map", true),
                createParam("overwrite", "Boolean", "是否覆盖", false)
        ));
        
        methods.add(createMethod("putAll", "Map", "添加所有键值对",
                createParam("other", "Map", "另一个Map", true)
        ));
        
        // Map转换操作
        methods.add(createMethod("map", "Map", "转换值",
                createParam("mapper", "Function", "转换函数", true)
        ));
        
        methods.add(createMethod("mapKeys", "Map", "转换键",
                createParam("mapper", "Function", "转换函数", true)
        ));
        
        methods.add(createMethod("mapValues", "Map", "转换值",
                createParam("mapper", "Function", "转换函数", true)
        ));
        
        methods.add(createMethod("filter", "Map", "过滤键值对",
                createParam("predicate", "Function", "判断函数", true)
        ));
        
        methods.add(createMethod("filterKeys", "Map", "按键过滤",
                createParam("predicate", "Function", "判断函数", true)
        ));
        
        methods.add(createMethod("filterValues", "Map", "按值过滤",
                createParam("predicate", "Function", "判断函数", true)
        ));
        
        // Map遍历操作
        methods.add(createMethod("forEach", "void", "遍历每个键值对",
                createParam("action", "Function", "操作函数", true)
        ));
        
        methods.add(createMethod("each", "Map", "遍历每个键值对(返回原Map)",
                createParam("action", "Function", "操作函数", true)
        ));
        
        // Map默认值操作
        methods.add(createMethod("getOrDefault", "Object", "获取值或默认值",
                createParam("key", "Object", "键", true),
                createParam("defaultValue", "Object", "默认值", true)
        ));
        
        methods.add(createMethod("putIfAbsent", "Object", "键不存在时添加",
                createParam("key", "Object", "键", true),
                createParam("value", "Object", "值", true)
        ));
        
        methods.add(createMethod("computeIfAbsent", "Object", "键不存在时计算值",
                createParam("key", "Object", "键", true),
                createParam("function", "Function", "计算函数", true)
        ));
        
        methods.add(createMethod("computeIfPresent", "Object", "键存在时重新计算值",
                createParam("key", "Object", "键", true),
                createParam("function", "Function", "计算函数", true)
        ));
        
        extensionMethods.put("Map", methods);
        registry.registerExtensionMethods("Map", methods);
    }
    
    /**
     * 注册Date扩展方法
     */
    private void registerDateExtensionMethods() {
        List<ApiMethod> methods = new ArrayList<>();
        
        // 日期格式化
        methods.add(createMethod("format", "String", "格式化日期",
                createParam("pattern", "String", "格式模式", true)
        ));
        
        methods.add(createMethod("toISOString", "String", "转换为ISO字符串"));
        
        methods.add(createMethod("toDateString", "String", "转换为日期字符串"));
        
        methods.add(createMethod("toTimeString", "String", "转换为时间字符串"));
        
        methods.add(createMethod("toString", "String", "转换为字符串",
                createParam("pattern", "String", "格式模式", false)
        ));
        
        // 日期计算
        methods.add(createMethod("addYears", "Date", "增加年份",
                createParam("years", "Integer", "年数", true)
        ));
        
        methods.add(createMethod("addMonths", "Date", "增加月份",
                createParam("months", "Integer", "月数", true)
        ));
        
        methods.add(createMethod("addWeeks", "Date", "增加周数",
                createParam("weeks", "Integer", "周数", true)
        ));
        
        methods.add(createMethod("addDays", "Date", "增加天数",
                createParam("days", "Integer", "天数", true)
        ));
        
        methods.add(createMethod("addHours", "Date", "增加小时",
                createParam("hours", "Integer", "小时数", true)
        ));
        
        methods.add(createMethod("addMinutes", "Date", "增加分钟",
                createParam("minutes", "Integer", "分钟数", true)
        ));
        
        methods.add(createMethod("addSeconds", "Date", "增加秒数",
                createParam("seconds", "Integer", "秒数", true)
        ));
        
        methods.add(createMethod("addMilliseconds", "Date", "增加毫秒",
                createParam("milliseconds", "Integer", "毫秒数", true)
        ));
        
        // 日期比较
        methods.add(createMethod("isAfter", "Boolean", "检查是否晚于指定日期",
                createParam("other", "Date", "另一个日期", true)
        ));
        
        methods.add(createMethod("isBefore", "Boolean", "检查是否早于指定日期",
                createParam("other", "Date", "另一个日期", true)
        ));
        
        methods.add(createMethod("isEqual", "Boolean", "检查是否等于指定日期",
                createParam("other", "Date", "另一个日期", true)
        ));
        
        methods.add(createMethod("isSameDay", "Boolean", "检查是否为同一天",
                createParam("other", "Date", "另一个日期", true)
        ));
        
        methods.add(createMethod("isSameMonth", "Boolean", "检查是否为同一月",
                createParam("other", "Date", "另一个日期", true)
        ));
        
        methods.add(createMethod("isSameYear", "Boolean", "检查是否为同一年",
                createParam("other", "Date", "另一个日期", true)
        ));
        
        // 日期差值计算
        methods.add(createMethod("diffYears", "Long", "计算年份差",
                createParam("other", "Date", "另一个日期", true)
        ));
        
        methods.add(createMethod("diffMonths", "Long", "计算月份差",
                createParam("other", "Date", "另一个日期", true)
        ));
        
        methods.add(createMethod("diffWeeks", "Long", "计算周数差",
                createParam("other", "Date", "另一个日期", true)
        ));
        
        methods.add(createMethod("diffDays", "Long", "计算天数差",
                createParam("other", "Date", "另一个日期", true)
        ));
        
        methods.add(createMethod("diffHours", "Long", "计算小时差",
                createParam("other", "Date", "另一个日期", true)
        ));
        
        methods.add(createMethod("diffMinutes", "Long", "计算分钟差",
                createParam("other", "Date", "另一个日期", true)
        ));
        
        methods.add(createMethod("diffSeconds", "Long", "计算秒数差",
                createParam("other", "Date", "另一个日期", true)
        ));
        
        methods.add(createMethod("diffMilliseconds", "Long", "计算毫秒差",
                createParam("other", "Date", "另一个日期", true)
        ));
        
        // 日期属性获取
        methods.add(createMethod("getYear", "Integer", "获取年份"));
        
        methods.add(createMethod("getMonth", "Integer", "获取月份"));
        
        methods.add(createMethod("getDay", "Integer", "获取日期"));
        
        methods.add(createMethod("getWeekday", "Integer", "获取星期几"));
        
        methods.add(createMethod("getHour", "Integer", "获取小时"));
        
        methods.add(createMethod("getMinute", "Integer", "获取分钟"));
        
        methods.add(createMethod("getSecond", "Integer", "获取秒数"));
        
        methods.add(createMethod("getMillisecond", "Integer", "获取毫秒"));
        
        methods.add(createMethod("getTime", "Long", "获取时间戳"));
        
        methods.add(createMethod("getDayOfYear", "Integer", "获取一年中的天数"));
        
        methods.add(createMethod("getWeekOfYear", "Integer", "获取一年中的周数"));
        
        // 日期设置
        methods.add(createMethod("withYear", "Date", "设置年份",
                createParam("year", "Integer", "年份", true)
        ));
        
        methods.add(createMethod("withMonth", "Date", "设置月份",
                createParam("month", "Integer", "月份", true)
        ));
        
        methods.add(createMethod("withDay", "Date", "设置日期",
                createParam("day", "Integer", "日期", true)
        ));
        
        methods.add(createMethod("withHour", "Date", "设置小时",
                createParam("hour", "Integer", "小时", true)
        ));
        
        methods.add(createMethod("withMinute", "Date", "设置分钟",
                createParam("minute", "Integer", "分钟", true)
        ));
        
        methods.add(createMethod("withSecond", "Date", "设置秒数",
                createParam("second", "Integer", "秒数", true)
        ));
        
        // 日期截取
        methods.add(createMethod("startOfYear", "Date", "获取年初"));
        
        methods.add(createMethod("endOfYear", "Date", "获取年末"));
        
        methods.add(createMethod("startOfMonth", "Date", "获取月初"));
        
        methods.add(createMethod("endOfMonth", "Date", "获取月末"));
        
        methods.add(createMethod("startOfWeek", "Date", "获取周初"));
        
        methods.add(createMethod("endOfWeek", "Date", "获取周末"));
        
        methods.add(createMethod("startOfDay", "Date", "获取当天开始"));
        
        methods.add(createMethod("endOfDay", "Date", "获取当天结束"));
        
        extensionMethods.put("Date", methods);
        registry.registerExtensionMethods("Date", methods);
    }
    
    /**
     * 注册Boolean扩展方法
     */
    private void registerBooleanExtensionMethods() {
        List<ApiMethod> methods = new ArrayList<>();
        
        // 逻辑运算
        methods.add(createMethod("and", "Boolean", "逻辑与运算",
                createParam("other", "Boolean", "另一个布尔值", true)
        ));
        
        methods.add(createMethod("or", "Boolean", "逻辑或运算",
                createParam("other", "Boolean", "另一个布尔值", true)
        ));
        
        methods.add(createMethod("not", "Boolean", "逻辑非运算"));
        
        methods.add(createMethod("xor", "Boolean", "逻辑异或运算",
                createParam("other", "Boolean", "另一个布尔值", true)
        ));
        
        methods.add(createMethod("nand", "Boolean", "逻辑与非运算",
                createParam("other", "Boolean", "另一个布尔值", true)
        ));
        
        methods.add(createMethod("nor", "Boolean", "逻辑或非运算",
                createParam("other", "Boolean", "另一个布尔值", true)
        ));
        
        // 条件运算
        methods.add(createMethod("then", "Object", "条件为真时执行",
                createParam("value", "Object", "返回值", true)
        ));
        
        methods.add(createMethod("otherwise", "Object", "条件为假时执行",
                createParam("value", "Object", "返回值", true)
        ));
        
        methods.add(createMethod("ifTrue", "Object", "为真时返回值",
                createParam("trueValue", "Object", "真值", true),
                createParam("falseValue", "Object", "假值", false)
        ));
        
        methods.add(createMethod("ifFalse", "Object", "为假时返回值",
                createParam("falseValue", "Object", "假值", true),
                createParam("trueValue", "Object", "真值", false)
        ));
        
        // 转换方法
        methods.add(createMethod("toString", "String", "转换为字符串"));
        
        methods.add(createMethod("toInt", "Integer", "转换为整数(true=1, false=0)"));
        
        methods.add(createMethod("toYesNo", "String", "转换为Yes/No"));
        
        methods.add(createMethod("toOnOff", "String", "转换为On/Off"));
        
        methods.add(createMethod("toTrueFalse", "String", "转换为True/False"));
        
        // 比较方法
        methods.add(createMethod("equals", "Boolean", "比较是否相等",
                createParam("other", "Boolean", "另一个布尔值", true)
        ));
        
        methods.add(createMethod("compare", "Integer", "比较布尔值",
                createParam("other", "Boolean", "另一个布尔值", true)
        ));
        
        extensionMethods.put("Boolean", methods);
        registry.registerExtensionMethods("Boolean", methods);
    }
    
    /**
     * 注册Object扩展方法
     */
    private void registerObjectExtensionMethods() {
        List<ApiMethod> methods = new ArrayList<>();
        
        // 类型转换方法
        methods.add(createMethod("asString", "String", "转换为字符串"));
        
        methods.add(createMethod("asInt", "Integer", "转换为整数",
                createParam("defaultValue", "Integer", "默认值", false)
        ));
        
        methods.add(createMethod("asLong", "Long", "转换为长整数",
                createParam("defaultValue", "Long", "默认值", false)
        ));
        
        methods.add(createMethod("asDouble", "Double", "转换为双精度浮点数",
                createParam("defaultValue", "Double", "默认值", false)
        ));
        
        methods.add(createMethod("asFloat", "Float", "转换为单精度浮点数",
                createParam("defaultValue", "Float", "默认值", false)
        ));
        
        methods.add(createMethod("asBoolean", "Boolean", "转换为布尔值"));
        
        methods.add(createMethod("asDate", "Date", "转换为日期",
                createParam("pattern", "String", "日期格式", false)
        ));
        
        methods.add(createMethod("asArray", "Array", "转换为数组"));
        
        methods.add(createMethod("asMap", "Map", "转换为Map"));
        
        // 类型检查方法
        methods.add(createMethod("is", "Boolean", "检查类型",
                createParam("type", "String", "类型名", true)
        ));
        
        methods.add(createMethod("isString", "Boolean", "检查是否为字符串"));
        
        methods.add(createMethod("isNumber", "Boolean", "检查是否为数字"));
        
        methods.add(createMethod("isInteger", "Boolean", "检查是否为整数"));
        
        methods.add(createMethod("isFloat", "Boolean", "检查是否为浮点数"));
        
        methods.add(createMethod("isBoolean", "Boolean", "检查是否为布尔值"));
        
        methods.add(createMethod("isArray", "Boolean", "检查是否为数组"));
        
        methods.add(createMethod("isList", "Boolean", "检查是否为列表"));
        
        methods.add(createMethod("isMap", "Boolean", "检查是否为Map"));
        
        methods.add(createMethod("isSet", "Boolean", "检查是否为Set"));
        
        methods.add(createMethod("isDate", "Boolean", "检查是否为日期"));
        
        methods.add(createMethod("isFunction", "Boolean", "检查是否为函数"));
        
        // 空值检查方法
        methods.add(createMethod("isNull", "Boolean", "检查是否为null"));
        
        methods.add(createMethod("isNotNull", "Boolean", "检查是否不为null"));
        
        methods.add(createMethod("isEmpty", "Boolean", "检查是否为空"));
        
        methods.add(createMethod("isNotEmpty", "Boolean", "检查是否不为空"));
        
        methods.add(createMethod("isBlank", "Boolean", "检查是否为空白"));
        
        methods.add(createMethod("isNotBlank", "Boolean", "检查是否不为空白"));
        
        // 默认值方法
        methods.add(createMethod("orElse", "Object", "为null时返回默认值",
                createParam("defaultValue", "Object", "默认值", true)
        ));
        
        methods.add(createMethod("orElseGet", "Object", "为null时执行函数获取默认值",
                createParam("supplier", "Function", "供应函数", true)
        ));
        
        methods.add(createMethod("orElseThrow", "Object", "为null时抛出异常",
                createParam("exception", "String", "异常消息", false)
        ));
        
        // 对象操作方法
        methods.add(createMethod("clone", "Object", "克隆对象"));
        
        methods.add(createMethod("deepClone", "Object", "深度克隆对象"));
        
        methods.add(createMethod("equals", "Boolean", "比较对象是否相等",
                createParam("other", "Object", "另一个对象", true)
        ));
        
        methods.add(createMethod("hashCode", "Integer", "获取哈希码"));
        
        methods.add(createMethod("toString", "String", "转换为字符串"));
        
        methods.add(createMethod("getClass", "String", "获取类名"));
        
        methods.add(createMethod("getType", "String", "获取类型名"));
        
        // JSON操作方法
        methods.add(createMethod("toJson", "String", "转换为JSON字符串",
                createParam("pretty", "Boolean", "是否格式化", false)
        ));
        
        methods.add(createMethod("toXml", "String", "转换为XML字符串",
                createParam("rootName", "String", "根元素名", false)
        ));
        
        methods.add(createMethod("toYaml", "String", "转换为YAML字符串"));
        
        // 反射操作方法
        methods.add(createMethod("hasProperty", "Boolean", "检查是否有属性",
                createParam("propertyName", "String", "属性名", true)
        ));
        
        methods.add(createMethod("getProperty", "Object", "获取属性值",
                createParam("propertyName", "String", "属性名", true),
                createParam("defaultValue", "Object", "默认值", false)
        ));
        
        methods.add(createMethod("setProperty", "Object", "设置属性值",
                createParam("propertyName", "String", "属性名", true),
                createParam("value", "Object", "属性值", true)
        ));
        
        methods.add(createMethod("hasMethod", "Boolean", "检查是否有方法",
                createParam("methodName", "String", "方法名", true)
        ));
        
        methods.add(createMethod("invokeMethod", "Object", "调用方法",
                createParam("methodName", "String", "方法名", true),
                createParam("args", "Object[]", "方法参数", false)
        ));
        
        extensionMethods.put("Object", methods);
        registry.registerExtensionMethods("Object", methods);
    }
    
    // ==================== 辅助方法 ====================
    
    /**
     * 创建扩展方法定义
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
     * 获取指定类型的扩展方法
     */
    @Nullable
    public List<ApiMethod> getExtensionMethodsByType(@NotNull String typeName) {
        return extensionMethods.get(typeName);
    }
    
    /**
     * 获取所有支持扩展方法的类型
     */
    @NotNull
    public Set<String> getSupportedTypes() {
        return Collections.unmodifiableSet(extensionMethods.keySet());
    }
    
    /**
     * 获取所有扩展方法
     */
    @NotNull
    public List<ApiMethod> getAllExtensionMethods() {
        List<ApiMethod> allMethods = new ArrayList<>();
        for (List<ApiMethod> methods : extensionMethods.values()) {
            allMethods.addAll(methods);
        }
        return allMethods;
    }
    
    /**
     * 检查类型是否支持扩展方法
     */
    public boolean supportsType(@NotNull String typeName) {
        return extensionMethods.containsKey(typeName);
    }
    
    /**
     * 检查指定类型是否有指定的扩展方法
     */
    public boolean hasMethod(@NotNull String typeName, @NotNull String methodName) {
        List<ApiMethod> methods = extensionMethods.get(typeName);
        if (methods != null) {
            for (ApiMethod method : methods) {
                if (method.getName().equals(methodName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 根据类型和方法名查找扩展方法
     */
    @Nullable
    public ApiMethod getExtensionMethod(@NotNull String typeName, @NotNull String methodName) {
        List<ApiMethod> methods = extensionMethods.get(typeName);
        if (methods != null) {
            for (ApiMethod method : methods) {
                if (method.getName().equals(methodName)) {
                    return method;
                }
            }
        }
        return null;
    }
    
    /**
     * 搜索扩展方法
     */
    @NotNull
    public List<ApiMethod> searchExtensionMethods(@NotNull String query) {
        List<ApiMethod> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        for (List<ApiMethod> methods : extensionMethods.values()) {
            for (ApiMethod method : methods) {
                if (method.getName().toLowerCase().contains(lowerQuery) ||
                    method.getDescription().toLowerCase().contains(lowerQuery)) {
                    results.add(method);
                }
            }
        }
        
        return results;
    }
    
    /**
     * 获取统计信息
     */
    @NotNull
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        
        stats.put("supportedTypes", extensionMethods.size());
        
        int totalMethods = 0;
        for (Map.Entry<String, List<ApiMethod>> entry : extensionMethods.entrySet()) {
            String type = entry.getKey();
            List<ApiMethod> methods = entry.getValue();
            stats.put(type + "MethodCount", methods.size());
            totalMethods += methods.size();
        }
        
        stats.put("totalExtensionMethods", totalMethods);
        
        return stats;
    }
}