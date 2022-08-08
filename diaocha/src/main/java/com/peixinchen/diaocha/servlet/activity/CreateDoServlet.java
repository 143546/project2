package com.peixinchen.diaocha.servlet.activity;

import com.peixinchen.diaocha.repository.ActivityRepo;
import com.peixinchen.diaocha.view_object.UserVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/activity/create.do")
public class CreateDoServlet extends HttpServlet {
    private final ActivityRepo activityRepo = new ActivityRepo();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        String sid = req.getParameter("sid");
        String started_at = req.getParameter("started_at");
        String ended_at = req.getParameter("ended_at");
        UserVO currentUser = (UserVO) req.getSession(true).getAttribute("currentUser");

        activityRepo.insert(currentUser.uid, sid, started_at, ended_at);
    }
}
