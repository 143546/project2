package com.peixinchen.diaocha.view_object;

import com.peixinchen.diaocha.data_object.QuestionDO;
import lombok.Data;

import java.util.List;

@Data
public class QuestionBindView {
    public Integer qid;
    public String question;
    public boolean bounden;

    public QuestionBindView(QuestionDO questionDO, List<Integer> qidBoundenList) {
        this.qid = questionDO.qid;
        this.question = questionDO.question;
        this.bounden = qidBoundenList.contains(questionDO.qid);
    }
}
