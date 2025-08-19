package com.magicapi.idea.structure;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.magicapi.idea.lang.psi.MSFunctionDeclaration;
import com.magicapi.idea.lang.psi.MSVarDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Magic Script 结构视图模型
 */
public class MagicScriptStructureViewModel extends TextEditorBasedStructureViewModel implements StructureViewModel.ElementInfoProvider {
    
    public MagicScriptStructureViewModel(@NotNull PsiFile psiFile, @Nullable Editor editor) {
        super(editor, psiFile);
    }
    
    @Override
    @NotNull
    public StructureViewTreeElement getRoot() {
        return new MagicScriptStructureViewElement(getPsiFile());
    }
    
    @Override
    @NotNull
    public Class[] getSuitableClasses() {
        return new Class[]{
            MSFunctionDeclaration.class,
            MSVarDeclaration.class
        };
    }
    
    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return false;
    }
    
    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        return element instanceof MagicScriptStructureViewElement &&
               ((MagicScriptStructureViewElement) element).getElement() instanceof MSVarDeclaration;
    }
}