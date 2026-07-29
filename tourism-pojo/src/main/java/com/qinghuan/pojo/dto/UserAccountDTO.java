package com.qinghuan.pojo.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class UserAccountDTO {
    private String loginName;
    private String password;
    private String displayName;
    private String phone;
}
