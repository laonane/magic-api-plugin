# Magic API IntelliJ IDEA Plugin

一个为 Magic API 框架提供 IntelliJ IDEA 支持的插件，支持 Magic Script (.ms) 文件的语法高亮、代码补全、导航等功能。

## 项目简介

Magic API 是一个基于Java的接口快速开发框架，使用类似JavaScript语法的Magic Script编写业务逻辑。本插件为IntelliJ IDEA提供了完整的Magic Script文件支持。

## 功能特性

### 核心功能
- **语法高亮**: 为 .ms 文件提供完整的语法着色支持
- **智能代码补全**: 上下文感知的智能提示，包括：
  - 关键字自动补全
  - 内置模块智能提示（db、http、request、response、env、log）
  - API方法精准补全（如 `db.select`、`http.get` 等）
  - 方法参数提示和类型信息
  - 链式调用补全支持
- **代码导航**: 
  - Ctrl+点击跳转到定义
  - 跨文件引用解析和导航
  - 查找所有使用处 (Find Usages)
  - 重命名重构支持
- **文档支持**:
  - 悬停显示API方法文档
  - 参数说明和返回值类型
  - 使用示例展示
- **import/export支持**: 模块导入导出的语法支持和引用解析
- **语法检查**: 实时语法错误检测和提示

### 支持的Magic API模块
- **数据库模块 (db)**: select、selectOne、selectInt、selectValue、insert、update、delete、page、transaction
- **HTTP模块 (http)**: get、post、put、delete、patch  
- **请求模块 (request)**: getParameter、getHeader、getBody、getMethod、getPath、getCookie、getSession
- **响应模块 (response)**: json、text、setHeader、setStatus、setCookie
- **环境模块 (env)**: get、getProperty
- **日志模块 (log)**: info、debug、warn、error

## 技术栈

- **开发语言**: Java 11+
- **构建工具**: Gradle with IntelliJ Platform Plugin
- **语法解析**: JFlex + Grammar-Kit  
- **智能提示**: 基于IntelliJ Platform SDK的上下文分析
- **API文档**: 硬编码的Magic-API完整API定义
- **目标平台**: IntelliJ IDEA 2022.3+

## 项目结构

```
magic-api-plugin/
├── build.gradle                           # Gradle构建配置
├── src/main/
│   ├── java/com/magicapi/idea/            # 核心Java代码
│   │   ├── completion/                     # 智能补全
│   │   │   ├── model/                      # API定义数据模型
│   │   │   └── context/                    # 上下文分析
│   │   ├── navigation/                     # 代码导航
│   │   ├── documentation/                  # 文档提供
│   │   ├── lang/                          # 语言定义
│   │   │   ├── psi/                       # PSI结构
│   │   │   ├── parser/                    # 语法分析
│   │   │   └── lexer/                     # 词法分析
│   │   ├── highlighting/                  # 语法高亮
│   │   └── icons/                         # 图标资源
│   ├── resources/                         # 资源文件
│   │   ├── META-INF/plugin.xml           # 插件配置
│   │   └── icons/                        # 图标文件
│   └── grammar/                          # 语法定义文件
│       ├── MagicScript.bnf               # BNF语法规则
│       └── MagicScriptLexer.flex         # JFlex词法规则
├── src/test/                             # 测试代码
├── src/gen/                              # 生成的代码
└── dev-docs/                             # 开发文档
    ├── 智能提示改进计划.md                 # 功能改进详细计划
    └── ...                               # 其他开发文档
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
// 数据库操作 - 支持智能补全和参数提示
var users = db.select('SELECT * FROM users WHERE age > ?', 18);
var user = db.selectOne('SELECT * FROM users WHERE id = ?', userId);
var count = db.selectInt('SELECT COUNT(*) FROM users');

// HTTP请求 - 链式调用支持
var response = http.get('https://api.example.com/data')
                  .header('Authorization', 'Bearer token');
var result = http.post('https://api.example.com/users', userData);

// 请求处理 - 完整的API方法提示
var id = request.getParameter('id', '0');
var token = request.getHeader('Authorization');
var body = request.getBody();

// 响应处理 - 支持链式调用
return response.json({code: 200, data: users})
              .setHeader('Content-Type', 'application/json')
              .setStatus(200);

// 环境变量和日志
var dbUrl = env.get('DATABASE_URL');
log.info('用户{}登录成功', userId);
```

### 智能提示功能演示

1. **模块方法补全**: 输入 `db.` 自动显示所有可用方法
2. **参数提示**: 显示方法的参数类型和说明
3. **返回值信息**: 悬停显示方法返回类型和描述
4. **链式调用**: `response.json().setHeader()` 等链式调用的智能补全
5. **跨文件引用**: import的模块和函数自动补全和跳转

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