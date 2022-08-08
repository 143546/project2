package com.peixinchen.diaocha.servlet.survey;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peixinchen.diaocha.service.SurveyService;
import com.peixinchen.diaocha.view_object.SurveyBindView;
import com.peixinchen.diaocha.view_object.UserVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/survey/bind.json")
public class BindJsonServlet extends HttpServlet {
    private final SurveyService surveyService = new SurveyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        int page = Integer.parseInt(req.getParameter("page"));
        int sid = Integer.parseInt(req.getParameter("sid"));
        UserVO currentUser = null;
        HttpSession session = req.getSession(false);
        if (session != null) {
            currentUser = (UserVO) session.getAttribute("currentUser");
        }

        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();

        if (currentUser == null) {
            SurveyBindView resultView = new SurveyBindView();
            String json = objectMapper.writeValueAsString(resultView);
            writer.println(json);
        } else {
            SurveyBindView resultView = surveyService.bindCandidates(currentUser, sid, page);
            String json = objectMapper.writeValueAsString(resultView);
            writer.println(json);
        }
    }
}
