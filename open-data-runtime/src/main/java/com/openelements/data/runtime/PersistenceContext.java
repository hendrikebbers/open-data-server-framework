package com.openelements.data.runtime;

import java.sql.Connection;
import java.sql.SQLException;

public interface PersistenceContext {
    Connection getConnection() throws SQLException;
}
