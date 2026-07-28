package com.qinghuan.pojo.vo;

import java.util.List;
import java.util.Objects;

/**
 * 通用分页响应。
 */
public record PageResult<T>(List<T> items, long total, int page, int size) {

    public PageResult {
        items = List.copyOf(Objects.requireNonNull(items, "items must not be null"));
        if (total < 0 || page < 1 || size < 1) {
            throw new IllegalArgumentException("invalid pagination metadata");
        }
    }

    public static <T> PageResult<T> empty(int page, int size) {
        return new PageResult<>(List.of(), 0, page, size);
    }
}
