package com.qinghuan.pojo.entity;

import com.qinghuan.pojo.enums.VisitorStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Visitor extends BaseEntity {

    private Long userId;
    private String name;
    private String idType;
    private String idNumber;
    private String phone;
    private VisitorStatus status;
}
