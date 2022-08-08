package com.peixinchen.diaocha.servlet.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peixinchen.diaocha.util.DBUtil;
import com.peixinchen.diaocha.util.Log;
import lombok.SneakyThrows;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/activity/exam.do")
public class ExamDoServlet extends HttpServlet {
    @SneakyThrows
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");

        String aid = req.getParameter("aid");
        String nickname = req.getParameter("nickname");
        String phone = req.getParameter("phone");
        Enumeration<String> parameterNames = req.getParameterNames();
        Map<String, String> answer = new HashMap<>();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            if (name.startsWith("qid-")) {
                String qid = name.substring("qid-".length());
                String value = req.getParameter(name);
                answer.put(qid, value);
            }
        }

        Log.println("aid = " + aid);
        Log.println("nickname = " + nickname);
        Log.println("phone = " + phone);
        ObjectMapper objectMapper = new ObjectMapper();
        String answerJson = objectMapper.writeValueAsString(answer);
        Log.println("answer = " + answerJson);

        try (Connection c = DBUtil.connection()) {
            String sql = "insert into results (aid, nickname, phone, answer) values (?, ?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, aid);
                ps.setString(2, nickname);
                ps.setString(3, phone);
                ps.setString(4, answerJson);

                Log.println("执行 SQL: " + ps);

                ps.executeUpdate();
            }
        }

        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/plain");
        resp.getWriter().printf("感谢参加调查问卷");
    }
}
