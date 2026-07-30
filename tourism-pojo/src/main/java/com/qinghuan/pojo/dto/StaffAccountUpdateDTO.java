package com.qinghuan.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 运营者修改工作人员基本资料的请求参数。
 * 登录账号、密码、状态和所属景点由各自的业务接口处理。
 */
@Getter
@Setter
public class StaffAccountUpdateDTO {

    @NotBlank(message = "工作人员姓名不能为空")
    @Size(max = 50, message = "工作人员姓名不能超过50个字符")
    private String displayName;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String phone;
    private LocalDateTime updateAt;
}
