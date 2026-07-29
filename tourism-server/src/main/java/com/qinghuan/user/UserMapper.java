package com.qinghuan.user;

import com.qinghuan.pojo.entity.UserAccount;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    public void saveAccount(UserAccount userAccount);
    public UserAccount getAccountByLoginNameAndPasswordHash(String loginName, String passwordHash);
    public UserAccount getAccountByLoginName(String loginName);
}
