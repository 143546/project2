package com.peixinchen.diaocha.servlet.activity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peixinchen.diaocha.util.DBUtil;
import com.peixinchen.diaocha.util.Log;
import com.peixinchen.diaocha.view_object.UserVO;
import lombok.Data;
import lombok.SneakyThrows;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/activity/result.json")
public class ResultJsonServlet extends HttpServlet {
    private static class OptionView {
        public String name;
        public Integer value;

        public OptionView(OptionCount optionCount) {
            this.name = optionCount.option;
            this.value = optionCount.count;
        }
    }

    private static class QuestionView {
        public Integer qid;
        public String question;
        public OptionView[] results = new OptionView[4];

        public QuestionView(Question question) {
            this.qid = question.qid;
            this.question = question.question;
            for (int i = 0; i < 4; i++) {
                OptionCount optionCount = question.options[i];
                OptionView optionView = new OptionView(optionCount);
                results[i] = optionView;
            }
        }
    }

    private static class ResultView {
        public UserVO currentUser;
        public Integer aid;
        public String startedAt;
        public String endedAt;
        public Integer sid;
        public String title;
        public String brief;
        public List<QuestionView> questionList = new ArrayList<>();

        public ResultView(UserVO currentUser) {
            this.currentUser = currentUser;
        }

        @SneakyThrows
        public void set(ResultSet rs) {
            this.aid = rs.getInt("aid");
            this.startedAt = tsToString(rs.getTimestamp("started_at"));
            this.endedAt = tsToString(rs.getTimestamp("ended_at"));
            this.sid = rs.getInt("sid");
            this.title = rs.getString("title");
            this.brief = rs.getString("brief");
        }

        private String tsToString(Timestamp ts) {
            LocalDateTime localDateTime = ts.toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return formatter.format(localDateTime);
        }

        public void setQuestionList(List<Integer> qidList, Map<Integer, Question> qidToQuestionMap) {
            for (int qid : qidList) {
                Question question = qidToQuestionMap.get(qid);
                QuestionView view = new QuestionView(question);
                this.questionList.add(view);
            }
        }
    }

    @Data
    private static class OptionCount {
        public String option;
        public Integer count;

        public OptionCount(String option) {
            this.option = option;
            this.count = 0;
        }
    }

    @Data
    public static class Question {
        public Integer qid;
        public String question;
        public OptionCount[] options = new OptionCount[4];

        @SneakyThrows
        public Question(ResultSet rs, ObjectMapper objectMapper) {
            this.qid = rs.getInt("qid");
            this.question = rs.getString("question");
            List<String> optionList = objectMapper.readValue(rs.getString("options"), new TypeReference<List<String>>() {});
            this.options[0] = new OptionCount(optionList.get(0));
            this.options[1] = new OptionCount(optionList.get(1));
            this.options[2] = new OptionCount(optionList.get(2));
            this.options[3] = new OptionCount(optionList.get(3));
        }
    }

    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String aid = req.getParameter("aid");
        if (aid == null || aid.trim().isEmpty()) {
            throw new RuntimeException("必须带有 aid 参数");
        }

        try {
            Integer.parseInt(aid);
        } catch (NumberFormatException exc) {
            throw new RuntimeException("aid 参数必须是数字", exc);
        }

        HttpSession session = req.getSession(false);
        if (session == null) {
            throw new RuntimeException("用户未登录");
        }

        UserVO currentUser = (UserVO) session.getAttribute("currentUser");
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ResultView resultView = new ResultView(currentUser);
        // qid -> Question
        Map<Integer, Question> qidToQuestionMap = new HashMap<>();
        try (Connection c = DBUtil.connection()) {
            {
                String sql = "select aid, started_at, ended_at, a.sid, title, brief from activities a join surveys s on a.sid = s.sid where a.uid = ? and aid = ?";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setInt(1, currentUser.uid);
                    ps.setString(2, aid);

                    Log.println("执行 SQL: " + ps);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new RuntimeException("aid 对应的调查活动不存在");
                        }

                        resultView.set(rs);
                    }
                }
            }

            List<Integer> qidList = new ArrayList<>();
            List<String>  qidStringList = new ArrayList<>();
            {
                String sql = "select qid from relations where sid = ? order by rid";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setInt(1, resultView.sid);

                    Log.println("执行 SQL: " + ps);

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int qid = rs.getInt("qid");
                            qidList.add(qid);
                            qidStringList.add(String.valueOf(qid));
                        }
                    }
                }
            }

            if (qidList.isEmpty()) {
                throw new RuntimeException("问卷完全没有关联任何题目");
            }

            {
                String sqlFormat = "select qid, question, options from questions where qid in (%s)";
                String s = String.join(", ", qidStringList);
                String sql = String.format(sqlFormat, s);

                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    Log.println("执行 SQL: " + ps);

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            Question question = new Question(rs, objectMapper);
                            qidToQuestionMap.put(question.qid, question);
                        }
                    }
                }
            }

            Log.println(qidToQuestionMap);

            {
                String sql = "select answer from results where aid = ?";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setString(1, aid);

                    Log.println("执行 SQL: " + ps);

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) { // 第一层循环        N: 循环本次参与调查的人数
                            String answerJson = rs.getString("answer");
                            Map<String, String> answerMap = objectMapper.readValue(answerJson, new TypeReference<Map<String, String>>() {});

                            for (Map.Entry<String, String> entry : answerMap.entrySet()) {  // 第二层循环    M: 本次调查的题目数量
                                String key = entry.getKey();
                                String value = entry.getValue();

                                int qid = Integer.parseInt(key);
                                int index = Integer.parseInt(value);

                                if (!qidToQuestionMap.containsKey(qid)) {
                                    throw new RuntimeException("这个 qid 不存在");
                                }

                                Question question = qidToQuestionMap.get(qid);
                                OptionCount[] options = question.options;
                                OptionCount optionCount = options[index];
                                optionCount.count++;
                            }
                        }
                    }
                }
            }

            Log.println(qidToQuestionMap);

            // qidToQuestionMap -> ResultView
            // qidList 是保留题目的顺序，因为 map 中，把顺序信息丢失了
            resultView.setQuestionList(qidList, qidToQuestionMap);
        }


        String json = objectMapper.writeValueAsString(resultView);
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");
        resp.getWriter().println(json);
    }
}
