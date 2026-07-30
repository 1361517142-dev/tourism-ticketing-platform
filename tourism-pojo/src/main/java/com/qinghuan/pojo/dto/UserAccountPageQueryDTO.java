package com.qinghuan.pojo.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserAccountPageQueryDTO {
    private Integer page = 1;
    private Integer pageSize = 20;
    private String keyword;
    private String status;
    private String roleCode;
    private Long venueId;
}
