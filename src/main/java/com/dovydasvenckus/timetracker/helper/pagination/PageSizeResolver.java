package com.dovydasvenckus.timetracker.helper.pagination;

import org.springframework.stereotype.Component;

@Component
public class PageSizeResolver {
    private static final int MAX_PAGE_SIZE = 20;

    public int resolvePageSize(Integer pageSize) {
        if (pageSize > MAX_PAGE_SIZE || pageSize < 1) {
            return MAX_PAGE_SIZE;
        }
        return pageSize;
    }
}
