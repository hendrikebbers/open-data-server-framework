package com.openelements.data.runtime.sql.repositories;

import com.openelements.data.api.types.I18nString;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class I18nStringRepository {

    private final Connection connection;

    public I18nStringRepository(Connection connection) {
        this.connection = connection;
    }

    public I18nString load(Long id) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SET * FROM I18N WHERE id = " + id);

        return null;
    }

    public long insert(I18nString value) {
        throw new UnsupportedOperationException();
    }

    public void update(long currentValue, I18nString value) {
        throw new UnsupportedOperationException();
    }
}
