package com.magicapi.idea.preview;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;

/**
 * Magic Script 预览文件编辑器
 */
public class MagicScriptPreviewFileEditor extends UserDataHolderBase implements FileEditor {
    
    private final Project project;
    private final VirtualFile file;
    private final MagicScriptPreviewProvider previewProvider;
    private JEditorPane previewPane;
    private JScrollPane scrollPane;
    
    public MagicScriptPreviewFileEditor(@NotNull Project project, @NotNull VirtualFile file) {
        this.project = project;
        this.file = file;
        this.previewProvider = new MagicScriptPreviewProvider(project);
        initializeComponents();
        refresh();
    }
    
    private void initializeComponents() {
        previewPane = new JEditorPane("text/html", "");
        previewPane.setEditable(false);
        previewPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        
        scrollPane = new JScrollPane(previewPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }
    
    public void refresh() {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        if (psiFile != null) {
            String preview = previewProvider.generatePreview(psiFile);
            previewPane.setText(preview);
            previewPane.setCaretPosition(0);
        }
    }
    
    @Override
    @NotNull
    public JComponent getComponent() {
        return scrollPane;
    }
    
    @Override
    @Nullable
    public JComponent getPreferredFocusedComponent() {
        return previewPane;
    }
    
    @Override
    @NotNull
    public String getName() {
        return "Preview";
    }
    
    @Override
    public void setState(@NotNull FileEditorState state) {
        // 简化实现
    }
    
    @Override
    public boolean isModified() {
        return false;
    }
    
    @Override
    public boolean isValid() {
        return true;
    }
    
    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
        // 简化实现
    }
    
    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
        // 简化实现
    }
    
    @Override
    @Nullable
    public FileEditorLocation getCurrentLocation() {
        return null;
    }
    
    @Override
    public void dispose() {
        // 清理资源
    }
}