package com.peixinchen.diaocha.servlet.survey;

import com.peixinchen.diaocha.data_object.SurveyDO;
import com.peixinchen.diaocha.repository.SurveyRepo;
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

@WebServlet("/survey/create.do")
public class CreateDoServlet extends HttpServlet {
    private final SurveyRepo surveyRepo = new SurveyRepo();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. 读取用户参数
        req.setCharacterEncoding("utf-8");
        String title = req.getParameter("title");
        String brief = req.getParameter("brief");
        Log.println("参数 title = " + title);
        Log.println("参数 brief = " + brief);

        // 2. 验证用户是否登录
        UserVO currentUser = null;
        HttpSession session = req.getSession(false);
        if (session != null) {
            currentUser = (UserVO) session.getAttribute("currentUser");
        }

        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/plain");
        PrintWriter writer = resp.getWriter();

        if (currentUser == null) {
            Log.println("用户未登录");
            writer.println("必须登录后才能使用");
            return;
        }

        // 3. 一个插入操作
        SurveyDO surveyDO = new SurveyDO(currentUser.uid, title, brief);
        surveyRepo.insert(surveyDO);
        Log.println("插入完成: " + surveyDO);

        // 4. 响应插入完成
        writer.println("插入成功");
    }
}
