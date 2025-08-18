package com.magicapi.idea.lang.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.magicapi.idea.lang.MagicScriptFileType;
import com.magicapi.idea.lang.MagicScriptLanguage;
import org.jetbrains.annotations.NotNull;

public class MSFile extends PsiFileBase {
    public MSFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, MagicScriptLanguage.INSTANCE);
    }
    
    @Override
    public @NotNull FileType getFileType() {
        return MagicScriptFileType.INSTANCE;
    }
    
    @Override
    public String toString() {
        return "Magic Script File";
    }
}