package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.h2.H2Dialect;
import com.openelements.data.runtime.sql.types.AbstractMatchingSqlTypeSupport;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Set;

public class ZonedDateTimeSupport extends AbstractMatchingSqlTypeSupport<ZonedDateTime> {

    public ZonedDateTimeSupport() {
        super(ZonedDateTime.class, "TIMESTAMP WITH TIME ZONE");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME);
    }

    @Override
    public ZonedDateTime normalizeSqlValue(Object sqlValue) throws SQLException {
        if (sqlValue == null) {
            return null; // Handle null case
        }
        if (sqlValue instanceof OffsetDateTime offsetDateTime) {
            return offsetDateTime.toZonedDateTime();
        }
        if (!getSqlType().isInstance(sqlValue)) {
            throw new SQLException(
                    "Expected SQL value of type " + getSqlType().getName() + ", but got: " + sqlValue.getClass()
                            .getName());
        }
        return getSqlType().cast(sqlValue);
    }
}
