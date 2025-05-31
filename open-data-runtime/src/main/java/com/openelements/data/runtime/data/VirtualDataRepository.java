package com.openelements.data.runtime.data;

import com.openelements.data.runtime.Page;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
    public void store(List<E> data) throws SQLException {
        data.forEach(d -> {
            try {
                store(d);
            } catch (SQLException e) {
                throw new RuntimeException("Error storing data", e);
            }
        });
    }

    @Override
    public void store(E data) throws SQLException {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
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

    private Predicate<E> createCheckFunction(BiPredicate<E, E> check, E data) {
        return value -> check.test(value, data);
    }

    private BiPredicate<E, E> createCheckFunction(DataAttribute<E, ?> attribute) {
        return (dataA, dataB) -> {
            Object valueA = DataAttribute.getFor(dataA, attribute);
            Object valueB = DataAttribute.getFor(dataB, attribute);
            return Objects.equals(valueA, valueB);
        };
    }
}
