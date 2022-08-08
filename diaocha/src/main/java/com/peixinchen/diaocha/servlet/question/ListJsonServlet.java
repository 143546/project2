package com.peixinchen.diaocha.servlet.question;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peixinchen.diaocha.service.QuestionService;
import com.peixinchen.diaocha.util.Log;
import com.peixinchen.diaocha.view_object.QuestionListView;
import com.peixinchen.diaocha.view_object.UserVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/question/list.json")
public class ListJsonServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final QuestionService questionService = new QuestionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.setCharacterEncoding("utf-8");
//        resp.setContentType("application/json");
//        PrintWriter writer = resp.getWriter();
//        String json = "{\"currentUser\": {\"uid\": 3, \"username\": \"小狗\"}, \"questionList\": [{\"qid\": 1, \"question\": \"我是谁\", \"options\": [\"Aa\", \"Bb\", \"Cc\", \"Dd\"]}]}";
//        writer.println(json);
        // 0. 读取 page 参数
        req.setCharacterEncoding("utf-8");
        Log.println("query string: " + req.getQueryString());
        String pageString = req.getParameter("page");
        Log.println("page 参数是: " + pageString);
        // 如果出现 page 的相关错误，暂时不做出错处理了，而是给 page 一个相对正确的值
        int page;
        if (pageString == null || pageString.trim().isEmpty()) {
            Log.println("用户没有传入 page 信息，让 page = 1");
            page = 1;
        } else {
            try {
                page = Integer.parseInt(pageString.trim());
            } catch (NumberFormatException exc) {
                Log.println("用户传入的 page 不是合法数字，让 page = 1");
                page = 1;
            }
        }

        // 1. 获取当前登录用户
        UserVO currentUser = null;
        HttpSession session = req.getSession(false);
        if (session != null) {
            currentUser = (UserVO) session.getAttribute("currentUser");
        }

        // 为响应做准备
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();

        // 如果用户未登录
        // { "currentUser": null }
        if (currentUser == null) {
            Log.println("用户未登录");
            QuestionListView resultView = new QuestionListView();
            // 下面这两步可以不写的
            resultView.currentUser = null;
            resultView.questionList = null;

            String json = objectMapper.writeValueAsString(resultView);
            writer.println(json);
            return;
        }

        // 2. 使用 service 对象，得到想要的题目列表（在已经登录的情况下）
        QuestionListView resultView = questionService.list(currentUser, page);
        Log.println("得到的结果对象是: " + resultView);
        // 3. 使用 jackson 将 对象进行 JSON 格式的序列化
        String json = objectMapper.writeValueAsString(resultView);
        Log.println("JSON 序列化后的结果是: " + json);
        // 4. 响应数据
        writer.println(json);
    }
}
