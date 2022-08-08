package com.peixinchen.diaocha.view_object;

import com.peixinchen.diaocha.data_object.UserDO;
import lombok.Data;

// @Data 的意思是：自动帮我们生成了 toString / equals / hashCode / getter / setter
@Data
public class UserVO {
    public Integer uid;
    public String username;

    public UserVO(UserDO userDO) {
        this.uid = userDO.uid;
        this.username = userDO.username;
    }
}
