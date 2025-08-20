package com.magicapi.idea.actions;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.magicapi.idea.icons.MagicScriptIcons;
import org.jetbrains.annotations.NotNull;

/**
 * 创建Magic Script文件的操作
 */
public class CreateMagicScriptFileAction extends CreateFileFromTemplateAction {
    
    public CreateMagicScriptFileAction() {
        super("Magic Script File", "Create new Magic Script file", MagicScriptIcons.FILE);
    }
    
    @Override
    protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory directory, CreateFileFromTemplateDialog.@NotNull Builder builder) {
        builder
            .setTitle("New Magic Script File")
            .addKind("Magic Script File", MagicScriptIcons.FILE, "MagicScript");
    }
    
    @Override
    protected String getActionName(PsiDirectory directory, @NotNull String newName, String templateName) {
        return "Create Magic Script File: " + newName;
    }
}