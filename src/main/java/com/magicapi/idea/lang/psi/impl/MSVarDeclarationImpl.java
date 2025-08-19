package com.magicapi.idea.lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.magicapi.idea.lang.psi.MSVarDeclaration;
import com.magicapi.idea.lang.psi.MSTypes;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 变量声明PSI元素实现
 */
public class MSVarDeclarationImpl extends ASTWrapperPsiElement implements MSVarDeclaration {
    
    public MSVarDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }
    
    public void accept(@NotNull PsiElementVisitor visitor) {
        super.accept(visitor);
    }
    
    @Override
    @Nullable
    public String getName() {
        PsiElement nameElement = getNameIdentifier();
        return nameElement != null ? nameElement.getText() : null;
    }
    
    @Nullable
    public PsiElement getNameIdentifier() {
        // 查找VAR关键字后的第一个IDENTIFIER
        ASTNode varNode = getNode().findChildByType(MSTypes.VAR);
        if (varNode != null) {
            ASTNode identifierNode = varNode.getTreeNext();
            while (identifierNode != null && identifierNode.getElementType() != MSTypes.IDENTIFIER) {
                identifierNode = identifierNode.getTreeNext();
            }
            return identifierNode != null ? identifierNode.getPsi() : null;
        }
        return null;
    }
    
    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        // TODO: 实现重命名逻辑
        return this;
    }
    
    /**
     * 获取变量的初始化表达式
     */
    @Nullable
    public PsiElement getInitializer() {
        // 查找赋值符号后的表达式
        PsiElement assign = findChildByElementType(MSTypes.ASSIGN);
        if (assign != null) {
            return PsiTreeUtil.getNextSiblingOfType(assign, PsiElement.class);
        }
        return null;
    }
    
    /**
     * 检查变量是否有初始化值
     */
    public boolean hasInitializer() {
        return getInitializer() != null;
    }
    
    @Nullable
    private PsiElement findChildByElementType(IElementType elementType) {
        ASTNode childNode = getNode().findChildByType(elementType);
        return childNode != null ? childNode.getPsi() : null;
    }
}