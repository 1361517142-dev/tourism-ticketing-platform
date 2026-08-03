package com.qinghuan.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 游客新建参观人的请求参数。
 */
@Getter
@Setter
public class VisitorCreateDTO {

    @NotBlank(message = "参观人姓名不能为空")
    @Size(max = 50, message = "参观人姓名不能超过50个字符")
    private String name;

    @NotBlank(message = "证件类型不能为空")
    @Size(max = 20, message = "证件类型不能超过20个字符")
    private String idType;

    @NotBlank(message = "证件号码不能为空")
    @Size(max = 64, message = "证件号码不能超过64个字符")
    private String idNumber;

    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String phone;
}

