package com.magicapi.idea.icons;

import com.intellij.icons.AllIcons;

import javax.swing.*;

/**
 * Magic Script 插件图标定义
 * 使用 IntelliJ 内置图标，避免图标文件缺失问题
 */
public interface MagicScriptIcons {
    Icon FILE = AllIcons.FileTypes.JavaScript;
    Icon FUNCTION = AllIcons.Nodes.Function;
    Icon METHOD = AllIcons.Nodes.Method;
    Icon VARIABLE = AllIcons.Nodes.Variable;
    Icon MODULE = AllIcons.Nodes.Module;
    Icon KEYWORD = AllIcons.Nodes.Static;
    Icon PARAMETER = AllIcons.Nodes.Parameter;
    Icon DATABASE = AllIcons.Providers.Sqlite;
    Icon HTTP = AllIcons.FileTypes.Json;
    Icon BUILTIN = AllIcons.Nodes.Static;
    Icon EXTENSION = AllIcons.Nodes.Method;
}