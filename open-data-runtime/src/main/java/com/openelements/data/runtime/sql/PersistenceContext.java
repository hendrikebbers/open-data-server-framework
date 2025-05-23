package com.openelements.data.runtime.sql;

import java.sql.SQLException;

public interface PersistenceContext {
    SqlConnection getConnection() throws SQLException;
}
