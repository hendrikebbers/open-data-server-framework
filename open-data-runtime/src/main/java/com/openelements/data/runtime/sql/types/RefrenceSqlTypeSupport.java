package com.openelements.data.runtime.sql.types;

import com.openelements.data.runtime.sql.connection.SqlConnectionImpl;
import java.sql.SQLException;

public interface RefrenceSqlTypeSupport<T, U> extends SqlTypeSupport<T, U> {

    default boolean isReferenceType() {
        return true;
    }

    U insert(T javaValue, SqlConnectionImpl connection) throws SQLException;

    U update(T javaValue, SqlConnectionImpl connection) throws SQLException;

}
