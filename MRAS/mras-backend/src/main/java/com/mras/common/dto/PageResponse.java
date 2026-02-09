package com.mras.common.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public class PageResponse<T> {
    private List<T> items;

    private int page;
    private int size;

    private long totalElements;
    private int totalPages;

    private boolean hasNext;
    private boolean hasPrevious;

    public static <T> PageResponse<T> from(Page<?> pageObj, List<T> items) {
        PageResponse<T> r = new PageResponse<>();
        r.items = items;
        r.page = pageObj.getNumber();
        r.size = pageObj.getSize();
        r.totalElements = pageObj.getTotalElements();
        r.totalPages = pageObj.getTotalPages();
        r.hasNext = pageObj.hasNext();
        r.hasPrevious = pageObj.hasPrevious();
        return r;
    }

    public List<T> getItems() { return items; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public boolean isHasNext() { return hasNext; }
    public boolean isHasPrevious() { return hasPrevious; }
}
