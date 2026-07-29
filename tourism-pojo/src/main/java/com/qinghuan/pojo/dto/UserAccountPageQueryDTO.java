package com.qinghuan.pojo.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserAccountPageQueryDTO {
    Integer page;
    Integer pageSize;
    String keyword;
    String status;
    String roleCode;
}
