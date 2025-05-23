package com.openelements.data.api.context;

import java.util.List;

public interface Page<E extends Record> {

    List<E> getContent();

    int getCount();

    int getPageNumber();

    int getPageSize();

    boolean isFirst();

    boolean hasNext();

    Page<E> nextPage();
}
