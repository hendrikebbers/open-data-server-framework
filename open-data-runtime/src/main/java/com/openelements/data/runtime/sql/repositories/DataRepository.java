package com.openelements.data.runtime.sql.repositories;

import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.data.Page;
import com.openelements.data.runtime.sql.SqlConnection;
import java.sql.SQLException;
import java.util.List;

public interface DataRepository<E extends Record> {

    List<E> getAll() throws SQLException;

    Page<E> getPage(int pageNumber, int pageSize) throws SQLException;

    long getCount() throws SQLException;

    void store(List<E> data) throws SQLException;

    void store(E data) throws SQLException;

    static <E extends Record> DataRepository<E> of(Class<E> dataType, SqlConnection connection) {
        return of(DataType.of(dataType), connection);
    }

    static <E extends Record> DataRepository<E> of(DataType<E> dataType, SqlConnection connection) {
        return DataRepositoryImpl.of(dataType, connection);
    }
}
