package com.qinghuan.pojo.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/** 订单详情中的参观人、票种成交快照及对应电子票。 */
@Getter
@Setter
public class OrderItemVO {

    private Long id;
    private Long visitorId;
    private String visitorName;
    private String visitorIdType;
    private String maskedVisitorIdNumber;
    private Long sessionTicketTypeId;
    private String ticketTypeName;
    private BigDecimal unitPrice;
    private TicketSummaryVO ticket;

    /**
     * MyBatis 读取证件号快照时立即转换为脱敏值。
     * 类中不保存原始证件号，避免后续序列化时意外泄露。
     */
    public void setVisitorIdNumber(String visitorIdNumber) {
        int visibleLength = 7;
        if (visitorIdNumber.length() <= visibleLength) {
            this.maskedVisitorIdNumber = "*".repeat(visitorIdNumber.length());
            return;
        }
        this.maskedVisitorIdNumber = visitorIdNumber.substring(0, 3)
                + "*".repeat(visitorIdNumber.length() - visibleLength)
                + visitorIdNumber.substring(visitorIdNumber.length() - 4);
    }
}
