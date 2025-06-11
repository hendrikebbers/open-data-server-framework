package com.openelements.data.runtime.sql.postgres;

import com.openelements.data.runtime.sql.api.SqlConnection;
import com.openelements.data.runtime.sql.types.AbstractSqlTypeSupport;
import com.openelements.data.runtime.types.ByteArray;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Set;

public class PostgresByteArraySupport extends AbstractSqlTypeSupport<ByteArray, byte[]> {

    public PostgresByteArraySupport() {
        super(ByteArray.class, "BYTEA");
    }

    @Override
    public Class<byte[]> getSqlType() {
        return byte[].class;
    }

    @Override
    public int getJdbcTypeCode() {
        return Types.BINARY;
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(PostgresDialect.DRIVER_CLASS_NAME);
    }

    @Override
    public ByteArray convertToJavaValue(byte[] sqlValue, SqlConnection connection) throws SQLException {
        if (sqlValue == null) {
            return null;
        }
        try {
            return new ByteArray(sqlValue);
        } catch (Exception e) {
            throw new SQLException("Error converting Blob to ByteArray", e);
        }
    }

    @Override
    public byte[] convertToSqlValue(ByteArray javaValue, SqlConnection connection) throws SQLException {
        if (javaValue == null) {
            return null;
        }
        return javaValue.value();
    }
}
