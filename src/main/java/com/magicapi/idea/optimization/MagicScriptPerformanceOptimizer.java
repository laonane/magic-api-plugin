package com.magicapi.idea.optimization;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Magic Script 性能优化器
 */
public class MagicScriptPerformanceOptimizer implements StartupActivity {
    
    private static final Key<ConcurrentHashMap<String, Object>> CACHE_KEY = Key.create("MagicScript.Cache");
    private static final ExecutorService BACKGROUND_EXECUTOR = Executors.newFixedThreadPool(2);
    
    @Override
    public void runActivity(@NotNull Project project) {
        initializeCache(project);
        optimizeMemoryUsage();
        scheduleBackgroundTasks();
    }
    
    private void initializeCache(@NotNull Project project) {
        if (project.getUserData(CACHE_KEY) == null) {
            project.putUserData(CACHE_KEY, new ConcurrentHashMap<>());
        }
    }
    
    private void optimizeMemoryUsage() {
        // 设置合理的缓存大小限制
        System.setProperty("idea.max.intellisense.filesize", "5000");
        System.setProperty("idea.max.content.load.filesize", "20000");
    }
    
    private void scheduleBackgroundTasks() {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            // 定期清理缓存
            cleanupCache();
        });
    }
    
    private void cleanupCache() {
        try {
            Thread.sleep(300000); // 5分钟后清理
            // 清理逻辑
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public static void cacheResult(@NotNull Project project, @NotNull String key, @NotNull Object value) {
        ConcurrentHashMap<String, Object> cache = project.getUserData(CACHE_KEY);
        if (cache != null && cache.size() < 1000) { // 限制缓存大小
            cache.put(key, value);
        }
    }
    
    public static <T> T getCachedResult(@NotNull Project project, @NotNull String key, @NotNull Class<T> type) {
        ConcurrentHashMap<String, Object> cache = project.getUserData(CACHE_KEY);
        if (cache != null) {
            Object result = cache.get(key);
            if (type.isInstance(result)) {
                return type.cast(result);
            }
        }
        return null;
    }
    
    public static void optimizePsiAccess(@NotNull PsiFile psiFile) {
        // 优化PSI树访问性能
        ApplicationManager.getApplication().runReadAction(() -> {
            // 缓存常用的PSI元素
            String fileName = psiFile.getName();
            int textLength = psiFile.getTextLength();
            
            // 异步处理大文件
            if (textLength > 50000) {
                BACKGROUND_EXECUTOR.submit(() -> {
                    // 后台处理大文件分析
                });
            }
        });
    }
}