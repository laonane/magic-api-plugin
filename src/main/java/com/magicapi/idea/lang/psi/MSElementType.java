package com.magicapi.idea.lang.psi;

import com.intellij.psi.tree.IElementType;
import com.magicapi.idea.lang.MagicScriptLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class MSElementType extends IElementType {
    public MSElementType(@NotNull @NonNls String debugName) {
        super(debugName, MagicScriptLanguage.INSTANCE);
    }
}