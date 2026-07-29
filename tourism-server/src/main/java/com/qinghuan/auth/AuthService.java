package com.qinghuan.auth;

import com.qinghuan.pojo.entity.UserAccount;

public interface AuthService {
    public String login(UserAccount userAccount);
}
