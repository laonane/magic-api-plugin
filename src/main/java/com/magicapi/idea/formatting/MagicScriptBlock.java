package com.magicapi.idea.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.TokenType;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.magicapi.idea.lang.psi.MSTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Magic Script格式化块
 */
public class MagicScriptBlock implements Block {
    private final ASTNode node;
    private final Alignment alignment;
    private final Indent indent;
    private final CodeStyleSettings settings;
    private final SpacingBuilder spacingBuilder;
    
    public MagicScriptBlock(@NotNull ASTNode node, 
                           @Nullable Alignment alignment, 
                           @Nullable Indent indent,
                           @NotNull CodeStyleSettings settings) {
        this.node = node;
        this.alignment = alignment;
        this.indent = indent != null ? indent : Indent.getNoneIndent();
        this.settings = settings;
        this.spacingBuilder = createSpacingBuilder(settings);
    }
    
    private SpacingBuilder createSpacingBuilder(CodeStyleSettings settings) {
        return new SpacingBuilder(settings, com.magicapi.idea.lang.MagicScriptLanguage.INSTANCE)
            .around(MSTypes.ASSIGN).spaces(1)
            .around(MSTypes.EQ).spaces(1)
            .around(MSTypes.NE).spaces(1)
            .around(MSTypes.LT).spaces(1)
            .around(MSTypes.GT).spaces(1)
            .around(MSTypes.LE).spaces(1)
            .around(MSTypes.GE).spaces(1)
            .around(MSTypes.PLUS).spaces(1)
            .around(MSTypes.MINUS).spaces(1)
            .around(MSTypes.MUL).spaces(1)
            .around(MSTypes.DIV).spaces(1)
            .around(MSTypes.AND).spaces(1)
            .around(MSTypes.OR).spaces(1)
            .after(MSTypes.COMMA).spaces(1)
            .before(MSTypes.COMMA).spaces(0)
            .after(MSTypes.LPAREN).spaces(0)
            .before(MSTypes.RPAREN).spaces(0)
            .after(MSTypes.LBRACE).spaces(1)
            .before(MSTypes.RBRACE).spaces(1)
            .after(MSTypes.IF).spaces(1)
            .after(MSTypes.ELSE).spaces(1)
            .after(MSTypes.FOR).spaces(1)
            .after(MSTypes.WHILE).spaces(1)
            .after(MSTypes.FUNCTION).spaces(1)
            .after(MSTypes.VAR).spaces(1);
    }
    
    @NotNull
    public ASTNode getNode() {
        return node;
    }
    
    @Override
    @NotNull
    public com.intellij.openapi.util.TextRange getTextRange() {
        return node.getTextRange();
    }
    
    @Override
    @NotNull
    public List<Block> getSubBlocks() {
        List<Block> blocks = new ArrayList<>();
        ASTNode child = node.getFirstChildNode();
        
        while (child != null) {
            if (child.getElementType() != TokenType.WHITE_SPACE) {
                blocks.add(new MagicScriptBlock(child, null, calculateIndent(child), settings));
            }
            child = child.getTreeNext();
        }
        
        return blocks;
    }
    
    @Nullable
    @Override
    public Wrap getWrap() {
        return null;
    }
    
    @Nullable
    @Override
    public Indent getIndent() {
        return indent;
    }
    
    @Nullable
    @Override
    public Alignment getAlignment() {
        return alignment;
    }
    
    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return spacingBuilder.getSpacing(this, child1, child2);
    }
    
    @Override
    @NotNull
    public ChildAttributes getChildAttributes(int newChildIndex) {
        IElementType elementType = node.getElementType();
        
        if (elementType == MSTypes.BLOCK_STATEMENT) {
            return new ChildAttributes(Indent.getNormalIndent(), null);
        }
        
        return new ChildAttributes(Indent.getNoneIndent(), null);
    }
    
    @Override
    public boolean isIncomplete() {
        return false;
    }
    
    @Override
    public boolean isLeaf() {
        return node.getFirstChildNode() == null;
    }
    
    @Nullable
    private Indent calculateIndent(@NotNull ASTNode child) {
        IElementType elementType = child.getElementType();
        IElementType parentType = node.getElementType();
        
        if (parentType == MSTypes.BLOCK_STATEMENT) {
            return Indent.getNormalIndent();
        }
        
        if (elementType == MSTypes.LBRACE || elementType == MSTypes.RBRACE) {
            return Indent.getNoneIndent();
        }
        
        return Indent.getNoneIndent();
    }
}