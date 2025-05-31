package com.openelements.data.runtime.sql.repositories;

import com.openelements.data.runtime.Page;
import com.openelements.data.runtime.data.DataRepository;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.tables.ResultRow;
import com.openelements.data.runtime.sql.tables.View;
import java.sql.SQLException;
import java.util.List;

public class ViewRepository<E extends Record> implements DataRepository<E> {

    private final View view;

    private final SqlConnection connection;


    public ViewRepository(View view, SqlConnection connection) {
        this.view = view;
        this.connection = connection;
    }

    @Override
    public List<E> getAll() throws SQLException {
        return connection.runInTransaction(() -> {
            final List<ResultRow> resultRows = view.getAll();
            return null;
        });
    }

    @Override
    public Page<E> getPage(int pageNumber, int pageSize) throws SQLException {
        throw new UnsupportedOperationException("Pagination in a view is not supported.");
    }

    @Override
    public long getCount() throws SQLException {
        return connection.runInTransaction(() -> view.getCount());
    }

    @Override
    public void store(List<E> data) throws SQLException {
        throw new UnsupportedOperationException("Storing data in a view is not supported.");
    }

    @Override
    public void store(E data) throws SQLException {
        throw new UnsupportedOperationException("Storing data in a view is not supported.");
    }
}
