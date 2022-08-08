package com.peixinchen.diaocha.servlet.question;

import com.peixinchen.diaocha.service.QuestionService;
import com.peixinchen.diaocha.util.Log;
import com.peixinchen.diaocha.view_object.UserVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

@WebServlet("/question/create.do")
public class CreateDoServlet extends HttpServlet {
    private final QuestionService questionService = new QuestionService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. 读取用户输入的题目、选项s
        req.setCharacterEncoding("utf-8");
        String question = req.getParameter("question");
        // 使用这个，可以将请求参数中 name 同名的读到一个数组中
        String[] options = req.getParameterValues("option");

        Log.println("读取到用户提交的题目: " + question);
        Log.println("读取到用户提交的选项: " + Arrays.toString(options));

        // 2. 判断用户是否已经登录，如果没有登录，提示用户登录后才能使用
        UserVO currentUser = null;
        HttpSession session = req.getSession(false);
        if (session != null) {
            currentUser = (UserVO) session.getAttribute("currentUser");
            if (currentUser == null) {
                Log.println("说明 session 存在，但 session 中没有 currentUser，目前我们的代码中基本不会出现这个情况");
            }
        } else {
            Log.println("说明 session 不存在，原因可能是 Cookie 中没有 session-id，或者 session-id 对应的 session 对象没有");
            Log.println("可能浏览器清理过缓存了，要么可能是 Tomcat 重启过，要么就是没有登录过（包含注册）");
        }

        // 准备响应
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/plain");
        PrintWriter writer = resp.getWriter();

        if (currentUser == null) {
            Log.println("说明用户没有登录，没法使用后续功能");
            writer.println("必须登录后才能使用");
            return;
        }

        // 一定有用户登录了
        Log.println("当前登录的用户为: " + currentUser);

        // 这里暂时不考虑失败的情况（比如数据库出错）
        questionService.save(currentUser, question, options);

        // 提示用户保存成功
        Log.println("题目保存成功");
        writer.println("题目保存成功");
    }
}
