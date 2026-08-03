package com.qinghuan.pojo.dto;

import com.qinghuan.pojo.enums.VisitorStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 启用或停用参观人的请求参数。
 */
@Getter
@Setter
public class VisitorStatusUpdateDTO {

    @NotNull(message = "参观人状态不能为空")
    private VisitorStatus status;
}

