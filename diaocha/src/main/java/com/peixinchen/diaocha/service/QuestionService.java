package com.peixinchen.diaocha.service;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peixinchen.diaocha.data_object.QidToRefCountDO;
import com.peixinchen.diaocha.data_object.QuestionDO;
import com.peixinchen.diaocha.repository.QuestionRepo;
import com.peixinchen.diaocha.repository.RelationRepo;
import com.peixinchen.diaocha.util.Log;
import com.peixinchen.diaocha.view_object.PaginationView;
import com.peixinchen.diaocha.view_object.QuestionListView;
import com.peixinchen.diaocha.view_object.QuestionView;
import com.peixinchen.diaocha.view_object.UserVO;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionService {
    private final ObjectMapper objectMapper;
    private final QuestionRepo questionRepo = new QuestionRepo();
    private final RelationRepo relationRepo = new RelationRepo();

    public QuestionService() {
        objectMapper = new ObjectMapper();
        objectMapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());   // 让序列化出来的 JSON 带有空格和换行
    }

    @SneakyThrows
    public void save(UserVO userVO, String question, String[] options) {
        // 1. 将 options 这个数组，通过 JSON 格式，变成一个字符串，最后保存到数据库中
        Log.println("准备对选项数组进行 JSON 序列化");
        String optionsJsonString = objectMapper.writeValueAsString(options);
        Log.println("JSON: " + optionsJsonString);

        // 2. 使用 QuestionRepo 对象，对数据进行插入表操作
        QuestionDO questionDO = new QuestionDO(userVO.uid, question, optionsJsonString);
        questionRepo.insert(questionDO);
        Log.println("插入成功: " + questionDO);
    }

    public QuestionListView list(UserVO user, int page) {
        // 0. 定义关于分页的基本信息
        int countPerPage = 5;   // 规定每页就 5 条记录

        PaginationView paginationView = new PaginationView();
        paginationView.countPerPage = countPerPage;
        paginationView.currentPage = page;

        int count = questionRepo.selectCountByUid(user.uid);
        Log.println("得到的结果是: " + count);
        if (count == 0) {
            // 针对 count 为 0 的情况，特殊处理
            Log.println("针对 count == 0 的特殊处理");
            QuestionListView resultView = new QuestionListView();
            resultView.currentUser = user;
            paginationView.totalPage = 0;
            resultView.pagination = paginationView;
            resultView.questionList = new ArrayList<>();
            return resultView;
        }
        int totalPage = count / countPerPage;
        if (count % countPerPage != 0) {
            totalPage++;    // 这一步是为了向上取整
        }
        paginationView.totalPage = totalPage;       // select count(*) where uid = 4 / countPerPage 需要向上取整

        // page 肯定是一个整数，但不一定是一个合法的页面数
        // page 应该满足 [1, totalPage]
        if (page < 1) {
            Log.println("page 过小了，修改成 page = 1");
            page = 1;
        }
        if (page > totalPage) {
            Log.println("page 过大了，修改成 page = 最大值:" + totalPage);
            page = totalPage;
        }
        // 根据已知信息去算 SQL 中 limit + offset
        int limit = countPerPage;
        int offset = (page - 1) * countPerPage;

        // 1. 查询
        List<QuestionDO> questionDOList = questionRepo.selectListByUidLimitOffset(user.uid, limit, offset);
        Log.println("从 questions 表中查到的结果: " + questionDOList);
        if (questionDOList.isEmpty()) {
            QuestionListView resultView = new QuestionListView();
            resultView.currentUser = user;
            resultView.pagination = paginationView;
            resultView.questionList = new ArrayList<>();
            return resultView;
        }

        // 1.1 从 questionDOList，先把 qidList 提取出来
        List<Integer> qidList = new ArrayList<>();
        for (QuestionDO questionDO : questionDOList) {
            qidList.add(questionDO.qid);
        }
        List<QidToRefCountDO> qidToRefCountDOList = relationRepo.selectCountGroupByQid(qidList);
        Log.println("查询关联数据: " + qidToRefCountDOList);
        // 把 List<QidToRefCountDO> 转换成 Map<qid, refCount>
        Map<Integer, Integer> qidToRefCountMap = new HashMap<>();
        for (QidToRefCountDO qidToRefCountDO : qidToRefCountDOList) {
            qidToRefCountMap.put(qidToRefCountDO.qid, qidToRefCountDO.refCount);
        }
        Log.println("qid to ref count map: " + qidToRefCountMap);

        // 之所以要转换，是因为表中存储的数据和最终想展示的数据不是完全一致的
        // 2. 进行必要的个数转换 List<QuestionDO> -> QuestionListView
        QuestionListView resultView = new QuestionListView();
        resultView.currentUser = user;
        resultView.pagination = paginationView;
        resultView.questionList = new ArrayList<>();
        for (QuestionDO questionDO : questionDOList) {
            QuestionView view = new QuestionView(objectMapper, questionDO, qidToRefCountMap.getOrDefault(questionDO.qid, 0));
            resultView.questionList.add(view);
        }
        Log.println("最终响应的结果: " + resultView);
        return resultView;
    }
}
