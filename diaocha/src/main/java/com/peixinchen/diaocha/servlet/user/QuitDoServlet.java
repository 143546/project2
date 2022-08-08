package com.peixinchen.diaocha.servlet.user;

import com.peixinchen.diaocha.util.Log;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/user/quit.do")
public class QuitDoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Log.println("session 对象: " + session);
        if (session != null) {
            session.removeAttribute("currentUser");
            Log.println("清空 session 中的 currentUser");
        }

        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/plain");
        PrintWriter writer = resp.getWriter();
        Log.println("退出登录成功");
        writer.println("退出成功");
    }
}
