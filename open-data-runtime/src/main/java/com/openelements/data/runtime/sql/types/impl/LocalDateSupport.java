package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.h2.H2Dialect;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.types.AbstractSqlTypeSupport;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

public class LocalDateSupport extends AbstractSqlTypeSupport<LocalDate, Date> {

    public LocalDateSupport() {
        super(LocalDate.class, "DATE");
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME);
    }

    @Override
    public LocalDate convertToJavaValue(Date sqlValue, SqlConnection connection) {
        return Optional.ofNullable(sqlValue)
                .map(Date::toLocalDate)
                .orElse(null);
    }

    @Override
    public Date convertToSqlValue(LocalDate value, SqlConnection connection) throws SQLException {
        return Optional.ofNullable(value)
                .map(Date::valueOf)
                .orElse(null);
    }

    @Override
    public Class<Date> getSqlType() {
        return Date.class;
    }
}
