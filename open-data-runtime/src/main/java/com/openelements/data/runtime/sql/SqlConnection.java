package com.openelements.data.runtime.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface SqlConnection {

    PreparedStatement prepareStatement(String sql) throws SQLException;
}
