package com.otakucenter.model;

import java.util.Collections;
import java.util.List;

public class PageResult<T> {

    private final List<T> items;
    private final int page;
    private final int pageSize;
    private final int totalItems;
    private final int totalPages;

    public PageResult(List<T> items, int page, int pageSize, int totalItems) {
        this.items = items;
        this.page = page;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.totalPages = totalItems == 0 ? 1 : (int) Math.ceil((double) totalItems / (double) pageSize);
    }

    public List<T> getItems() {
        return items;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isHasPrevious() {
        return page > 1;
    }

    public boolean isHasNext() {
        return page < totalPages;
    }

    public int getPreviousPage() {
        return isHasPrevious() ? page - 1 : 1;
    }

    public int getNextPage() {
        return isHasNext() ? page + 1 : totalPages;
    }

    public int getStartItem() {
        if (totalItems == 0) {
            return 0;
        }
        return (page - 1) * pageSize + 1;
    }

    public int getEndItem() {
        if (totalItems == 0) {
            return 0;
        }
        return Math.min(page * pageSize, totalItems);
    }

    public static <T> PageResult<T> fromList(List<T> source, int requestedPage, int pageSize) {
        if (source == null || source.isEmpty()) {
            return new PageResult<T>(Collections.<T>emptyList(), 1, pageSize, 0);
        }

        int totalItems = source.size();
        int totalPages = (int) Math.ceil((double) totalItems / (double) pageSize);
        int normalizedPage = requestedPage < 1 ? 1 : Math.min(requestedPage, totalPages);
        int fromIndex = (normalizedPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalItems);

        return new PageResult<T>(source.subList(fromIndex, toIndex), normalizedPage, pageSize, totalItems);
    }

    public static <T> PageResult<T> fromPage(List<T> items, int requestedPage, int pageSize, int totalItems) {
        if (totalItems <= 0) {
            return new PageResult<T>(Collections.<T>emptyList(), 1, pageSize, 0);
        }

        int totalPages = (int) Math.ceil((double) totalItems / (double) pageSize);
        int normalizedPage = requestedPage < 1 ? 1 : Math.min(requestedPage, totalPages);
        return new PageResult<T>(items, normalizedPage, pageSize, totalItems);
    }
}
