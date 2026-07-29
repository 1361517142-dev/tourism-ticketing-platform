package com.qinghuan.user;

import com.qinghuan.pojo.entity.UserAccount;

public interface UserService {
    public void saveStaffAccount(UserAccount staffAccount);
    public UserAccount getAccountByLoginNameandPasswordHash(String loginName, String passwordHash);
}
