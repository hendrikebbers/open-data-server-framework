package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.sql.api.SqlConnection;
import com.openelements.data.runtime.sql.h2.H2Dialect;
import com.openelements.data.runtime.sql.postgres.PostgresDialect;
import com.openelements.data.runtime.sql.types.AbstractSqlTypeSupport;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

public class YearMonthSupport extends AbstractSqlTypeSupport<YearMonth, Timestamp> {

    public YearMonthSupport() {
        super(YearMonth.class, "TIMESTAMP WITH TIME ZONE");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME, PostgresDialect.DRIVER_CLASS_NAME);
    }

    @Override
    public YearMonth convertToJavaValue(Timestamp sqlValue, SqlConnection connection) throws SQLException {
        if (sqlValue == null) {
            return null; // Handle null case
        }
        return YearMonth.from(sqlValue.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate());
    }

    @Override
    public Timestamp convertToSqlValue(YearMonth javaValue, SqlConnection connection) throws SQLException {
        if (javaValue == null) {
            return null; // Handle null case
        }
        final Date date = Date.from(javaValue.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return new Timestamp(date.getTime());
    }

    @Override
    public Class<Timestamp> getSqlType() {
        return Timestamp.class;
    }

    @Override
    public int getJdbcTypeCode() {
        return Types.TIMESTAMP_WITH_TIMEZONE;
    }

    @Override
    public Timestamp normalizeSqlValue(Object sqlValue) throws SQLException {
        if (sqlValue == null) {
            return null; // Handle null case
        }
        if (sqlValue instanceof OffsetDateTime offsetDateTime) {
            // Convert OffsetTime to Timestamp
            final Date date = Date.from(offsetDateTime.toInstant());
            return new Timestamp(date.getTime());
        }
        return super.normalizeSqlValue(sqlValue);
    }
}
