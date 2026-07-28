package com.qinghuan.pojo.entity;

import com.qinghuan.pojo.enums.AccountRole;
import com.qinghuan.pojo.enums.AccountStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAccount extends BaseEntity {

    private String loginName;
    private String passwordHash;
    private String displayName;
    private String phone;
    private AccountRole roleCode;
    private Long venueId;
    private AccountStatus status;
}
