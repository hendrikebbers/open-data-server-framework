package com.openelements.data.runtime.sql.types.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.openelements.data.runtime.sql.api.SqlConnection;
import com.openelements.data.runtime.sql.h2.H2Dialect;
import com.openelements.data.runtime.sql.postgres.PostgresDialect;
import com.openelements.data.runtime.sql.types.SqlTypeSupport;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class StringSetSupport implements SqlTypeSupport<Set<String>, String> {

    private static final Set<String> FIELD_FOR_TYPE = null;

    private final String nativeSqlType;

    private final Type javaType;

    private final Class<String> sqlType;

    private final Set<String> supportedJdbcDrivers;

    public StringSetSupport() {
        try {
            javaType = StringSetSupport.class.getDeclaredField("FIELD_FOR_TYPE").getGenericType();
            sqlType = String.class;
            nativeSqlType = "VARCHAR";
            supportedJdbcDrivers = Set.of(H2Dialect.DRIVER_CLASS_NAME, PostgresDialect.DRIVER_CLASS_NAME);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to get field type for StringSetSupport", e);
        }
    }

    @Override
    public boolean supportsJavaType(Type type) {
        return Objects.equals(javaType, type);
    }

    @Override
    public Type getJavaType() {
        return javaType;
    }

    @Override
    public String getNativeSqlType() {
        return nativeSqlType;
    }

    @Override
    public Class<String> getSqlType() {
        return sqlType;
    }

    @Override
    public int getJdbcTypeCode() {
        return Types.VARCHAR;
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return supportedJdbcDrivers;
    }

    @Override
    public Set<String> convertToJavaValue(String sqlValue, SqlConnection connection) throws SQLException {
        Set<String> resultSet = new HashSet<>();
        JsonParser.parseString(sqlValue).getAsJsonArray().forEach(element -> {
            resultSet.add(element.getAsString());
        });
        return Collections.unmodifiableSet(resultSet);
    }

    @Override
    public String convertToSqlValue(Set<String> javaValue, SqlConnection connection) throws SQLException {
        JsonArray jsonArray = new JsonArray();
        javaValue.stream()
                .map(value -> new JsonPrimitive(value))
                .forEach(jsonArray::add);
        return jsonArray.toString();
    }
}
