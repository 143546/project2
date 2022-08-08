package com.peixinchen.diaocha.servlet.survey;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peixinchen.diaocha.service.SurveyService;
import com.peixinchen.diaocha.util.Log;
import com.peixinchen.diaocha.view_object.SurveyListView;
import com.peixinchen.diaocha.view_object.UserVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/survey/list.json")
public class ListJsonServlet extends HttpServlet {
    private final SurveyService surveyService = new SurveyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserVO currentUser = null;
        HttpSession session = req.getSession();
        if (session != null) {
            currentUser = (UserVO) session.getAttribute("currentUser");
        }

        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();

        if (currentUser == null) {
            SurveyListView resultView = new SurveyListView();
            String json = objectMapper.writeValueAsString(resultView);
            Log.println("用户未登录，JSON: " + json);
            writer.println(json);
            return;
        }

        SurveyListView resultView = surveyService.list(currentUser);
        String json = objectMapper.writeValueAsString(resultView);
        Log.println("JSON: " + json);
        writer.println(json);
    }
}
