package com.openelements.data.runtime.sql;

import java.sql.SQLException;

public interface QueryContext {
    SqlConnection getConnection() throws SQLException;
}
