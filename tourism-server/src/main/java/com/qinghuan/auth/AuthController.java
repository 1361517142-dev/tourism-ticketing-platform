package com.qinghuan.auth;

import com.qinghuan.common.response.ApiResponse;
import com.qinghuan.pojo.dto.UserAccountDTO;
import com.qinghuan.pojo.entity.UserAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 登录
    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody UserAccountDTO userAccountDTO) {
        log.info("登录账号: {}", userAccountDTO.getLoginName());
        UserAccount userAccount = new UserAccount();
        BeanUtils.copyProperties(userAccountDTO, userAccount);
        String token = authService.login(userAccount);
        return ApiResponse.success(token);
    }


}
