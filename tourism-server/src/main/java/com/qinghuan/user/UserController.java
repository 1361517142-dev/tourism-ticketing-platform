package com.qinghuan.user;

import com.qinghuan.common.response.ApiResponse;
import com.qinghuan.pojo.dto.UserAccountDTO;
import com.qinghuan.pojo.entity.UserAccount;
import com.qinghuan.pojo.enums.AccountRole;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 保存工作人员账号
     */
    @PostMapping("/operator/staff")
    public ApiResponse<Void> saveStaffAccount(@RequestBody UserAccountDTO userAccountDTO) {
        UserAccount staffAccount = new UserAccount();
        BeanUtils.copyProperties(userAccountDTO, staffAccount);
        userService.saveStaffAccount(staffAccount);
        return ApiResponse.success();
    }
}
