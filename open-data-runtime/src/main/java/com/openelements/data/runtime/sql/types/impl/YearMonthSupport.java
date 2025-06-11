package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.sql.api.SqlConnection;
import com.openelements.data.runtime.sql.h2.H2Dialect;
import com.openelements.data.runtime.sql.postgres.PostgresDialect;
import com.openelements.data.runtime.sql.types.AbstractSqlTypeSupport;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.Set;

public class YearMonthSupport extends AbstractSqlTypeSupport<YearMonth, LocalDate> {

    public YearMonthSupport() {
        super(YearMonth.class, "DATE");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME, PostgresDialect.DRIVER_CLASS_NAME);
    }

    @Override
    public YearMonth convertToJavaValue(LocalDate sqlValue, SqlConnection connection) throws SQLException {
        if (sqlValue == null) {
            return null; // Handle null case
        }
        return YearMonth.of(sqlValue.getYear(), sqlValue.getMonthValue());
    }

    @Override
    public LocalDate convertToSqlValue(YearMonth javaValue, SqlConnection connection) throws SQLException {
        if (javaValue == null) {
            return null; // Handle null case
        }
        return LocalDate.of(javaValue.getYear(), javaValue.getMonthValue(), 1);
    }

    @Override
    public Class<LocalDate> getSqlType() {
        return LocalDate.class;
    }

    @Override
    public int getJdbcTypeCode() {
        return Types.DATE;
    }

    @Override
    public LocalDate normalizeSqlValue(Object sqlValue) throws SQLException {
        if (sqlValue == null) {
            return null; // Handle null case
        }
        if (sqlValue instanceof OffsetDateTime offsetDateTime) {
            return LocalDate.of(offsetDateTime.getYear(), offsetDateTime.getMonthValue(),
                    offsetDateTime.getDayOfMonth());
        }
        if (sqlValue instanceof Date date) {
            return date.toLocalDate();
        }
        return super.normalizeSqlValue(sqlValue);
    }
}
