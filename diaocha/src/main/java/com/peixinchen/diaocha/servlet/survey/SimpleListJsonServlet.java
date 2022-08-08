package com.peixinchen.diaocha.servlet.survey;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peixinchen.diaocha.data_object.SurveyDO;
import com.peixinchen.diaocha.repository.SurveyRepo;
import com.peixinchen.diaocha.view_object.SurveySimpleListView;
import com.peixinchen.diaocha.view_object.UserVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/survey/simple-list.json")
public class SimpleListJsonServlet extends HttpServlet {
    private final SurveyRepo surveyRepo = new SurveyRepo();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserVO currentUser = null;
        HttpSession session = req.getSession(false);
        if (session != null) {
            currentUser = (UserVO) session.getAttribute("currentUser");
        }

        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();

        SurveySimpleListView resultView = new SurveySimpleListView();
        if (currentUser == null) {
            String json = objectMapper.writeValueAsString(resultView);
            writer.println(json);
            return;
        }

        List<SurveyDO> surveyDOList = surveyRepo.selectListByUid(currentUser.uid);
        resultView.currentUser = currentUser;
        resultView.surveyList = new ArrayList<>();
        for (SurveyDO surveyDO : surveyDOList) {
            SurveySimpleListView.SimpleSurveyView view = new SurveySimpleListView.SimpleSurveyView();
            view.sid = surveyDO.sid;
            view.title = surveyDO.title;
            resultView.surveyList.add(view);
        }

        String json = objectMapper.writeValueAsString(resultView);
        writer.println(json);
    }
}
