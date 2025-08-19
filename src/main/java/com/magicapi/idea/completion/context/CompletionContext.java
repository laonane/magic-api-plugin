package com.magicapi.idea.completion.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 代码补全上下文信息
 * 包含当前补全位置的语法上下文和类型信息
 */
public class CompletionContext {
    
    /**
     * 补全类型枚举
     */
    public enum Type {
        EXPRESSION,           // 表达式位置
        MEMBER_ACCESS,        // 成员访问 (如 obj.method)
        FUNCTION_CALL,        // 函数调用 (如 function())
        FUNCTION_PARAMETER,   // 函数参数位置
        VARIABLE_DECLARATION, // 变量声明
        IMPORT_STATEMENT,     // import语句
        STRING_LITERAL        // 字符串字面量
    }
    
    private final Type type;
    private final String qualifier;  // 限定符（如成员访问中的对象名）
    private final String qualifierType; // 限定符类型
    private final String functionName;  // 函数名称
    private final int parameterIndex;   // 参数索引
    private final String chainReturnType; // 链式调用返回类型
    
    public CompletionContext(@NotNull Type type, @Nullable String qualifier, @Nullable String qualifierType) {
        this(type, qualifier, qualifierType, null, 0, null);
    }
    
    public CompletionContext(@NotNull Type type, @Nullable String qualifier, @Nullable String qualifierType,
                           @Nullable String functionName, int parameterIndex, @Nullable String chainReturnType) {
        this.type = type;
        this.qualifier = qualifier;
        this.qualifierType = qualifierType;
        this.functionName = functionName;
        this.parameterIndex = parameterIndex;
        this.chainReturnType = chainReturnType;
    }
    
    @NotNull
    public Type getType() {
        return type;
    }
    
    @Nullable
    public String getQualifier() {
        return qualifier;
    }
    
    @Nullable
    public String getQualifierType() {
        return qualifierType;
    }
    
    /**
     * 是否为成员访问上下文
     */
    public boolean isMemberAccess() {
        return type == Type.MEMBER_ACCESS;
    }
    
    /**
     * 是否为函数调用上下文
     */
    public boolean isFunctionCall() {
        return type == Type.FUNCTION_CALL;
    }
    
    /**
     * 是否为函数参数上下文
     */
    public boolean isFunctionParameter() {
        return type == Type.FUNCTION_PARAMETER;
    }
    
    /**
     * 是否为内置模块访问
     */
    public boolean isBuiltinModuleAccess() {
        if (!isMemberAccess()) {
            return false;
        }
        
        String type = getQualifierType();
        return type != null && isBuiltinModuleType(type);
    }
    
    /**
     * 是否为链式调用上下文
     */
    public boolean isChainedCall() {
        return chainReturnType != null;
    }
    
    /**
     * 获取链式调用返回类型
     */
    @Nullable
    public String getChainReturnType() {
        return chainReturnType;
    }
    
    /**
     * 是否为参数位置
     */
    public boolean isParameterPosition() {
        return type == Type.FUNCTION_PARAMETER;
    }
    
    /**
     * 获取函数名称
     */
    @Nullable
    public String getFunctionName() {
        return functionName;
    }
    
    /**
     * 获取参数索引
     */
    public int getParameterIndex() {
        return parameterIndex;
    }
    
    /**
     * 检查是否为内置模块类型
     */
    private boolean isBuiltinModuleType(@NotNull String type) {
        return type.equals("db") || type.equals("http") || type.equals("request") || 
               type.equals("response") || type.equals("env") || type.equals("log");
    }
    
    @Override
    public String toString() {
        return "CompletionContext{" +
                "type=" + type +
                ", qualifier='" + qualifier + '\'' +
                ", qualifierType='" + qualifierType + '\'' +
                '}';
    }
}