package com.magicapi.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class CreateMagicScriptFileAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;
        
        VirtualFile directory = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (directory == null || !directory.isDirectory()) {
            directory = project.getBaseDir();
        }
        
        String fileName = Messages.showInputDialog(
            project,
            "Enter file name:",
            "New Magic Script File",
            AllIcons.FileTypes.Text
        );
        
        if (fileName != null && !fileName.isEmpty()) {
            if (!fileName.endsWith(".ms")) {
                fileName += ".ms";
            }
            
            try {
                VirtualFile file = directory.createChildData(this, fileName);
                // 创建文件模板内容
                String template = generateFileTemplate();
                VfsUtil.saveText(file, template);
                
                // 打开文件
                OpenFileDescriptor descriptor = new OpenFileDescriptor(project, file);
                descriptor.navigate(true);
            } catch (IOException ex) {
                Messages.showErrorDialog(project, "Failed to create file: " + ex.getMessage(), "Error");
            }
        }
    }
    
    private String generateFileTemplate() {
        return """
            // Magic Script file
            //
            // Example usage:
            var result = db.select("SELECT * FROM users LIMIT 10");
            response.json(result);
            """;
    }
}