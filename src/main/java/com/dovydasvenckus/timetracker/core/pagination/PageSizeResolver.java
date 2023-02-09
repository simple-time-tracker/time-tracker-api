package com.dovydasvenckus.timetracker.core.pagination;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PageSizeResolver {
    private final int maxPageSize;

    public PageSizeResolver(@Value("${pagination.defaultSize:20}") int defaultMaxPageSize) {
        this.maxPageSize = defaultMaxPageSize;
    }

    public int resolvePageSize(Integer pageSize) {
        if (pageSize > maxPageSize || pageSize < 1) {
            return maxPageSize;
        }
        return pageSize;
    }
}
