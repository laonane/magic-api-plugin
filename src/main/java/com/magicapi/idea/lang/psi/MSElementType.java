package com.magicapi.idea.lang.psi;

import com.intellij.psi.tree.IElementType;
import com.magicapi.idea.lang.MagicScriptLanguage;
import org.jetbrains.annotations.NotNull;

/**
 * Magic Script Element Type
 * PSI元素类型定义
 */
public class MSElementType extends IElementType {
    public MSElementType(@NotNull String debugName) {
        super(debugName, MagicScriptLanguage.INSTANCE);
    }
    
    @Override
    public String toString() {
        return "MSElementType." + super.toString();
    }
}