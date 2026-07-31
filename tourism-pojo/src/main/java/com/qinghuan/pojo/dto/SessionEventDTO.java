package com.qinghuan.pojo.dto;

import com.qinghuan.pojo.enums.SessionEvent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 触发场次状态变化的业务事件。
 */
@Getter
@Setter
public class SessionEventDTO {

    @NotNull(message = "场次事件不能为空")
    private SessionEvent event;
}
