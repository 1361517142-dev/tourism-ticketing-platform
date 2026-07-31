package com.qinghuan.pojo.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class TicketTypePageQueryDTO {
    private int page = 1;
    private int pageSize = 10;
    private String keyword;
}
