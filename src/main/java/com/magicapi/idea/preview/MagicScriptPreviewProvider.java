package com.magicapi.idea.preview;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Magic Script 实时预览提供器
 */
public class MagicScriptPreviewProvider {
    
    private final Project project;
    
    public MagicScriptPreviewProvider(@NotNull Project project) {
        this.project = project;
    }
    
    @Nullable
    public String generatePreview(@NotNull PsiFile psiFile) {
        StringBuilder preview = new StringBuilder();
        
        preview.append("<html><body style='font-family: monospace; font-size: 12px;'>");
        preview.append("<h3>Magic Script 预览</h3>");
        preview.append("<p><strong>文件:</strong> ").append(psiFile.getName()).append("</p>");
        preview.append("<p><strong>大小:</strong> ").append(psiFile.getTextLength()).append(" 字符</p>");
        
        // 添加语法结构预览
        preview.append("<h4>语法结构:</h4>");
        preview.append("<div style='background: #f5f5f5; padding: 10px; border-radius: 5px;'>");
        
        String content = psiFile.getText();
        if (content.contains("function")) {
            int functionCount = content.split("function").length - 1;
            preview.append("• 函数: ").append(functionCount).append("<br>");
        }
        
        if (content.contains("var ")) {
            int varCount = content.split("var ").length - 1;
            preview.append("• 变量: ").append(varCount).append("<br>");
        }
        
        if (content.contains("db.")) {
            preview.append("• 使用数据库模块<br>");
        }
        
        if (content.contains("http.")) {
            preview.append("• 使用HTTP模块<br>");
        }
        
        if (content.contains("request.")) {
            preview.append("• 使用请求模块<br>");
        }
        
        if (content.contains("response.")) {
            preview.append("• 使用响应模块<br>");
        }
        
        preview.append("</div>");
        
        // 添加代码预览
        preview.append("<h4>代码预览:</h4>");
        preview.append("<pre style='background: #f8f8f8; padding: 10px; border-radius: 5px; overflow-x: auto;'>");
        
        String[] lines = content.split("\n");
        int maxLines = Math.min(lines.length, 20);
        for (int i = 0; i < maxLines; i++) {
            preview.append(String.format("%2d: ", i + 1));
            preview.append(escapeHtml(lines[i]));
            preview.append("\n");
        }
        
        if (lines.length > 20) {
            preview.append("... (").append(lines.length - 20).append(" 更多行)");
        }
        
        preview.append("</pre>");
        preview.append("</body></html>");
        
        return preview.toString();
    }
    
    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    public void refreshPreview(@NotNull VirtualFile file) {
        FileEditor[] editors = FileEditorManager.getInstance(project).getEditors(file);
        for (FileEditor editor : editors) {
            if (editor instanceof MagicScriptPreviewFileEditor) {
                ((MagicScriptPreviewFileEditor) editor).refresh();
            }
        }
    }
}