package com.openelements.data.runtime.sql;

import java.sql.Connection;
import java.sql.SQLException;

public interface QueryContext {
    Connection getConnection() throws SQLException;
}
