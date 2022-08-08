package com.peixinchen.diaocha.data_object;

import lombok.Data;

// 直接反映的是数据库表的数据
// 尽量让属性名称 等同于 表中的字段
// 数量也一样
@Data
public class UserDO {
    public Integer uid;
    public String username;
    public String password;

    public UserDO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserDO(int uid, String username, String password) {
        this.uid = uid;
        this.username = username;
        this.password = password;
    }
}
