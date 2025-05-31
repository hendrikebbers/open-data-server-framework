package com.openelements.data.runtime.data;

import com.openelements.data.runtime.Page;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class PageImpl<E extends Record> implements Page<E> {

    private final List<E> records;

    private final int pageNumber;

    private final int pageSize;

    private final Optional<Supplier<Page<E>>> nextPageSupplier;

    public PageImpl(List<E> records, int pageNumber, int pageSize, BiFunction<Integer, Integer, Page<E>> pageSupplier) {
        this.records = records;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.nextPageSupplier = Optional.ofNullable(() -> pageSupplier.apply(pageNumber + 1, pageSize));
    }

    public PageImpl(List<E> records, int pageNumber, int pageSize, Supplier<Page<E>> nextPageSupplier) {
        this.records = records;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.nextPageSupplier = Optional.ofNullable(nextPageSupplier);
    }

    @Override
    public List<E> getContent() {
        return records;
    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public boolean isFirst() {
        return pageNumber == 0;
    }

    @Override
    public boolean hasNext() {
        return nextPageSupplier.isPresent();
    }

    @Override
    public Page<E> nextPage() {
        return nextPageSupplier.map(Supplier::get)
                .orElseThrow(() -> new IllegalStateException("No next page available"));
    }
}
