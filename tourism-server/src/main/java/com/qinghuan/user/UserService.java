package com.qinghuan.user;

import com.qinghuan.pojo.dto.StaffAccountUpdateDTO;
import com.qinghuan.pojo.dto.UserAccountPageQueryDTO;
import com.qinghuan.pojo.entity.UserAccount;
import com.qinghuan.pojo.enums.AccountStatus;
import com.qinghuan.pojo.vo.PageResult;
import com.qinghuan.pojo.vo.UserAccountVO;

public interface UserService {
    //保存工作人员账号
    public void saveStaffAccount(UserAccount staffAccount);
    //根据登录用户名和哈希密码获取账号信息
    public UserAccount getAccountByLoginNameandPasswordHash(String loginName, String passwordHash);

    //工作人员账号分页查询
    public PageResult<UserAccountVO> StaffAccountPageQuery(UserAccountPageQueryDTO userAccountPageQueryDTO);

    //修改工作人员账号状态
    public void changeStaffAccountStatus(AccountStatus status, Long id);

    /** 修改当前运营者所属景点内的工作人员基本资料。 */
    void updateStaffAccount(Long staffId, StaffAccountUpdateDTO updateDTO);
}
