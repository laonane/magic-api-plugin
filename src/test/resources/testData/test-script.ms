// Magic Script测试文件
var name = "Hello World";
var count = 10;

function testFunction() {
    var users = db.select("SELECT * FROM users");
    var response = http.get("https://api.example.com/data");
    
    return {
        users: users,
        response: response,
        message: name
    };
}

// 测试内置模块
var param = request.getParameter("id");
response.json({
    status: "success",
    data: testFunction()
});