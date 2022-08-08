package com.peixinchen.diaocha.data_object;

import lombok.Data;

@Data
public class QuestionDO {
    public Integer qid;
    public Integer uid;
    public String question;
    public String options;

    public QuestionDO(Integer uid, String question, String options) {
        this.uid = uid;
        this.question = question;
        this.options = options;
    }

    public QuestionDO(int qid, int uid, String question, String options) {
        this.qid = qid;
        this.uid = uid;
        this.question = question;
        this.options = options;
    }
}
