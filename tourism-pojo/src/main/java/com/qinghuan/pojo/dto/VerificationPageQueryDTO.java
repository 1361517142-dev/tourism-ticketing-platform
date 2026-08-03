package com.qinghuan.pojo.dto;

import com.qinghuan.pojo.enums.VerificationResult;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/** 运营端核销记录分页筛选条件。 */
@Getter
@Setter
public class VerificationPageQueryDTO extends PageQuery {

    @Size(max = 64, message = "票码不能超过 64 个字符")
    private String ticketCode;
    private VerificationResult result;
    private LocalDate verificationDate;
}
