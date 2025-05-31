package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.h2.H2Dialect;
import com.openelements.data.runtime.sql.types.AbstractMatchingSqlTypeSupport;
import java.sql.SQLException;
import java.util.Set;

public class DoubleSupport extends AbstractMatchingSqlTypeSupport<Double> {

    public DoubleSupport() {
        super(Double.class, "DOUBLE PRECISION");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME);
    }

    @Override
    public Double normalizeSqlValue(Object sqlValue) throws SQLException {
        if (sqlValue == null) {
            return null; // Handle null case
        }
        if (Double.class.isAssignableFrom(sqlValue.getClass())) {
            return ((Double) sqlValue).doubleValue();
        }
        if (Double.TYPE.isAssignableFrom(sqlValue.getClass())) {
            return ((Double) sqlValue).doubleValue();
        }
        return super.normalizeSqlValue(sqlValue);
    }
}
