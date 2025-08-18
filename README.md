# Magic API IntelliJ IDEA Plugin

一个为 Magic API 框架提供 IntelliJ IDEA 支持的插件，支持 Magic Script (.ms) 文件的语法高亮、代码补全、导航等功能。

## 项目简介

Magic API 是一个基于Java的接口快速开发框架，使用类似JavaScript语法的Magic Script编写业务逻辑。本插件为IntelliJ IDEA提供了完整的Magic Script文件支持。

## 功能特性

- **语法高亮**: 为 .ms 文件提供语法着色
- **代码补全**: 智能提示关键字和内置API
- **代码导航**: 支持定义跳转和引用查找
- **语法检查**: 实时语法错误提示
- **代码格式化**: 自动代码格式化

## 技术栈

- **开发语言**: Java 11+
- **构建工具**: Gradle with IntelliJ Platform Plugin
- **语法解析**: JFlex + Grammar-Kit
- **目标平台**: IntelliJ IDEA 2022.3+

## 项目结构

```
magic-api-plugin/
├── build.gradle                    # Gradle构建配置
├── src/main/
│   ├── java/com/magicapi/idea/    # 核心Java代码
│   ├── resources/                  # 资源文件
│   └── grammar/                    # 语法定义文件
├── src/test/                       # 测试代码
└── src/gen/                        # 生成的代码
```

## 构建和运行

### 前置要求

- JDK 11 或更高版本
- Gradle 7.0+
- IntelliJ IDEA 2022.3+

### 构建命令

```bash
# 编译项目
./gradlew compileJava

# 构建插件
./gradlew buildPlugin

# 运行测试
./gradlew test

# 启动IDE进行调试
./gradlew runIde
```

## Magic Script 语法支持

### 支持的语法元素

- 变量声明: `var name = value`
- 函数定义: `function name() { }`
- 控制流: `if/else`, `for`, `while`
- 内置模块: `db`, `http`, `request`, `response`

### 内置API示例

```javascript
// 数据库操作
var users = db.select('select * from users');

// HTTP请求
var data = http.get('https://api.example.com/data');

// 响应处理
return response.json(users);
```

## 开发指南

### 生成语法分析器

```bash
# 生成词法分析器
./gradlew generateLexer

# 生成语法分析器
./gradlew generateParser
```

### 测试

```bash
# 运行所有测试
./gradlew test

# 运行特定测试
./gradlew test --tests "*ParsingTest"
```

## 贡献

欢迎提交 Issue 和 Pull Request 来改进这个插件。

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请通过 GitHub Issues 联系我们。