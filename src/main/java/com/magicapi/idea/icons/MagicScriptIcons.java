package com.magicapi.idea.icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public interface MagicScriptIcons {
    Icon FILE = IconLoader.getIcon("/icons/magicscript-file.png", MagicScriptIcons.class);
    Icon FUNCTION = IconLoader.getIcon("/icons/function.png", MagicScriptIcons.class);
    Icon METHOD = IconLoader.getIcon("/icons/function.png", MagicScriptIcons.class); // 复用function图标
    Icon VARIABLE = IconLoader.getIcon("/icons/variable.png", MagicScriptIcons.class);
    Icon MODULE = IconLoader.getIcon("/icons/module.png", MagicScriptIcons.class);
    Icon KEYWORD = IconLoader.getIcon("/icons/keyword.png", MagicScriptIcons.class);
}