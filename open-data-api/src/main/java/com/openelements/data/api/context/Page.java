package com.openelements.data.api.context;

import java.util.List;

public interface Page<T extends Record> {

    int getPageIndex();

    int getSize();

    List<T> getData();

    boolean hasNext();

    Page<T> next();

    Page<T> first();

    boolean isFirst();

    boolean isLast();
}
