package com.qinghuan.pojo.vo;

import com.qinghuan.pojo.enums.AccountRole;
import com.qinghuan.pojo.enums.AccountStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
public class UserAccountVO {
    private Long id;
    private String loginName;
    private String displayName;
    private String phone;
    private AccountRole roleCode;
    private Long venueId;
    private AccountStatus status;
    private LocalDate createdAt;
}
