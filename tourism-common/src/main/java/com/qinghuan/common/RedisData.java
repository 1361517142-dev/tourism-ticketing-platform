package com.qinghuan.common;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
public class RedisData<T> {
    private T data;
    private LocalDateTime expireAt;
}
