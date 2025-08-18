package com.magicapi.idea.lang.lexer;

import com.intellij.lexer.FlexAdapter;

public class MagicScriptLexerAdapter extends FlexAdapter {
    public MagicScriptLexerAdapter() {
        super(new MagicScriptLexer(null));
    }
}