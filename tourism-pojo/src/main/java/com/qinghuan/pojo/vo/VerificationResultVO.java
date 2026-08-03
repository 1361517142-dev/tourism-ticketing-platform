package com.qinghuan.pojo.vo;

import com.qinghuan.pojo.enums.VerificationResult;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** 单次核销请求的业务结果。 */
@Getter
@Setter
public class VerificationResultVO {

    private String requestNo;
    private VerificationResult result;
    private String failureReason;
    private LocalDateTime verifiedAt;
    private VerificationTicketVO ticket;
}
