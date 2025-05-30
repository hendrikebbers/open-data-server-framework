package com.openelements.data.runtime.sql.types;

import com.openelements.data.runtime.sql.SqlConnection;
import java.sql.SQLException;

public interface RefrenceSqlTypeSupport<T, U> extends SqlTypeSupport<T, U> {

    default boolean isReferenceType() {
        return true;
    }
    
    U insert(T javaValue, SqlConnection connection) throws SQLException;

    U update(T javaValue, SqlConnection connection) throws SQLException;

}
