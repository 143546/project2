package com.peixinchen.diaocha.servlet.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peixinchen.diaocha.repository.ActivitySurveyRepo;
import com.peixinchen.diaocha.view_object.ActivityView;
import com.peixinchen.diaocha.view_object.UserVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.rmi.ServerRuntimeException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/activity/list.json")
public class ListJsonServlet extends HttpServlet {
    private final ActivitySurveyRepo repo = new ActivitySurveyRepo();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 这个里只写正常流程，所有非正常情况全部以异常的形式抛出
        HttpSession session = req.getSession(false);
        if (session == null) {
            throw new RuntimeException("session 为空，说明没登录");
        }

        UserVO currentUser = (UserVO) session.getAttribute("currentUser");
        if (currentUser == null) {
            throw new RuntimeException("用户没有登录");
        }

        List<ActivityView> activityViewList = repo.selectListByUid(currentUser.uid);
        // 使用 Map 替代专门定义一个类
        Map<String, Object> view = new HashMap<>();
        view.put("currentUser", currentUser);
        view.put("activityList", activityViewList);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(view);

        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");
        resp.getWriter().println(json);
    }
}
