package com.qinghuan.pojo.entity;

import com.qinghuan.pojo.enums.VerificationResult;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VerificationRecord {

    private Long id;
    private String requestNo;
    private Long ticketId;
    private Long verifierId;
    private VerificationResult result;
    private String failureReason;
    private String deviceNo;
    private LocalDateTime verifiedAt;
}
