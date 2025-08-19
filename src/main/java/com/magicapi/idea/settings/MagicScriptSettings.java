package com.magicapi.idea.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Magic Script 插件设置
 */
@State(
    name = "MagicScriptSettings",
    storages = @Storage("magic-script-plugin.xml")
)
public class MagicScriptSettings implements PersistentStateComponent<MagicScriptSettings> {
    
    private boolean codeCompletionEnabled = true;
    private boolean syntaxHighlightingEnabled = true;
    private boolean errorCheckingEnabled = true;
    private String apiBaseUrl = "http://localhost:9999";
    
    public static MagicScriptSettings getInstance() {
        return ApplicationManager.getApplication().getService(MagicScriptSettings.class);
    }
    
    @Override
    public @Nullable MagicScriptSettings getState() {
        return this;
    }
    
    @Override
    public void loadState(@NotNull MagicScriptSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
    
    // Getters and Setters
    public boolean isCodeCompletionEnabled() {
        return codeCompletionEnabled;
    }
    
    public void setCodeCompletionEnabled(boolean codeCompletionEnabled) {
        this.codeCompletionEnabled = codeCompletionEnabled;
    }
    
    public boolean isSyntaxHighlightingEnabled() {
        return syntaxHighlightingEnabled;
    }
    
    public void setSyntaxHighlightingEnabled(boolean syntaxHighlightingEnabled) {
        this.syntaxHighlightingEnabled = syntaxHighlightingEnabled;
    }
    
    public boolean isErrorCheckingEnabled() {
        return errorCheckingEnabled;
    }
    
    public void setErrorCheckingEnabled(boolean errorCheckingEnabled) {
        this.errorCheckingEnabled = errorCheckingEnabled;
    }
    
    public String getApiBaseUrl() {
        return apiBaseUrl;
    }
    
    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }
}