package com.magicapi.idea.navigation;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.magicapi.idea.registry.ModuleRegistry;
import com.magicapi.idea.completion.model.MagicApiModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 内置模块引用实现
 * 处理内置模块（db, http等）的引用和文档
 */
public class BuiltinModuleReference extends PsiReferenceBase<PsiElement> {
    
    private final String moduleName;
    private final ModuleRegistry moduleRegistry;
    
    public BuiltinModuleReference(@NotNull PsiElement element) {
        super(element, new TextRange(0, element.getTextLength()));
        this.moduleName = element.getText();
        this.moduleRegistry = ModuleRegistry.getInstance();
    }
    
    @Override
    @Nullable
    public PsiElement resolve() {
        // 内置模块不需要跳转到具体定义，返回虚拟元素用于文档显示
        if (moduleRegistry.hasModule(moduleName)) {
            return createBuiltinModuleElement();
        }
        return null;
    }
    
    @Override
    @NotNull
    public Object[] getVariants() {
        // 提供所有可用的内置模块
        return moduleRegistry.getModuleNames().toArray(new String[0]);
    }
    
    /**
     * 创建内置模块的虚拟元素
     * 用于提供模块文档和信息
     */
    @NotNull
    private PsiElement createBuiltinModuleElement() {
        // 暂时返回原始元素，后续可以扩展为虚拟PSI元素
        return myElement;
    }
    
}