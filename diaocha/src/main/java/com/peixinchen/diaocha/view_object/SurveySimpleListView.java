package com.peixinchen.diaocha.view_object;

import lombok.Data;

import java.util.List;

@Data
public class SurveySimpleListView {
    @Data
    public static class SimpleSurveyView {
        public Integer sid;
        public String title;
    }

    public UserVO currentUser;

    public List<SimpleSurveyView> surveyList;
}
