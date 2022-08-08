package com.peixinchen.diaocha.view_object;

import com.peixinchen.diaocha.data_object.SurveyDO;
import lombok.Data;

@Data
public class SurveyView {
    public Integer sid;
    public String title;
    public String brief;

    public SurveyView(SurveyDO surveyDO) {
        this.sid = surveyDO.sid;
        this.title = surveyDO.title;
        this.brief = surveyDO.brief;
    }
}
