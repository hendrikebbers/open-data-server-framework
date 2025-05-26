package com.openelements.data.runtime.sql.types;

import com.openelements.data.runtime.sql.SqlConnection;
import java.sql.SQLException;

public abstract class AbstractMatchingSqlTypeSupport<D> implements MatchingSqlTypeSupport<D> {

    private final String sqlType;

    private final Class<D> javaClass;

    protected AbstractMatchingSqlTypeSupport(Class<D> javaClass, String sqlType) {
        this.sqlType = sqlType;
        this.javaClass = javaClass;
    }

    @Override
    public String getNativeSqlType() {
        return sqlType;
    }

    @Override
    public Class<D> getJavaClass() {
        return javaClass;
    }

    @Override
    public D convertToJavaValue(D sqlValue, SqlConnection connection) {
        return sqlValue;
    }

    @Override
    public D convertToSqlValue(D javaValue, SqlConnection connection) throws SQLException {
        return javaValue;
    }
}
