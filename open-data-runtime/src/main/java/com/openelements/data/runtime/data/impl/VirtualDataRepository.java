package com.openelements.data.runtime.data.impl;

import com.openelements.data.runtime.api.Page;
import com.openelements.data.runtime.data.DataAttribute;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.integration.DataRepository;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.jspecify.annotations.NonNull;

public class VirtualDataRepository<E extends Record> implements DataRepository<E> {

    private final List<E> dataList;

    public VirtualDataRepository() {
        this.dataList = new CopyOnWriteArrayList<>();
    }

    @Override
    public List<E> getAll() {
        return Collections.unmodifiableList(dataList);
    }

    @Override
    public Page<E> getPage(int pageNumber, int pageSize) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number must be non-negative");
        }
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero");
        }
        final int startIndex = pageSize * pageNumber;
        final int endIndex = Math.min(startIndex + pageSize, dataList.size());
        final List<E> pageData = Collections.unmodifiableList(dataList.subList(startIndex, endIndex));
        final Supplier<Page<E>> nextPageSupplier;
        if (endIndex < dataList.size()) {
            nextPageSupplier = () -> getPage(pageNumber + 1, pageSize);
        } else {
            nextPageSupplier = null; // No next page
        }
        return new PageImpl<>(pageData, pageNumber, pageSize, nextPageSupplier);
    }

    @Override
    public long getCount() {
        return dataList.size();
    }

    @Override
    public void store(@NonNull final List<E> data) throws SQLException {
        Objects.requireNonNull(data, "Data list cannot be null");
        data.forEach(d -> {
            try {
                store(d);
            } catch (SQLException e) {
                throw new RuntimeException("Error storing data", e);
            }
        });
    }

    @Override
    public void store(@NonNull E data) throws SQLException {
        Objects.requireNonNull(data, "Data cannot be null");
        final Predicate<E> check = DataType.of((Class<E>) data.getClass()).attributes().stream()
                .filter(attr -> attr.partOfIdentifier())
                .map(attr -> createCheckFunction(attr))
                .map(biCheck -> createCheckFunction(biCheck, data))
                .reduce(Predicate::and)
                .orElse(value -> true);
        for (E existingData : this.dataList) {
            if (check.test(existingData)) {
                this.dataList.replaceAll(e -> {
                    if (existingData.equals(e)) {
                        return data;
                    }
                    return e;
                });
                return; // Data already exists, so we replace it
            }
        }
        dataList.add(data);
    }

    @NonNull
    private Predicate<E> createCheckFunction(@NonNull final BiPredicate<E, E> check, @NonNull final E data) {
        Objects.requireNonNull(check, "Check function cannot be null");
        Objects.requireNonNull(data, "Data cannot be null");
        return value -> check.test(value, data);
    }

    @NonNull
    private BiPredicate<E, E> createCheckFunction(@NonNull final DataAttribute<E, ?> attribute) {
        Objects.requireNonNull(attribute, "Attribute cannot be null");
        return (dataA, dataB) -> {
            Object valueA = DataAttribute.getFor(dataA, attribute);
            Object valueB = DataAttribute.getFor(dataB, attribute);
            return Objects.equals(valueA, valueB);
        };
    }
}
