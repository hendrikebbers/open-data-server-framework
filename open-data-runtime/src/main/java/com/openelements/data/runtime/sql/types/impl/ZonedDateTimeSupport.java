package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.sql.api.SqlConnection;
import com.openelements.data.runtime.sql.h2.H2Dialect;
import com.openelements.data.runtime.sql.postgres.PostgresDialect;
import com.openelements.data.runtime.sql.types.AbstractSqlTypeSupport;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Set;

public class ZonedDateTimeSupport extends AbstractSqlTypeSupport<ZonedDateTime, OffsetDateTime> {

    public ZonedDateTimeSupport() {
        super(ZonedDateTime.class, "TIMESTAMP WITH TIME ZONE");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME, PostgresDialect.DRIVER_CLASS_NAME);
    }

    @Override
    public ZonedDateTime convertToJavaValue(OffsetDateTime sqlValue, SqlConnection connection) throws SQLException {
        if (sqlValue != null) {
            return sqlValue.toZonedDateTime();
        }
        return null;
    }

    @Override
    public OffsetDateTime convertToSqlValue(ZonedDateTime javaValue, SqlConnection connection) throws SQLException {
        if (javaValue != null) {
            return javaValue.toOffsetDateTime();
        }
        return null;
    }

    @Override
    public Class<OffsetDateTime> getSqlType() {
        return OffsetDateTime.class;
    }

    @Override
    public int getJdbcTypeCode() {
        return Types.TIMESTAMP_WITH_TIMEZONE;
    }

    @Override
    public OffsetDateTime normalizeSqlValue(Object sqlValue) throws SQLException {
        if (sqlValue == null) {
            return null; // Handle null case
        }
        if (sqlValue instanceof Timestamp timestamp) {
            return OffsetDateTime.ofInstant(timestamp.toInstant(), java.time.ZoneOffset.UTC);
        }
        if (sqlValue instanceof OffsetDateTime offsetDateTime) {
            return offsetDateTime;
        }
        if (!getSqlType().isInstance(sqlValue)) {
            throw new SQLException(
                    "Expected SQL value of type " + getSqlType().getName() + ", but got: " + sqlValue.getClass()
                            .getName());
        }
        return getSqlType().cast(sqlValue);
    }
}
