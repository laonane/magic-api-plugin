// 简单的Magic Script测试
var message = "Hello Magic API";

function test() {
    db.select("SELECT 1");
    http.get("http://localhost:8080/api/test");
    var param = request.getParameter("test");
    response.json({result: "success"});
}