package com.openelements.data.runtime.api;

import java.util.List;
import org.jspecify.annotations.NonNull;

public interface Page<E extends Record> {

    @NonNull
    List<E> getContent();

    int getCount();

    int getPageNumber();

    int getPageSize();

    boolean isFirst();

    boolean hasNext();

    @NonNull
    Page<E> nextPage();
}
