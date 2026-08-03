package com.qinghuan.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** 单次票券核销请求。 */
public record VerificationRequestDTO(
        @NotBlank(message = "核销请求号不能为空")
        @Size(max = 64, message = "核销请求号不能超过 64 个字符")
        String requestNo,

        @NotBlank(message = "票码不能为空")
        @Size(max = 64, message = "票码不能超过 64 个字符")
        String ticketCode,

        @Size(max = 64, message = "设备编号不能超过 64 个字符")
        String deviceNo
) {
}
