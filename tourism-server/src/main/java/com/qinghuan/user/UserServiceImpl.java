package com.qinghuan.user;

import com.qinghuan.auth.context.UserContext;
import com.qinghuan.common.exception.BusinessException;
import com.qinghuan.common.exception.ErrorCode;
import com.qinghuan.pojo.entity.UserAccount;
import com.qinghuan.pojo.enums.AccountRole;
import com.qinghuan.pojo.enums.AccountStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Transactional
    @Override
    public void saveStaffAccount(UserAccount userAccount) {
        log.info("保存工作人员账号：{}", userAccount);
        //检查是否已存在相同账号名
        if (userMapper.getAccountByLoginName(userAccount.getLoginName()) != null) {
            log.info("试图添加重复的账号名！");
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "账号名重复！");
        }

        // 密码哈希
        userAccount.setPasswordHash(DigestUtils.md5DigestAsHex(userAccount.getPassword().getBytes()));
        // 工作人员继承运营者的VenueId
        userAccount.setVenueId(UserContext.getRequired().venueId());
        userAccount.setRoleCode(AccountRole.STAFF);
        userAccount.setStatus(AccountStatus.ACTIVE);
        userAccount.setCreatedAt(LocalDateTime.now());
        userAccount.setUpdatedAt(LocalDateTime.now());
        userMapper.saveAccount(userAccount);
    }

    @Override
    public UserAccount getAccountByLoginNameandPasswordHash(String loginName, String passwordHash) {
        return userMapper.getAccountByLoginNameAndPasswordHash(loginName, passwordHash);
    }

}
