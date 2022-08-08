package com.peixinchen.diaocha.view_object;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class QuestionListView {
    public UserVO currentUser;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public PaginationView pagination;

    // 只有 questionList != null 的时候，才会出现在 JSON 中
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<QuestionView> questionList;
}
