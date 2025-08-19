package com.magicapi.idea.navigation;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.magicapi.idea.lang.psi.MSFunctionDeclaration;
import com.magicapi.idea.lang.psi.MSFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 函数引用实现
 * 处理函数调用到函数定义的跳转
 */
public class FunctionReference extends PsiReferenceBase<PsiElement> {
    
    private final String functionName;
    
    public FunctionReference(@NotNull PsiElement element) {
        super(element, new TextRange(0, element.getTextLength()));
        this.functionName = element.getText();
    }
    
    @Override
    @Nullable
    public PsiElement resolve() {
        // 查找函数定义
        return findFunctionDeclaration(myElement, functionName);
    }
    
    @Override
    @NotNull
    public Object[] getVariants() {
        // 提供所有可用的函数名
        List<String> functionNames = new ArrayList<>();
        
        // 查找当前文件中的所有函数声明
        MSFile msFile = PsiTreeUtil.getParentOfType(myElement, MSFile.class);
        if (msFile != null) {
            List<MSFunctionDeclaration> functions = PsiTreeUtil.getChildrenOfTypeAsList(msFile, MSFunctionDeclaration.class);
            for (MSFunctionDeclaration function : functions) {
                String name = function.getName();
                if (name != null && !name.isEmpty()) {
                    functionNames.add(name);
                }
            }
        }
        
        return functionNames.toArray();
    }
    
    /**
     * 查找函数声明
     */
    @Nullable
    private PsiElement findFunctionDeclaration(@NotNull PsiElement context, @NotNull String functionName) {
        // 1. 在当前文件中查找
        MSFile currentFile = PsiTreeUtil.getParentOfType(context, MSFile.class);
        if (currentFile != null) {
            MSFunctionDeclaration function = findFunctionInFile(currentFile, functionName);
            if (function != null) {
                return function;
            }
        }
        
        // 2. 在项目的其他.ms文件中查找
        // 这里可以扩展为在整个项目中搜索
        
        // 3. 检查是否为内置函数或全局函数
        if (isBuiltinFunction(functionName)) {
            // 返回虚拟的内置函数元素
            return createBuiltinFunctionElement(functionName);
        }
        
        return null;
    }
    
    /**
     * 在指定文件中查找函数
     */
    @Nullable
    private MSFunctionDeclaration findFunctionInFile(@NotNull MSFile file, @NotNull String functionName) {
        List<MSFunctionDeclaration> functions = PsiTreeUtil.getChildrenOfTypeAsList(file, MSFunctionDeclaration.class);
        
        for (MSFunctionDeclaration function : functions) {
            if (functionName.equals(function.getName())) {
                return function;
            }
        }
        
        return null;
    }
    
    /**
     * 检查是否为内置函数
     */
    private boolean isBuiltinFunction(@NotNull String functionName) {
        // 聚合函数
        String[] aggregateFunctions = {"count", "sum", "max", "min", "avg", "group_concat"};
        for (String func : aggregateFunctions) {
            if (func.equals(functionName)) {
                return true;
            }
        }
        
        // 数学函数
        String[] mathFunctions = {"round", "floor", "ceil", "abs", "sqrt", "pow", "random"};
        for (String func : mathFunctions) {
            if (func.equals(functionName)) {
                return true;
            }
        }
        
        // 字符串函数
        String[] stringFunctions = {"uuid", "concat", "format", "md5", "sha1", "base64_encode", "base64_decode"};
        for (String func : stringFunctions) {
            if (func.equals(functionName)) {
                return true;
            }
        }
        
        // 日期函数
        String[] dateFunctions = {"now", "current_timestamp", "current_date", "date_format", "parse_date"};
        for (String func : dateFunctions) {
            if (func.equals(functionName)) {
                return true;
            }
        }
        
        // 工具函数
        String[] utilityFunctions = {"is_null", "not_null", "nvl", "coalesce", "typeof", "instanceof"};
        for (String func : utilityFunctions) {
            if (func.equals(functionName)) {
                return true;
            }
        }
        
        // 调试函数
        String[] debugFunctions = {"print", "println", "debug", "info", "warn", "error"};
        for (String func : debugFunctions) {
            if (func.equals(functionName)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 创建内置函数的虚拟元素
     * 用于提供文档和跳转目标
     */
    @Nullable
    private PsiElement createBuiltinFunctionElement(@NotNull String functionName) {
        // 这里可以创建一个虚拟的PSI元素来表示内置函数
        // 现在简化处理，返回null
        return null;
    }
}