package com.magicapi.idea.lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.magicapi.idea.lang.psi.MSFunctionDeclaration;
import com.magicapi.idea.lang.psi.MSTypes;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 函数声明PSI元素实现
 */
public class MSFunctionDeclarationImpl extends ASTWrapperPsiElement implements MSFunctionDeclaration {
    
    public MSFunctionDeclarationImpl(@NotNull ASTNode node) {
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
        // 查找FUNCTION关键字后的第一个IDENTIFIER
        ASTNode functionNode = getNode().findChildByType(MSTypes.FUNCTION);
        if (functionNode != null) {
            ASTNode identifierNode = functionNode.getTreeNext();
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
     * 获取函数参数列表
     */
    @NotNull
    public List<PsiElement> getParameters() {
        // 查找括号内的参数
        PsiElement lparen = findChildByType(MSTypes.LPAREN);
        PsiElement rparen = findChildByType(MSTypes.RPAREN);
        
        if (lparen != null && rparen != null) {
            return PsiTreeUtil.getElementsOfRange(lparen, rparen)
                    .stream()
                    .filter(element -> element.getNode().getElementType() == MSTypes.IDENTIFIER)
                    .toList();
        }
        
        return List.of();
    }
    
    /**
     * 获取函数体
     */
    @Nullable
    public PsiElement getBody() {
        // 查找函数体代码块
        return findChildByElementType(MSTypes.LBRACE);
    }
    
    /**
     * 获取参数数量
     */
    public int getParameterCount() {
        return getParameters().size();
    }
    
    /**
     * 检查函数是否有参数
     */
    public boolean hasParameters() {
        return !getParameters().isEmpty();
    }
    
    @Nullable
    private PsiElement findChildByElementType(IElementType elementType) {
        ASTNode childNode = getNode().findChildByType(elementType);
        return childNode != null ? childNode.getPsi() : null;
    }
}