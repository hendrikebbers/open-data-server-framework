package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.sql.h2.H2Dialect;
import com.openelements.data.runtime.sql.postgres.PostgresDialect;
import com.openelements.data.runtime.sql.types.AbstractMatchingSqlTypeSupport;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Set;

public class LongSupport extends AbstractMatchingSqlTypeSupport<Long> {

    public LongSupport() {
        super(Long.class, "BIGINT");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME, PostgresDialect.DRIVER_CLASS_NAME);
    }

    @Override
    public int getJdbcTypeCode() {
        return Types.BIGINT;
    }

    @Override
    public Long normalizeSqlValue(Object sqlValue) throws SQLException {
        if (sqlValue == null) {
            return null; // Handle null case
        }
        if (Integer.class.isAssignableFrom(sqlValue.getClass())) {
            return ((Long) sqlValue).longValue();
        }
        if (Integer.TYPE.isAssignableFrom(sqlValue.getClass())) {
            return ((Long) sqlValue).longValue();
        }
        return super.normalizeSqlValue(sqlValue);
    }
}
