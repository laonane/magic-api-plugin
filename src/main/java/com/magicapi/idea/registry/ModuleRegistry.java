package com.magicapi.idea.registry;

import com.magicapi.idea.registry.providers.BuiltinModuleProvider;
import com.magicapi.idea.registry.providers.GlobalFunctionProvider;
import com.magicapi.idea.registry.providers.ExtensionMethodProvider;
import com.magicapi.idea.registry.providers.TypeSystemProvider;
import com.magicapi.idea.completion.model.ApiMethod;
import com.magicapi.idea.completion.model.MagicApiModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Magic API 模块注册中心
 * 
 * 负责管理所有的API模块、全局函数、扩展方法和类型定义
 * 提供统一的访问接口和缓存机制
 */
public class ModuleRegistry {
    
    // 单例实例
    private static volatile ModuleRegistry instance;
    
    // 核心提供器
    private final BuiltinModuleProvider builtinModuleProvider;
    private final GlobalFunctionProvider globalFunctionProvider;
    private final ExtensionMethodProvider extensionMethodProvider;
    private final TypeSystemProvider typeSystemProvider;
    
    // 缓存
    private final Map<String, MagicApiModule> moduleCache = new ConcurrentHashMap<>();
    private final Map<String, List<ApiMethod>> globalFunctionCache = new ConcurrentHashMap<>();
    private final Map<String, List<ApiMethod>> extensionMethodCache = new ConcurrentHashMap<>();
    private final Map<String, String> typeCache = new ConcurrentHashMap<>();
    
    // 版本兼容性
    private final Map<String, Set<String>> versionCompatibility = new ConcurrentHashMap<>();
    private String currentApiVersion = "2.7.0";
    
    private ModuleRegistry() {
        this.builtinModuleProvider = new BuiltinModuleProvider(this);
        this.globalFunctionProvider = new GlobalFunctionProvider(this);
        this.extensionMethodProvider = new ExtensionMethodProvider(this);
        this.typeSystemProvider = new TypeSystemProvider(this);
        
        // 初始化注册表
        initialize();
    }
    
    /**
     * 获取单例实例
     */
    @NotNull
    public static ModuleRegistry getInstance() {
        if (instance == null) {
            synchronized (ModuleRegistry.class) {
                if (instance == null) {
                    instance = new ModuleRegistry();
                }
            }
        }
        return instance;
    }
    
    /**
     * 初始化注册表
     */
    private void initialize() {
        // 注册内置模块
        builtinModuleProvider.registerAllModules();
        
        // 注册全局函数
        globalFunctionProvider.registerAllFunctions();
        
        // 注册扩展方法
        extensionMethodProvider.registerAllMethods();
        
        // 初始化类型系统
        typeSystemProvider.initializeTypeSystem();
        
        // 设置版本兼容性
        initializeVersionCompatibility();
    }
    
    // ==================== 模块管理 ====================
    
    /**
     * 获取内置模块
     */
    @Nullable
    public MagicApiModule getModule(@NotNull String moduleName) {
        return moduleCache.get(moduleName);
    }
    
    /**
     * 获取所有模块名称
     */
    @NotNull
    public Set<String> getModuleNames() {
        return Collections.unmodifiableSet(moduleCache.keySet());
    }
    
    /**
     * 获取所有模块
     */
    @NotNull
    public Collection<MagicApiModule> getAllModules() {
        return Collections.unmodifiableCollection(moduleCache.values());
    }
    
    /**
     * 注册模块
     */
    public void registerModule(@NotNull String name, @NotNull MagicApiModule module) {
        moduleCache.put(name, module);
    }
    
    /**
     * 检查模块是否存在
     */
    public boolean hasModule(@NotNull String moduleName) {
        return moduleCache.containsKey(moduleName);
    }
    
    // ==================== 全局函数管理 ====================
    
    /**
     * 获取所有全局函数
     */
    @NotNull
    public List<ApiMethod> getGlobalFunctions() {
        return globalFunctionCache.getOrDefault("all", Collections.emptyList());
    }
    
    /**
     * 获取指定类别的全局函数
     */
    @NotNull
    public List<ApiMethod> getGlobalFunctionsByCategory(@NotNull String category) {
        return globalFunctionCache.getOrDefault(category, Collections.emptyList());
    }
    
    /**
     * 获取所有全局函数类别
     */
    @NotNull
    public Set<String> getGlobalFunctionCategories() {
        return Collections.unmodifiableSet(globalFunctionCache.keySet());
    }
    
