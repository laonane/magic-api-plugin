package com.magicapi.idea.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.magicapi.idea.icons.MagicScriptIcons;
import com.magicapi.idea.lang.psi.MSFunctionDeclaration;
import com.magicapi.idea.lang.psi.MSVarDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Magic Script 结构视图元素
 */
public class MagicScriptStructureViewElement implements StructureViewTreeElement, SortableTreeElement {
    
    private final NavigatablePsiElement element;
    
    public MagicScriptStructureViewElement(@NotNull NavigatablePsiElement element) {
        this.element = element;
    }
    
    @Override
    @NotNull
    public Object getValue() {
        return element;
    }
    
    public NavigatablePsiElement getElement() {
        return element;
    }
    
    @Override
    @NotNull
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            @Nullable
            public String getPresentableText() {
                if (element instanceof MSFunctionDeclaration) {
                    return ((MSFunctionDeclaration) element).getName() + "()";
                } else if (element instanceof MSVarDeclaration) {
                    return ((MSVarDeclaration) element).getName();
                }
                return element.getName();
            }
            
            @Override
            @Nullable
            public String getLocationString() {
                return null;
            }
            
            @Override
            @Nullable
            public Icon getIcon(boolean unused) {
                if (element instanceof MSFunctionDeclaration) {
                    return MagicScriptIcons.FUNCTION;
                } else if (element instanceof MSVarDeclaration) {
                    return MagicScriptIcons.VARIABLE;
                }
                return MagicScriptIcons.FILE;
            }
        };
    }
    
    @Override
    @NotNull
    public TreeElement[] getChildren() {
        List<TreeElement> children = new ArrayList<>();
        
        if (element instanceof PsiElement) {
            Collection<MSFunctionDeclaration> functions = 
                PsiTreeUtil.findChildrenOfType(element, MSFunctionDeclaration.class);
            for (MSFunctionDeclaration function : functions) {
                if (function instanceof NavigatablePsiElement) {
                    children.add(new MagicScriptStructureViewElement((NavigatablePsiElement) function));
                }
            }
            
            Collection<MSVarDeclaration> variables = 
                PsiTreeUtil.findChildrenOfType(element, MSVarDeclaration.class);
            for (MSVarDeclaration variable : variables) {
                if (variable instanceof NavigatablePsiElement) {
                    children.add(new MagicScriptStructureViewElement((NavigatablePsiElement) variable));
                }
            }
        }
        
        return children.toArray(new TreeElement[0]);
    }
    
    @Override
    public void navigate(boolean requestFocus) {
        if (element != null) {
            element.navigate(requestFocus);
        }
    }
    
    @Override
    public boolean canNavigate() {
        return element != null && element.canNavigate();
    }
    
    @Override
    public boolean canNavigateToSource() {
        return element != null && element.canNavigateToSource();
    }
    
    @Override
    @NotNull
    public String getAlphaSortKey() {
        String name = element instanceof MSFunctionDeclaration ? 
            ((MSFunctionDeclaration) element).getName() :
            element instanceof MSVarDeclaration ? 
                ((MSVarDeclaration) element).getName() : 
                element.getName();
        return name != null ? name : "";
    }
}