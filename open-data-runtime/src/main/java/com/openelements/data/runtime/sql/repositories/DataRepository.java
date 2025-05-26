package com.openelements.data.runtime.sql.repositories;

import com.openelements.data.api.context.Page;
import java.sql.SQLException;
import java.util.List;

public interface DataRepository<E extends Record> {

    List<E> getAll() throws SQLException;

    Page<E> getPage(int pageNumber, int pageSize) throws SQLException;

    long getCount() throws SQLException;

    void createTable() throws SQLException;

    void store(List<E> data) throws SQLException;

    void store(E data) throws SQLException;
}