    /**
     * 注册全局函数
     */
    public void registerGlobalFunction(@NotNull String category, @NotNull ApiMethod function) {
        globalFunctionCache.computeIfAbsent(category, k -> new ArrayList<>()).add(function);
        globalFunctionCache.computeIfAbsent("all", k -> new ArrayList<>()).add(function);
    }
    
    /**
     * 批量注册全局函数
     */
    public void registerGlobalFunctions(@NotNull String category, @NotNull List<ApiMethod> functions) {
        globalFunctionCache.computeIfAbsent(category, k -> new ArrayList<>()).addAll(functions);
        globalFunctionCache.computeIfAbsent("all", k -> new ArrayList<>()).addAll(functions);
    }
    
    // ==================== 扩展方法管理 ====================
    
    /**
     * 获取指定类型的扩展方法
     */
    @NotNull
    public List<ApiMethod> getExtensionMethods(@NotNull String typeName) {
        return extensionMethodCache.getOrDefault(typeName, Collections.emptyList());
    }
    
    /**
     * 获取所有支持扩展方法的类型
     */
    @NotNull
    public Set<String> getExtensionMethodTypes() {
        return Collections.unmodifiableSet(extensionMethodCache.keySet());
    }
    
    /**
     * 注册扩展方法
     */
    public void registerExtensionMethod(@NotNull String typeName, @NotNull ApiMethod method) {
        extensionMethodCache.computeIfAbsent(typeName, k -> new ArrayList<>()).add(method);
    }
    
    /**
     * 批量注册扩展方法
     */
    public void registerExtensionMethods(@NotNull String typeName, @NotNull List<ApiMethod> methods) {
        extensionMethodCache.computeIfAbsent(typeName, k -> new ArrayList<>()).addAll(methods);
    }
    
    // ==================== 类型系统管理 ====================
    
    /**
     * 获取类型信息
     */
    @Nullable
    public String getTypeInfo(@NotNull String typeName) {
        return typeCache.get(typeName);
    }
    
    /**
     * 推断表达式类型
     */
    @NotNull
    public String inferType(@NotNull String expression) {
        return typeSystemProvider.inferExpressionType(expression);
    }
    
    /**
     * 获取方法返回类型
     */
    @NotNull
    public String getMethodReturnType(@NotNull String objectType, @NotNull String methodName) {
        return typeSystemProvider.getMethodReturnType(objectType, methodName);
    }
    
    /**
     * 注册类型信息
     */
    public void registerType(@NotNull String typeName, @NotNull String typeInfo) {
        typeCache.put(typeName, typeInfo);
    }
    
    // ==================== 版本兼容性管理 ====================
    
    /**
     * 设置当前API版本
     */
    public void setApiVersion(@NotNull String version) {
        this.currentApiVersion = version;
    }
    
    /**
     * 获取当前API版本
     */
    @NotNull
    public String getApiVersion() {
        return currentApiVersion;
    }
    
    /**
     * 检查功能是否在当前版本可用
     */
    public boolean isFeatureAvailable(@NotNull String featureName) {
        Set<String> supportedVersions = versionCompatibility.get(featureName);
        return supportedVersions != null && supportedVersions.contains(currentApiVersion);
    }
    
    /**
     * 注册功能版本兼容性
     */
    public void registerFeatureCompatibility(@NotNull String featureName, @NotNull Set<String> supportedVersions) {
        versionCompatibility.put(featureName, supportedVersions);
    }
    
    /**
     * 初始化版本兼容性
     */
    private void initializeVersionCompatibility() {
        // 定义各个功能的版本支持
        registerFeatureCompatibility("db.cache", Set.of("2.5.0", "2.6.0", "2.7.0"));
        registerFeatureCompatibility("http.timeout", Set.of("2.6.0", "2.7.0"));
        registerFeatureCompatibility("response.download", Set.of("2.4.0", "2.5.0", "2.6.0", "2.7.0"));
        registerFeatureCompatibility("magic.invoke", Set.of("2.7.0"));
        
        // 全局函数版本支持
        registerFeatureCompatibility("uuid", Set.of("2.3.0", "2.4.0", "2.5.0", "2.6.0", "2.7.0"));
        registerFeatureCompatibility("group_concat", Set.of("2.5.0", "2.6.0", "2.7.0"));
        registerFeatureCompatibility("percent", Set.of("2.6.0", "2.7.0"));
        
        // 扩展方法版本支持
        registerFeatureCompatibility("string.isBlank", Set.of("2.4.0", "2.5.0", "2.6.0", "2.7.0"));
        registerFeatureCompatibility("array.distinct", Set.of("2.5.0", "2.6.0", "2.7.0"));
        registerFeatureCompatibility("map.merge", Set.of("2.6.0", "2.7.0"));
    }
    
