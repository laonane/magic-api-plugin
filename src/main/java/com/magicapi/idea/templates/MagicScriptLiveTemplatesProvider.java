package com.magicapi.idea.templates;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Magic Script 代码模板提供器
 */
public class MagicScriptLiveTemplatesProvider implements DefaultLiveTemplatesProvider {
    
    @Override
    @NotNull
    public String[] getDefaultLiveTemplateFiles() {
        return new String[]{"liveTemplates/MagicScript"};
    }
    
    @Override
    @Nullable
    public String[] getHiddenLiveTemplateFiles() {
        return null;
    }
}