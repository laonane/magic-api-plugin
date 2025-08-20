// Magic Script 智能补全测试文件
// 测试各种补全功能

// 1. 测试关键字补全 - 输入以下部分应该有补全提示
var userName = "test";
function getUserInfo() {
    return "user info";
}

// 2. 测试内置模块补全 - 输入以下应该有补全提示
var dbResult = db.
var httpResponse = http.
var paramValue = request.
response.

// 3. 测试具体方法补全
var users = db.select("SELECT * FROM users");
var userData = db.selectOne("SELECT * FROM users WHERE id = ?", 1);
var count = db.count("SELECT COUNT(*) FROM users");

var apiData = http.get("https://api.example.com/data");
var postResult = http.post("https://api.example.com/users", {name: "John"});

var userId = request.getParameter("id");
var authHeader = request.getHeader("Authorization");
var requestBody = request.getBody();

response.json({
    status: "success",
    data: users,
    count: count
});

// 4. 测试控制流语句
if (userId != null) {
    for (var i = 0; i < users.length; i++) {
        var user = users[i];
        // 处理用户数据
    }
} else {
    response.json({error: "用户ID不能为空"});
}

// 5. 测试异常处理
try {
    var result = db.transaction(function() {
        db.insert("INSERT INTO logs (message) VALUES (?)", "操作开始");
        var data = http.get("https://external-api.com/data");
        return data;
    });
} catch (e) {
    response.json({error: e.message});
} finally {
    // 清理资源
}

// 测试说明：
// - 在每个不完整的语句后按 Ctrl+Space 应该显示智能补全
// - db. 后应该显示：select, selectOne, insert, update, delete, transaction, page, cache
// - http. 后应该显示：get, post, put, delete, patch, head, options  
// - request. 后应该显示：getParameter, getHeader, getBody, getMethod, getPath, getCookie, getSession
// - response. 后应该显示：json, text, setHeader, setStatus, setCookie