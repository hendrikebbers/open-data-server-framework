package com.openelements.data.runtime.sql.repositories;

import com.openelements.data.api.types.I18nString;
import com.openelements.data.runtime.sql.SqlConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class I18nStringRepository {

    private final SqlConnection connection;

    public I18nStringRepository(SqlConnection connection) {
        this.connection = connection;
    }

    public I18nString load(Long id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM I18N WHERE id = " + id);
        ResultSet resultSet = statement.executeQuery();
        return null;
    }

    public long insert(I18nString value) {
        throw new UnsupportedOperationException();
    }

    public void update(long currentValue, I18nString value) {
        throw new UnsupportedOperationException();
    }
}
