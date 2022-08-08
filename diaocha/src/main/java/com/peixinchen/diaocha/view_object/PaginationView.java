package com.peixinchen.diaocha.view_object;

import lombok.Data;

@Data
public class PaginationView {
    public Integer countPerPage;    // 每页多少条
    public Integer currentPage; // 当前是第几页
    public Integer totalPage;   // 一共多少页
}
