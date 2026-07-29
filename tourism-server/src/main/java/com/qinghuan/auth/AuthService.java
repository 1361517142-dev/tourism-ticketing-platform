package com.qinghuan.auth;

import com.qinghuan.pojo.entity.UserAccount;

public interface AuthService {
    //登录
    public String login(UserAccount userAccount);
}
