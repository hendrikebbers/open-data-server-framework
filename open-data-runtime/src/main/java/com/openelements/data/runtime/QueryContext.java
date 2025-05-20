package com.openelements.data.runtime;

import java.sql.Connection;
import java.sql.SQLException;

public interface QueryContext {
    Connection getConnection() throws SQLException;
}
