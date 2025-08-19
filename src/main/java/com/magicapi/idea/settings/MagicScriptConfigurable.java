package com.magicapi.idea.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Magic Script 设置配置页面
 */
public class MagicScriptConfigurable implements Configurable {
    
    private MagicScriptSettingsPanel settingsPanel;
    
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Magic Script";
    }
    
    @Override
    public @Nullable JComponent createComponent() {
        if (settingsPanel == null) {
            settingsPanel = new MagicScriptSettingsPanel();
        }
        return settingsPanel.getPanel();
    }
    
    @Override
    public boolean isModified() {
        if (settingsPanel == null) {
            return false;
        }
        return settingsPanel.isModified();
    }
    
    @Override
    public void apply() throws ConfigurationException {
        if (settingsPanel != null) {
            settingsPanel.apply();
        }
    }
    
    @Override
    public void reset() {
        if (settingsPanel != null) {
            settingsPanel.reset();
        }
    }
    
    @Override
    public void disposeUIResources() {
        settingsPanel = null;
    }
}