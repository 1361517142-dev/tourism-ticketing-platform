package com.qinghuan.pojo.dto;

import com.qinghuan.pojo.enums.TicketStatus;
import lombok.Getter;
import lombok.Setter;

/** 游客端电子票分页筛选条件。 */
@Getter
@Setter
public class TicketPageQueryDTO extends PageQuery {

    private TicketStatus status;
}
