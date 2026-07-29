package com.qinghuan.auth;

import com.qinghuan.auth.jwt.JwtUtils;
import com.qinghuan.auth.model.LoginUser;
import com.qinghuan.common.exception.BusinessException;
import com.qinghuan.common.exception.ErrorCode;
import com.qinghuan.pojo.entity.UserAccount;
import com.qinghuan.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public String login(UserAccount userAccount) {
        //获取登录用户名和哈希密码
        String loginName = userAccount.getLoginName();
        String passwordHash = DigestUtils.md5DigestAsHex(userAccount.getPassword().getBytes());

        //根据登录用户名和哈希密码获取账号信息
        UserAccount foundUserAccount = userService.getAccountByLoginNameandPasswordHash(loginName, passwordHash);

        //验证账号信息
        if (foundUserAccount == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        //签发Token
        String token = jwtUtils.generateAccessToken(
                new LoginUser(foundUserAccount.getId(), foundUserAccount.getLoginName(), foundUserAccount.getRoleCode(), foundUserAccount.getVenueId()));

        return token;
    }


}
