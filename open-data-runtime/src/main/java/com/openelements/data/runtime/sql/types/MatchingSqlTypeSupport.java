package com.openelements.data.runtime.sql.types;

import com.openelements.data.runtime.sql.api.SqlConnection;
import java.sql.SQLException;

public interface MatchingSqlTypeSupport<D> extends SqlTypeSupport<D, D> {

    @Override
    default D convertToJavaValue(D sqlValue, SqlConnection connection) {
        return sqlValue;
    }

    @Override
    default D convertToSqlValue(D value, SqlConnection connection) throws SQLException {
        return value;
    }

    @Override
    default Class<D> getSqlType() {
        if (getJavaType() instanceof Class<?> javaClass) {
            return (Class<D>) javaClass;
        }
        throw new IllegalStateException("Java Type must be a Class type for MatchingSqlTypeSupport");
    }
}
