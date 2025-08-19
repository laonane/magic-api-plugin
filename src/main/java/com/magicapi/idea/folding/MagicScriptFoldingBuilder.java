package com.magicapi.idea.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.magicapi.idea.lang.psi.MSTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Magic Script 代码折叠构建器
 */
public class MagicScriptFoldingBuilder extends FoldingBuilderEx {
    
    @Override
    @NotNull
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        
        buildFoldRegions(root.getNode(), descriptors);
        
        return descriptors.toArray(new FoldingDescriptor[0]);
    }
    
    private void buildFoldRegions(@NotNull ASTNode node, @NotNull List<FoldingDescriptor> descriptors) {
        if (isFoldable(node)) {
            TextRange range = getFoldingRange(node);
            if (range != null && range.getLength() > 2) {
                descriptors.add(new FoldingDescriptor(node, range));
            }
        }
        
        // 递归处理子节点
        for (ASTNode child = node.getFirstChildNode(); child != null; child = child.getTreeNext()) {
            buildFoldRegions(child, descriptors);
        }
    }
    
    private boolean isFoldable(@NotNull ASTNode node) {
        return node.getElementType() == MSTypes.BLOCK_STATEMENT ||
               node.getElementType() == MSTypes.FUNCTION_BODY ||
               node.getElementType() == MSTypes.IF_STATEMENT ||
               node.getElementType() == MSTypes.FOR_STATEMENT ||
               node.getElementType() == MSTypes.WHILE_STATEMENT;
    }
    
    @Nullable
    private TextRange getFoldingRange(@NotNull ASTNode node) {
        // 查找左大括号和右大括号
        ASTNode lbrace = findChildByType(node, MSTypes.LBRACE);
        ASTNode rbrace = findChildByType(node, MSTypes.RBRACE);
        
        if (lbrace != null && rbrace != null) {
            return new TextRange(lbrace.getStartOffset(), rbrace.getStartOffset() + rbrace.getTextLength());
        }
        
        return null;
    }
    
    @Nullable
    private ASTNode findChildByType(@NotNull ASTNode parent, @NotNull com.intellij.psi.tree.IElementType type) {
        for (ASTNode child = parent.getFirstChildNode(); child != null; child = child.getTreeNext()) {
            if (child.getElementType() == type) {
                return child;
            }
        }
        return null;
    }
    
    @Override
    @Nullable
    public String getPlaceholderText(@NotNull ASTNode node) {
        if (node.getElementType() == MSTypes.FUNCTION_BODY) {
            return "{...}";
        } else if (node.getElementType() == MSTypes.BLOCK_STATEMENT) {
            return "{...}";
        } else if (node.getElementType() == MSTypes.IF_STATEMENT) {
            return "{...}";
        } else if (node.getElementType() == MSTypes.FOR_STATEMENT) {
            return "{...}";
        } else if (node.getElementType() == MSTypes.WHILE_STATEMENT) {
            return "{...}";
        }
        return "{...}";
    }
    
    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false; // 默认不折叠
    }
}