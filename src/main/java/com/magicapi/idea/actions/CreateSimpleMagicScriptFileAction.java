package com.magicapi.idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.magicapi.idea.icons.MagicScriptIcons;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * 简单的Magic Script文件创建操作
 */
public class CreateSimpleMagicScriptFileAction extends AnAction {
    
    public CreateSimpleMagicScriptFileAction() {
        super("Magic Script File", "Create new Magic Script file", MagicScriptIcons.FILE);
    }
    
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
            "Enter file name (without extension):",
            "New Magic Script File",
            MagicScriptIcons.FILE
        );
        
        if (fileName != null && !fileName.trim().isEmpty()) {
            fileName = fileName.trim();
            if (!fileName.endsWith(".ms")) {
                fileName += ".ms";
            }
            
            try {
                VirtualFile file = directory.createChildData(this, fileName);
                String template = createSimpleTemplate();
                VfsUtil.saveText(file, template);
                
                // 打开文件
                OpenFileDescriptor descriptor = new OpenFileDescriptor(project, file);
                descriptor.navigate(true);
            } catch (IOException ex) {
                Messages.showErrorDialog(project, "Failed to create file: " + ex.getMessage(), "Error");
            }
        }
    }
    
    private String createSimpleTemplate() {
        return """
            // Magic Script API接口
            
            // 获取请求参数
            var id = request.getParameter("id");
            
            // 查询数据库
            var result = db.select("SELECT * FROM users WHERE id = ?", id);
            
            // 返回JSON响应
            response.json({
                success: true,
                data: result
            });
            """;
    }
}