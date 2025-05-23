package com.openelements.data.runtime.sql;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public interface DataRepository<E extends Record> {

    List<E> getAll()
            throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

    Page<E> getPage(int pageNumber, int pageSize)
            throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

    long getCount() throws SQLException;

    void createTable() throws SQLException;
}
