package com.qinghuan.pojo.vo;

import com.qinghuan.pojo.enums.VerificationResult;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** 运营端核销记录列表项。 */
@Getter
@Setter
public class VerificationRecordVO {

    private Long id;
    private String requestNo;
    private LocalDateTime verifiedAt;
    private String ticketCode;
    private String visitorName;
    private String ticketTypeName;
    private Long verifierId;
    private String verifierName;
    private VerificationResult result;
    private String failureReason;
    private String deviceNo;
}
