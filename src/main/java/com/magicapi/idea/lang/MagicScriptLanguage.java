package com.magicapi.idea.lang;

import com.intellij.lang.Language;

public class MagicScriptLanguage extends Language {
    public static final MagicScriptLanguage INSTANCE = new MagicScriptLanguage();
    
    private MagicScriptLanguage() {
        super("MagicScript");
    }
    
    @Override
    public String getDisplayName() {
        return "Magic Script";
    }
    
    @Override
    public boolean isCaseSensitive() {
        return true;
    }
}