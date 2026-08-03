package com.qinghuan.pojo.vo;

import com.qinghuan.pojo.entity.Visitor;
import com.qinghuan.pojo.enums.VisitorStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 参观人接口响应，证件号码只提供脱敏值。
 */
@Getter
@Setter
public class VisitorVO {

    private Long id;
    private String name;
    private String idType;
    private String maskedIdNumber;
    private String phone;
    private VisitorStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static VisitorVO from(Visitor visitor) {
        VisitorVO vo = new VisitorVO();
        vo.setId(visitor.getId());
        vo.setName(visitor.getName());
        vo.setIdType(visitor.getIdType());
        vo.setMaskedIdNumber(maskIdNumber(visitor.getIdNumber()));
        vo.setPhone(visitor.getPhone());
        vo.setStatus(visitor.getStatus());
        vo.setCreatedAt(visitor.getCreatedAt());
        vo.setUpdatedAt(visitor.getUpdatedAt());
        return vo;
    }

    /**
     * 保留证件号前三位和后四位，避免接口泄露完整身份信息。
     */
    private static String maskIdNumber(String idNumber) {
        int visibleLength = 7;
        if (idNumber.length() <= visibleLength) {
            return "*".repeat(idNumber.length());
        }
        return idNumber.substring(0, 3)
                + "*".repeat(idNumber.length() - visibleLength)
                + idNumber.substring(idNumber.length() - 4);
    }
}

