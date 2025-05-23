package com.openelements.data.runtime.sql;

import java.sql.Connection;
import java.sql.SQLException;

public interface PersistenceContext {
    Connection getConnection() throws SQLException;
}
