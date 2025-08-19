package com.magicapi.idea.debug;

import com.intellij.openapi.fileTypes.FileType;
import com.magicapi.idea.lang.MagicScriptFileType;
import org.jetbrains.annotations.NotNull;

/**
 * Magic Script 调试类型过滤器
 */
public class MagicScriptDebuggerTypesFilter {
    
    public static boolean isMagicScriptFile(@NotNull FileType fileType) {
        return fileType instanceof MagicScriptFileType;
    }
}