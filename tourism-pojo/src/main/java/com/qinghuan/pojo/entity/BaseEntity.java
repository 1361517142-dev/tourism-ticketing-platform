package com.qinghuan.pojo.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 带主键和审计时间的数据库实体基类。
 */
@Getter
@Setter
public abstract class BaseEntity {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
