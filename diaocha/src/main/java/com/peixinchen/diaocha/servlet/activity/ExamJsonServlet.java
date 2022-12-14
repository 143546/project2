package com.peixinchen.diaocha.servlet.activity;

import com.fasterxml.jackson.core.type.TypeReference;
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
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/activity/exam.json")
public class ExamJsonServlet extends HttpServlet {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static class QuestionView {
        public Integer qid;
        public String question;
        public List<String> optionList;

        @SneakyThrows
        public QuestionView(int qid, String question, String options) {
            this.qid = qid;
            this.question = question;
            this.optionList = objectMapper.readValue(options, new TypeReference<List<String>>() {
            });
        }
    }

    static class ResultView {
        public Integer aid;
        public String title;
        public String brief;
        public List<QuestionView> questionList = new ArrayList<>();
    }

    @Override
    @SneakyThrows
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int aid = Integer.parseInt(req.getParameter("aid"));

        ResultView resultView = new ResultView();

        String sql = "select title, brief, q.qid, question, options " +
                "from activities a " +
                "join surveys s on a.sid = s.sid " +
                "join relations r on s.sid = r.sid " +
                "join questions q on r.qid = q.qid " +
                "where aid = ? order by q.qid";
        try (Connection c = DBUtil.connection()) {
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, aid);

                Log.println("?????? SQL: " + ps);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        resultView.aid = aid;
                        resultView.title = rs.getString("title");
                        resultView.brief = rs.getString("brief");

                        QuestionView questionView = new QuestionView(
                                rs.getInt("qid"),
                                rs.getString("question"),
                                rs.getString("options")
                        );
                        resultView.questionList.add(questionView);
                    }
                }
            }
        }

        String json = objectMapper.writeValueAsString(resultView);

        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");
        resp.getWriter().println(json);
    }
}
