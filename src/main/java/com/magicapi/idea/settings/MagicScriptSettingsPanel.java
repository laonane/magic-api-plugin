package com.magicapi.idea.settings;

import javax.swing.*;
import java.awt.*;

/**
 * Magic Script 设置面板
 */
public class MagicScriptSettingsPanel {
    
    private JPanel mainPanel;
    private JCheckBox enableCodeCompletionCheckBox;
    private JCheckBox enableSyntaxHighlightingCheckBox;
    private JCheckBox enableErrorCheckingCheckBox;
    private JTextField apiBaseUrlField;
    
    public MagicScriptSettingsPanel() {
        createUIComponents();
        initializeSettings();
    }
    
    private void createUIComponents() {
        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // 标题
        JLabel titleLabel = new JLabel("Magic Script Plugin Settings");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(titleLabel, gbc);
        
        // 代码补全选项
        enableCodeCompletionCheckBox = new JCheckBox("启用代码补全");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 0);
        mainPanel.add(enableCodeCompletionCheckBox, gbc);
        
        // 语法高亮选项
        enableSyntaxHighlightingCheckBox = new JCheckBox("启用语法高亮");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(enableSyntaxHighlightingCheckBox, gbc);
        
        // 错误检查选项
        enableErrorCheckingCheckBox = new JCheckBox("启用错误检查");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(enableErrorCheckingCheckBox, gbc);
        
        // API 基础URL设置
        JLabel apiUrlLabel = new JLabel("Magic API 基础URL:");
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 0, 5, 10);
        mainPanel.add(apiUrlLabel, gbc);
        
        apiBaseUrlField = new JTextField(30);
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 0, 5, 0);
        mainPanel.add(apiBaseUrlField, gbc);
        
        // 帮助文本
        JLabel helpLabel = new JLabel("<html><i>配置 Magic API 服务器地址以获得更好的代码补全体验</i></html>");
        helpLabel.setFont(helpLabel.getFont().deriveFont(12f));
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 0, 0);
        mainPanel.add(helpLabel, gbc);
    }
    
    private void initializeSettings() {
        MagicScriptSettings settings = MagicScriptSettings.getInstance();
        enableCodeCompletionCheckBox.setSelected(settings.isCodeCompletionEnabled());
        enableSyntaxHighlightingCheckBox.setSelected(settings.isSyntaxHighlightingEnabled());
        enableErrorCheckingCheckBox.setSelected(settings.isErrorCheckingEnabled());
        apiBaseUrlField.setText(settings.getApiBaseUrl());
    }
    
    public JPanel getPanel() {
        return mainPanel;
    }
    
    public boolean isModified() {
        MagicScriptSettings settings = MagicScriptSettings.getInstance();
        return enableCodeCompletionCheckBox.isSelected() != settings.isCodeCompletionEnabled() ||
               enableSyntaxHighlightingCheckBox.isSelected() != settings.isSyntaxHighlightingEnabled() ||
               enableErrorCheckingCheckBox.isSelected() != settings.isErrorCheckingEnabled() ||
               !apiBaseUrlField.getText().equals(settings.getApiBaseUrl());
    }
    
    public void apply() {
        MagicScriptSettings settings = MagicScriptSettings.getInstance();
        settings.setCodeCompletionEnabled(enableCodeCompletionCheckBox.isSelected());
        settings.setSyntaxHighlightingEnabled(enableSyntaxHighlightingCheckBox.isSelected());
        settings.setErrorCheckingEnabled(enableErrorCheckingCheckBox.isSelected());
        settings.setApiBaseUrl(apiBaseUrlField.getText().trim());
    }
    
    public void reset() {
        initializeSettings();
    }
}