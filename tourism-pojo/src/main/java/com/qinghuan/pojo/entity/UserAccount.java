package com.qinghuan.pojo.entity;

import com.qinghuan.pojo.enums.AccountRole;
import com.qinghuan.pojo.enums.AccountStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserAccount extends BaseEntity {
    private Long id;
    private String loginName;
    private String passwordHash;
    private String password;
    private String displayName;
    private String phone;
    private AccountRole roleCode;
    private Long venueId;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
