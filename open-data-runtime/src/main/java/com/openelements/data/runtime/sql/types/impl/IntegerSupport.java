package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.sql.h2.H2Dialect;
import com.openelements.data.runtime.sql.postgres.PostgresDialect;
import com.openelements.data.runtime.sql.types.AbstractMatchingSqlTypeSupport;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Set;

public class IntegerSupport extends AbstractMatchingSqlTypeSupport<Integer> {

    public IntegerSupport() {
        super(Integer.class, "INTEGER");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME, PostgresDialect.DRIVER_CLASS_NAME);
    }

    @Override
    public int getJdbcTypeCode() {
        return Types.INTEGER;
    }

    @Override
    public Integer normalizeSqlValue(Object sqlValue) throws SQLException {
        if (sqlValue == null) {
            return null; // Handle null case
        }
        if (Integer.class.isAssignableFrom(sqlValue.getClass())) {
            return ((Integer) sqlValue).intValue();
        }
        if (Integer.TYPE.isAssignableFrom(sqlValue.getClass())) {
            return ((Integer) sqlValue).intValue();
        }
        return super.normalizeSqlValue(sqlValue);
    }
}
