// Magic Script 示例代码
var name = "张三";
var age = 25;

function getUserById(id) {
    var user = db.selectOne("SELECT * FROM users WHERE id = ?", [id]);
    if (user) {
        return {
            id: user.id,
            name: user.name,
            email: user.email
        };
    }
    return null;
}

// HTTP请求处理
var users = getUserById(request.getParameter("id"));
if (users) {
    response.json(users);
} else {
    response.setStatus(404);
    response.json({error: "User not found"});
}