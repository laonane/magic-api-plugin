package com.magicapi.idea.navigation;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.magicapi.idea.lang.MagicScriptFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Import语句引用实现
 * 处理import语句到对应文件的跳转
 */
public class ImportReference extends PsiReferenceBase<PsiElement> {
    
    private final String importPath;
    
    public ImportReference(@NotNull PsiElement element) {
        super(element, new TextRange(1, element.getTextLength() - 1)); // 排除引号
        
        // 提取引号内的路径
        String text = element.getText();
        if (text.length() >= 2 && text.startsWith("\"") && text.endsWith("\"")) {
            this.importPath = text.substring(1, text.length() - 1);
        } else {
            this.importPath = text;
        }
    }
    
    @Override
    @Nullable
    public PsiElement resolve() {
        if (importPath.isEmpty()) {
            return null;
        }
        
        Project project = myElement.getProject();
        
        // 1. 处理相对路径导入
        if (importPath.startsWith("./") || importPath.startsWith("../")) {
            return resolveRelativeImport(project);
        }
        
        // 2. 处理绝对路径导入
        if (importPath.startsWith("/")) {
            return resolveAbsoluteImport(project);
        }
        
        // 3. 处理内置模块导入
        if (importPath.startsWith("/builtin/")) {
            return resolveBuiltinModule();
        }
        
        // 4. 处理普通模块导入（在项目中搜索）
        return resolveProjectModule(project);
    }
    
    @Override
    @NotNull
    public Object[] getVariants() {
        // 提供可导入的模块建议
        List<String> variants = new ArrayList<>();
        
        // 内置模块
        String[] builtinModules = {"db", "http", "request", "response", "env", "log", "magic"};
        for (String module : builtinModules) {
            variants.add("\"/builtin/" + module + "\"");
        }
        
        // 项目中的.ms文件
        Project project = myElement.getProject();
        Collection<VirtualFile> msFiles = FileTypeIndex.getFiles(
            MagicScriptFileType.INSTANCE,
            GlobalSearchScope.projectScope(project)
        );
        
        for (VirtualFile file : msFiles) {
            String path = getRelativePath(file);
            if (path != null && !path.isEmpty()) {
                variants.add("\"" + path + "\"");
            }
        }
        
        // 常用的相对路径
        variants.add("\"./utils\"");
        variants.add("\"../common/helper\"");
        variants.add("\"/common/utils\"");
        
        return variants.toArray();
    }
    
    /**
     * 解析相对路径导入
     */
    @Nullable
    private PsiElement resolveRelativeImport(@NotNull Project project) {
        PsiFile currentFile = myElement.getContainingFile();
        if (currentFile == null) {
            return null;
        }
        
        VirtualFile currentVFile = currentFile.getVirtualFile();
        if (currentVFile == null) {
            return null;
        }
        
        VirtualFile parentDir = currentVFile.getParent();
        if (parentDir == null) {
            return null;
        }
        
        // 处理相对路径
        String resolvedPath = resolvePathSegments(parentDir.getPath(), importPath);
        
        // 查找对应的文件
        return findFileByPath(project, resolvedPath);
    }
    
    /**
     * 解析绝对路径导入
     */
    @Nullable
    private PsiElement resolveAbsoluteImport(@NotNull Project project) {
        // 从项目根目录开始查找
        VirtualFile projectBase = project.getBaseDir();
        if (projectBase == null) {
            return null;
        }
        
        String fullPath = projectBase.getPath() + importPath;
        return findFileByPath(project, fullPath);
    }
    
    /**
     * 解析内置模块
     */
    @Nullable
    private PsiElement resolveBuiltinModule() {
        // 内置模块不对应具体文件，暂时返回原始元素
        return myElement;
    }
    
    /**
     * 解析项目模块
     */
    @Nullable
    private PsiElement resolveProjectModule(@NotNull Project project) {
        // 在项目中搜索匹配的文件
        Collection<VirtualFile> msFiles = FileTypeIndex.getFiles(
            MagicScriptFileType.INSTANCE,
            GlobalSearchScope.projectScope(project)
        );
        
        for (VirtualFile file : msFiles) {
            String fileName = file.getNameWithoutExtension();
            if (fileName.equals(importPath) || file.getName().equals(importPath)) {
                PsiManager psiManager = PsiManager.getInstance(project);
                return psiManager.findFile(file);
            }
        }
        
        return null;
    }
    
    /**
     * 根据路径查找文件
     */
    @Nullable
    private PsiElement findFileByPath(@NotNull Project project, @NotNull String path) {
        // 尝试不同的文件扩展名
        String[] extensions = {".ms", ".magic", ""};
        
        for (String ext : extensions) {
            String fullPath = path + ext;
            VirtualFile file = project.getBaseDir();
            
            if (file != null) {
                VirtualFile targetFile = file.getFileSystem().findFileByPath(fullPath);
                if (targetFile != null && targetFile.exists()) {
                    PsiManager psiManager = PsiManager.getInstance(project);
                    return psiManager.findFile(targetFile);
                }
            }
        }
        
        return null;
    }
    
    /**
     * 解析路径段（处理 ../ 和 ./）
     */
    @NotNull
    private String resolvePathSegments(@NotNull String basePath, @NotNull String relativePath) {
        String[] baseSegments = basePath.split("/");
        String[] relativeSegments = relativePath.split("/");
        
        List<String> resolvedSegments = new ArrayList<>();
        
        // 添加基础路径段
        for (String segment : baseSegments) {
            if (!segment.isEmpty()) {
                resolvedSegments.add(segment);
            }
        }
        
        // 处理相对路径段
        for (String segment : relativeSegments) {
            if ("..".equals(segment)) {
                // 向上一级目录
                if (!resolvedSegments.isEmpty()) {
                    resolvedSegments.remove(resolvedSegments.size() - 1);
                }
            } else if (!".".equals(segment) && !segment.isEmpty()) {
                // 普通路径段
                resolvedSegments.add(segment);
            }
        }
        
        return "/" + String.join("/", resolvedSegments);
    }
    
    /**
     * 获取文件的相对路径
     */
    @Nullable
    private String getRelativePath(@NotNull VirtualFile file) {
        Project project = myElement.getProject();
        VirtualFile projectBase = project.getBaseDir();
        
        if (projectBase != null) {
            String projectPath = projectBase.getPath();
            String filePath = file.getPath();
            
            if (filePath.startsWith(projectPath)) {
                String relativePath = filePath.substring(projectPath.length());
                if (relativePath.startsWith("/")) {
                    relativePath = relativePath.substring(1);
                }
                
                // 移除文件扩展名
                if (relativePath.endsWith(".ms")) {
                    relativePath = relativePath.substring(0, relativePath.length() - 3);
                }
                
                return relativePath;
            }
        }
        
        return null;
    }
    
}