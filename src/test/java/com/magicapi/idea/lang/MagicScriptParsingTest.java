package com.magicapi.idea.lang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MagicScriptParsingTest {
    
    @Test
    public void testBasicFileTypeDefinition() {
        // 测试语言和文件类型定义
        MagicScriptLanguage language = MagicScriptLanguage.INSTANCE;
        assertNotNull(language);
        assertEquals("MagicScript", language.getID());
        
        MagicScriptFileType fileType = MagicScriptFileType.INSTANCE;
        assertNotNull(fileType);
        assertEquals("ms", fileType.getDefaultExtension());
        assertEquals("Magic Script", fileType.getName());
    }
    
    @Test
    public void testLanguageDefinition() {
        // 测试语言基本属性
        MagicScriptLanguage language = MagicScriptLanguage.INSTANCE;
        assertEquals("MagicScript", language.getID());
        assertEquals("Magic Script", language.getDisplayName());
    }
    
    @Test
    public void testBasicComponents() {
        // 测试基本组件是否能正确创建
        MagicScriptFileType fileType = MagicScriptFileType.INSTANCE;
        assertNotNull(fileType.getIcon());
        assertNotNull(fileType.getLanguage());
    }
}