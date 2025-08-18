package com.magicapi.idea.lang.psi;

import com.intellij.psi.tree.IElementType;
import com.magicapi.idea.lang.MagicScriptLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class MSTokenType extends IElementType {
    public MSTokenType(@NotNull @NonNls String debugName) {
        super(debugName, MagicScriptLanguage.INSTANCE);
    }
    
    @Override
    public String toString() {
        return "MSTokenType." + super.toString();
    }
}