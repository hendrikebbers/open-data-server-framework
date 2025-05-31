package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.h2.H2Dialect;
import com.openelements.data.runtime.sql.types.AbstractMatchingSqlTypeSupport;
import java.sql.SQLException;
import java.util.Set;

public class PrimitiveLongSupport extends AbstractMatchingSqlTypeSupport<Long> {

    public PrimitiveLongSupport() {
        super(Long.TYPE, "BIGINT");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME);
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
