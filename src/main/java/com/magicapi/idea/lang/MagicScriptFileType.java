package com.magicapi.idea.lang;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import com.magicapi.idea.icons.MagicScriptIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MagicScriptFileType extends LanguageFileType {
    public static final MagicScriptFileType INSTANCE = new MagicScriptFileType();
    
    private MagicScriptFileType() {
        super(MagicScriptLanguage.INSTANCE);
    }
    
    @Override
    public @NonNls @NotNull String getName() {
        return "MagicScript";
    }
    
    @Override
    public @NlsContexts.Label @NotNull String getDescription() {
        return "Magic Script file";
    }
    
    @Override
    public @NlsSafe @NotNull String getDefaultExtension() {
        return "ms";
    }
    
    @Override
    public @Nullable Icon getIcon() {
        return MagicScriptIcons.FILE;
    }
}