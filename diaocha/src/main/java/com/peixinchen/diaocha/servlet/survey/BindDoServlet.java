package com.peixinchen.diaocha.servlet.survey;

import com.peixinchen.diaocha.repository.RelationRepo;
import com.peixinchen.diaocha.util.Log;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/survey/bind.do")
public class BindDoServlet extends HttpServlet {
    private final RelationRepo relationRepo = new RelationRepo();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        Log.println("|" + req.getParameter("sid") + "|");
        int sid = Integer.parseInt(req.getParameter("sid"));
        // 这里是一个 stream 的用法
        // 等同于 遍历每个元素，调用 Integer 下的 parseInt
        // 把最终的机会收集到 List 中
        // String[] -> List<Integer>
        List<Integer> bindQidList = new ArrayList<>();
        if (req.getParameter("bind-qid") != null) {
            bindQidList = Arrays.stream(req.getParameterValues("bind-qid"))
                    .map(Integer::parseInt) // :: 方法引用
                    .collect(Collectors.toList());
        }

        List<Integer> unBindQidList = new ArrayList<>();
        if (req.getParameter("unbind-qid") != null) {
            unBindQidList = Arrays.stream(req.getParameterValues("unbind-qid"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
        Log.println("sid = " + sid);
        Log.println("bindQidList = " + bindQidList);
        Log.println("unBindQidList = " + unBindQidList);

        List<Integer> qidList = new ArrayList<>();
        qidList.addAll(bindQidList);
        qidList.addAll(unBindQidList);

        if (!qidList.isEmpty()) {
            relationRepo.deleteBySidAndQidList(sid, qidList);
        }
        if (!bindQidList.isEmpty()) {
            relationRepo.insertBySidAndQidList(sid, bindQidList);
        }
    }
}
