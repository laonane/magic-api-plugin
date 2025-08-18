package com.magicapi.idea.lang.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Magic Script 引用实现
 * 处理标识符的引用解析，包括变量、函数、导入符号等
 */
public class MagicScriptReference extends PsiReferenceBase<PsiElement> {
    
    private final String referenceName;
    
    public MagicScriptReference(@NotNull PsiElement element, TextRange range, String referenceName) {
        super(element, range);
        this.referenceName = referenceName;
    }
    
    public MagicScriptReference(@NotNull PsiElement element, String referenceName) {
        super(element);
        this.referenceName = referenceName;
    }
    
    @Override
    @Nullable
    public PsiElement resolve() {
        // 1. 首先在当前文件的作用域中查找
        PsiElement localDefinition = findLocalDefinition();
        if (localDefinition != null) {
            return localDefinition;
        }
        
        // 2. 查找导入的符号
        PsiElement importedDefinition = findImportedDefinition();
        if (importedDefinition != null) {
            return importedDefinition;
        }
        
        // 3. 查找内置符号
        PsiElement builtinDefinition = findBuiltinDefinition();
        if (builtinDefinition != null) {
            return builtinDefinition;
        }
        
        return null;
    }
    
    /**
     * 在当前文件中查找局部定义
     */
    @Nullable
    private PsiElement findLocalDefinition() {
        PsiFile currentFile = getElement().getContainingFile();
        if (!(currentFile instanceof MSFile)) {
            return null;
        }
        
        // 查找变量声明
        PsiElement varDeclaration = findVariableDeclaration(currentFile, referenceName);
        if (varDeclaration != null) {
            return varDeclaration;
        }
        
        // 查找函数声明
        PsiElement functionDeclaration = findFunctionDeclaration(currentFile, referenceName);
        if (functionDeclaration != null) {
            return functionDeclaration;
        }
        
        return null;
    }
    
    /**
     * 查找导入的定义
     */
    @Nullable
    private PsiElement findImportedDefinition() {
        PsiFile currentFile = getElement().getContainingFile();
        if (!(currentFile instanceof MSFile)) {
            return null;
        }
        
        // 获取当前文件的所有import语句
        PsiElement[] imports = PsiTreeUtil.collectElements(currentFile, 
            element -> element instanceof MSImportStatement);
            
        for (PsiElement importElement : imports) {
            if (importElement instanceof MSImportStatement) {
                MSImportStatement importStatement = (MSImportStatement) importElement;
                PsiElement importedFile = resolveImportPath(importStatement);
                
                if (importedFile instanceof MSFile) {
                    // 在导入的文件中查找导出的符号
                    PsiElement exportedSymbol = findExportedSymbol((MSFile) importedFile, referenceName);
                    if (exportedSymbol != null) {
                        return exportedSymbol;
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * 查找内置定义（如内置模块）
     */
    @Nullable
    private PsiElement findBuiltinDefinition() {
        // 检查是否为内置模块名
        if (isBuiltinModule(referenceName)) {
            // 返回一个虚拟的内置模块元素
            // 这里可以创建一个特殊的PSI元素来表示内置模块
            return createBuiltinModuleElement();
        }
        
        return null;
    }
    
    /**
     * 在文件中查找变量声明
     */
    @Nullable
    private PsiElement findVariableDeclaration(@NotNull PsiFile file, @NotNull String name) {
        return PsiTreeUtil.findChildrenOfType(file, MSVarDeclaration.class)
            .stream()
            .filter(var -> name.equals(var.getName()))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * 在文件中查找函数声明
     */
    @Nullable
    private PsiElement findFunctionDeclaration(@NotNull PsiFile file, @NotNull String name) {
        return PsiTreeUtil.findChildrenOfType(file, MSFunctionDeclaration.class)
            .stream()
            .filter(func -> name.equals(func.getName()))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * 解析import路径
     */
    @Nullable
    private PsiElement resolveImportPath(@NotNull MSImportStatement importStatement) {
        String importPath = importStatement.getImportPath();
        if (importPath == null) {
            return null;
        }
        
        // 移除引号
        if (importPath.startsWith("\"") && importPath.endsWith("\"")) {
            importPath = importPath.substring(1, importPath.length() - 1);
        } else if (importPath.startsWith("'") && importPath.endsWith("'")) {
            importPath = importPath.substring(1, importPath.length() - 1);
        }
        
        // 解析相对路径
        PsiFile currentFile = getElement().getContainingFile();
        PsiDirectory currentDirectory = currentFile.getContainingDirectory();
        
        if (currentDirectory != null) {
            return resolveRelativePath(currentDirectory, importPath);
        }
        
        return null;
    }
    
    /**
     * 解析相对路径
     */
    @Nullable
    private PsiElement resolveRelativePath(@NotNull PsiDirectory baseDirectory, @NotNull String path) {
        String[] pathParts = path.split("/");
        PsiDirectory currentDir = baseDirectory;
        
        for (int i = 0; i < pathParts.length - 1; i++) {
            String part = pathParts[i];
            if ("..".equals(part)) {
                currentDir = currentDir.getParentDirectory();
            } else if (!".".equals(part) && !part.isEmpty()) {
                currentDir = currentDir.findSubdirectory(part);
            }
            
            if (currentDir == null) {
                return null;
            }
        }
        
        // 查找文件
        String fileName = pathParts[pathParts.length - 1];
        if (!fileName.endsWith(".ms")) {
            fileName += ".ms";
        }
        
        return currentDir.findFile(fileName);
    }
    
    /**
     * 在文件中查找导出的符号
     */
    @Nullable
    private PsiElement findExportedSymbol(@NotNull MSFile file, @NotNull String symbolName) {
        // 查找export语句
        return PsiTreeUtil.findChildrenOfType(file, MSExportStatement.class)
            .stream()
            .map(export -> findSymbolInExport(export, symbolName))
            .filter(symbol -> symbol != null)
            .findFirst()
            .orElse(null);
    }
    
    /**
     * 在export语句中查找符号
     */
    @Nullable
    private PsiElement findSymbolInExport(@NotNull MSExportStatement export, @NotNull String symbolName) {
        // TODO: 实现export语句的符号查找
        // 需要根据具体的export语法来实现
        return null;
    }
    
    /**
     * 检查是否为内置模块
     */
    private boolean isBuiltinModule(@NotNull String name) {
        return "db".equals(name) || "http".equals(name) || "request".equals(name) || 
               "response".equals(name) || "env".equals(name) || "log".equals(name);
    }
    
    /**
     * 创建内置模块元素
     */
    @Nullable
    private PsiElement createBuiltinModuleElement() {
        // 这里可以创建一个特殊的PSI元素来表示内置模块
        // 暂时返回null，实际实现可能需要创建虚拟元素
        return null;
    }
    
    @Override
    @NotNull
    public Object[] getVariants() {
        // 返回代码补全的候选项
        // 这个方法会被代码补全系统调用
        return new Object[0];
    }
    
    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        // 处理重命名操作
        // TODO: 实现重命名逻辑
        return getElement();
    }
}