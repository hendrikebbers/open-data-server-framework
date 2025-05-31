package com.openelements.data.runtime.data.impl;

import com.openelements.data.runtime.api.Page;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class PageImpl<E extends Record> implements Page<E> {

    private final List<E> records;

    private final int pageNumber;

    private final int pageSize;

    private final Optional<Supplier<Page<E>>> nextPageSupplier;

    public PageImpl(@NonNull final List<E> records, final int pageNumber, final int pageSize,
            @Nullable final BiFunction<Integer, Integer, Page<E>> pageSupplier) {
        Objects.requireNonNull(records, "records must not be null");
        this.records = Collections.unmodifiableList(records);
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.nextPageSupplier = Optional.ofNullable(() -> pageSupplier.apply(pageNumber + 1, pageSize));
    }

    public PageImpl(@NonNull final List<E> records, final int pageNumber, final int pageSize,
            @Nullable final Supplier<Page<E>> nextPageSupplier) {
        Objects.requireNonNull(records, "records must not be null");
        this.records = Collections.unmodifiableList(records);
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
