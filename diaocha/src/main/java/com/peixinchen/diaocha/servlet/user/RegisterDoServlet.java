package com.peixinchen.diaocha.servlet.user;

import com.peixinchen.diaocha.service.UserService;
import com.peixinchen.diaocha.view_object.UserVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/user/register.do")
public class RegisterDoServlet extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. 读取用户输入的 用户名 + 密码
        req.setCharacterEncoding("utf-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        // TODO: 完成参数的合法性校验工作

        // 2. 进行注册（密码加密  +  表的插入操作），所以，交给 service 对象进行处理
        UserVO userVO = userService.register(username, password);

        // 3. 注册成功之后，同步进行登录（将当前注册用户，放入 session 中）
        HttpSession session = req.getSession();
        session.setAttribute("currentUser", userVO);

        // 4. 在响应中，写下注册成功（或者可以重定向到其他页面）
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/plain");
        PrintWriter writer = resp.getWriter();
        writer.println("注册成功");
    }
}
