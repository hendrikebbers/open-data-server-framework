package com.openelements.data.runtime.sql.types.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.openelements.data.runtime.h2.H2Dialect;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.types.AbstractSqlTypeSupport;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Set;

public class EnumSupport extends AbstractSqlTypeSupport<Enum, String> {

    public EnumSupport() {
        super(Enum.class, "VARCHAR");
    }

    @Override
    public Class<String> getSqlType() {
        return String.class;
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME);
    }

    @Override
    public boolean supportsJavaType(Type type) {
        if (type instanceof Class<?> clazz) {
            return Enum.class.isAssignableFrom(clazz);
        }
        return super.supportsJavaType(type);
    }

    @Override
    public Enum convertToJavaValue(String sqlValue, SqlConnection connection) throws SQLException {
        if (sqlValue == null) {
            return null;
        }
        final JsonObject jsonObject = JsonParser.parseString(sqlValue).getAsJsonObject();
        if (!jsonObject.has("enumType") || !jsonObject.has("enumValue")) {
            throw new SQLException("Invalid enum JSON format: " + sqlValue);
        }
        String enumType = jsonObject.get("enumType").getAsString();
        String enumValue = jsonObject.get("enumValue").getAsString();
        try {
            Class<? extends Enum> enumClass = (Class<? extends Enum>) Class.forName(enumType);
            return Enum.valueOf(enumClass, enumValue);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Enum class not found: " + enumType, e);
        }
    }

    @Override
    public String convertToSqlValue(Enum javaValue, SqlConnection connection) throws SQLException {
        if (javaValue == null) {
            return null; // Handle null case
        }
        JsonObject json = new JsonObject();
        json.addProperty("enumType", javaValue.getClass().getName());
        json.addProperty("enumValue", javaValue.name());
        return json.toString();
    }
}
