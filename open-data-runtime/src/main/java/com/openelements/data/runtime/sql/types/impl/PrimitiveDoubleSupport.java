package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.sql.h2.H2Dialect;
import com.openelements.data.runtime.sql.postgres.PostgresDialect;
import com.openelements.data.runtime.sql.types.AbstractMatchingSqlTypeSupport;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Set;

public class PrimitiveDoubleSupport extends AbstractMatchingSqlTypeSupport<Double> {

    public PrimitiveDoubleSupport() {
        super(Double.TYPE, "DOUBLE PRECISION");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME, PostgresDialect.DRIVER_CLASS_NAME);
    }

    @Override
    public int getJdbcTypeCode() {
        return Types.DOUBLE;
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
