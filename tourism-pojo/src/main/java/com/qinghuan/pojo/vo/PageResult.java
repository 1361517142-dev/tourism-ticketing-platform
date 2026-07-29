package com.qinghuan.pojo.vo;

import java.util.List;
import java.util.Objects;

/**
 * 通用分页响应。
 */
//items 当前页的条目， total 总条目数， page 当前页码， size 每页大小
public record PageResult<T>(List<T> items, long total, int page, int size) {

    public PageResult {
        items = List.copyOf(Objects.requireNonNull(items, "返回条目不能为空"));
        if (total < 0 || page < 1 || size < 1) {
            throw new IllegalArgumentException("不合法的分页元数据");
        }
    }

    public static <T> PageResult<T> empty(int page, int size) {
        return new PageResult<>(List.of(), 0, page, size);
    }
}
