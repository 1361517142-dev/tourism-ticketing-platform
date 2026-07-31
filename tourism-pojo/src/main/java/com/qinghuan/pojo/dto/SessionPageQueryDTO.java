package com.qinghuan.pojo.dto;

import com.qinghuan.pojo.enums.AdmissionSessionStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 运营端场次分页筛选条件。
 */
@Getter
@Setter
public class SessionPageQueryDTO extends PageQuery {

    private LocalDate visitDate;
    private AdmissionSessionStatus status;
}
