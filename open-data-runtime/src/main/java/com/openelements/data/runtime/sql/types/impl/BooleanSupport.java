package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.h2.H2Dialect;
import com.openelements.data.runtime.sql.types.AbstractMatchingSqlTypeSupport;
import java.sql.SQLException;
import java.util.Set;

public class BooleanSupport extends AbstractMatchingSqlTypeSupport<Boolean> {

    public BooleanSupport() {
        super(Boolean.class, "BOOLEAN");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME);
    }

    @Override
    public Boolean normalizeSqlValue(Object sqlValue) throws SQLException {
        if (sqlValue == null) {
            return null; // Handle null case
        }
        if (Boolean.class.isAssignableFrom(sqlValue.getClass())) {
            return ((Boolean) sqlValue).booleanValue();
        }
        if (Boolean.TYPE.isAssignableFrom(sqlValue.getClass())) {
            return ((Boolean) sqlValue).booleanValue();
        }
        return super.normalizeSqlValue(sqlValue);
    }

}