    // ==================== 搜索和查询 ====================
    
    /**
     * 搜索API方法
     */
    @NotNull
    public List<ApiMethod> searchMethods(@NotNull String query) {
        List<ApiMethod> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        // 搜索模块方法
        for (MagicApiModule module : moduleCache.values()) {
            for (ApiMethod method : module.getMethods()) {
                if (method.getName().toLowerCase().contains(lowerQuery) ||
                    method.getDescription().toLowerCase().contains(lowerQuery)) {
                    results.add(method);
                }
            }
        }
        
        // 搜索全局函数
        for (ApiMethod function : getGlobalFunctions()) {
            if (function.getName().toLowerCase().contains(lowerQuery) ||
                function.getDescription().toLowerCase().contains(lowerQuery)) {
                results.add(function);
            }
        }
        
        // 搜索扩展方法
        for (List<ApiMethod> methods : extensionMethodCache.values()) {
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
     * 按类别获取方法
     */
    @NotNull
    public List<ApiMethod> getMethodsByCategory(@NotNull String category) {
        List<ApiMethod> results = new ArrayList<>();
        
        switch (category.toLowerCase()) {
            case "database":
            case "db":
                MagicApiModule dbModule = getModule("db");
                if (dbModule != null) {
                    results.addAll(dbModule.getMethods());
                }
                break;
                
            case "http":
            case "network":
                MagicApiModule httpModule = getModule("http");
                if (httpModule != null) {
                    results.addAll(httpModule.getMethods());
                }
                break;
                
            case "global":
            case "function":
                results.addAll(getGlobalFunctions());
                break;
                
            case "string":
                results.addAll(getExtensionMethods("String"));
                break;
                
            case "array":
            case "list":
                results.addAll(getExtensionMethods("Array"));
                results.addAll(getExtensionMethods("List"));
                break;
                
            default:
                // 尝试作为扩展方法类型
                results.addAll(getExtensionMethods(category));
                break;
        }
        
        return results;
    }
    
    // ==================== 缓存管理 ====================
    
    /**
     * 清空缓存
     */
    public void clearCache() {
        moduleCache.clear();
        globalFunctionCache.clear();
        extensionMethodCache.clear();
        typeCache.clear();
    }
    
    /**
     * 重新加载注册表
     */
    public void reload() {
        clearCache();
        initialize();
    }
    
    /**
     * 获取缓存统计信息
     */
    @NotNull
    public Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("modules", moduleCache.size());
        stats.put("globalFunctionCategories", globalFunctionCache.size());
        stats.put("extensionMethodTypes", extensionMethodCache.size());
        stats.put("types", typeCache.size());
        stats.put("versionCompatibility", versionCompatibility.size());
        
        // 计算总方法数
        int totalMethods = 0;
        for (MagicApiModule module : moduleCache.values()) {
            totalMethods += module.getMethods().size();
        }
        stats.put("totalModuleMethods", totalMethods);
        
        int totalGlobalFunctions = getGlobalFunctions().size();
        stats.put("totalGlobalFunctions", totalGlobalFunctions);
        
        int totalExtensionMethods = extensionMethodCache.values().stream()
                .mapToInt(List::size).sum();
        stats.put("totalExtensionMethods", totalExtensionMethods);
        
        return stats;
    }
    
    // ==================== 访问器方法 ====================
    
    /**
     * 获取内置模块提供器
     */
    @NotNull
    public BuiltinModuleProvider getBuiltinModuleProvider() {
        return builtinModuleProvider;
    }
    
    /**
     * 获取全局函数提供器
     */
    @NotNull
    public GlobalFunctionProvider getGlobalFunctionProvider() {
        return globalFunctionProvider;
    }
    
    /**
     * 获取扩展方法提供器
     */
    @NotNull
    public ExtensionMethodProvider getExtensionMethodProvider() {
        return extensionMethodProvider;
    }
    
    /**
     * 获取类型系统提供器
     */
    @NotNull
    public TypeSystemProvider getTypeSystemProvider() {
        return typeSystemProvider;
    }
}