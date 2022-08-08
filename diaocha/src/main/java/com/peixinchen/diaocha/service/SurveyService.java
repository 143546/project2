package com.peixinchen.diaocha.service;

import com.peixinchen.diaocha.data_object.QuestionDO;
import com.peixinchen.diaocha.data_object.SurveyDO;
import com.peixinchen.diaocha.repository.QuestionRepo;
import com.peixinchen.diaocha.repository.RelationRepo;
import com.peixinchen.diaocha.repository.SurveyRepo;
import com.peixinchen.diaocha.view_object.SurveyBindView;
import com.peixinchen.diaocha.view_object.SurveyListView;
import com.peixinchen.diaocha.view_object.SurveyView;
import com.peixinchen.diaocha.view_object.UserVO;

import java.util.ArrayList;
import java.util.List;

public class SurveyService {
    private final SurveyRepo surveyRepo = new SurveyRepo();
    private final RelationRepo relationRepo = new RelationRepo();
    private final QuestionRepo questionRepo = new QuestionRepo();

    public SurveyListView list(UserVO user) {
        SurveyListView resultView = new SurveyListView();
        resultView.currentUser = user;

        resultView.surveyList = new ArrayList<>();
        List<SurveyDO> surveyDOList = surveyRepo.selectListByUid(user.uid);
        for (SurveyDO surveyDO : surveyDOList) {
            SurveyView view = new SurveyView(surveyDO);
            resultView.surveyList.add(view);
        }

        return resultView;
    }

    public SurveyBindView bindCandidates(UserVO user, int sid, int page) {
        SurveyDO surveyDO = surveyRepo.selectOneBySidAndUid(sid, user.uid);
        if (surveyDO == null) {
            // 要绑定的 sid（问卷）是不存在的
            // TODO: 具体怎么处理
            throw new RuntimeException("404");
        }

        List<Integer> qidBoundenList = relationRepo.selectQidListBySid(sid);

        if (page < 1) {
            page = 1;
        }
        int limit = 5;
        int offset = (page - 1) * limit;    // 暂时不考虑 page 是错误值的情况
        List<QuestionDO> questionDOList = questionRepo.selectListByUidLimitOffset(user.uid, limit, offset);

        return new SurveyBindView(user, surveyDO, qidBoundenList, questionDOList);
    }
}
