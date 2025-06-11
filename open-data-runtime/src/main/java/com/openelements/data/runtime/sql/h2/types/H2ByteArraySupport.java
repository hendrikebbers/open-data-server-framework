package com.openelements.data.runtime.sql.h2.types;

import com.openelements.data.runtime.sql.api.SqlConnection;
import com.openelements.data.runtime.sql.h2.H2Dialect;
import com.openelements.data.runtime.sql.types.AbstractSqlTypeSupport;
import com.openelements.data.runtime.types.ByteArray;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Set;

public class H2ByteArraySupport extends AbstractSqlTypeSupport<ByteArray, Blob> {

    public H2ByteArraySupport() {
        super(ByteArray.class, "BINARY VARYING");
    }

    @Override
    public Class<Blob> getSqlType() {
        return Blob.class;
    }

    @Override
    public int getJdbcTypeCode() {
        return Types.BINARY;
    }

    @Override
    public Set<String> getSupportedJdbcDrivers() {
        return Set.of(H2Dialect.DRIVER_CLASS_NAME);
    }

    @Override
    public ByteArray convertToJavaValue(Blob sqlValue, SqlConnection connection) throws SQLException {
        if (sqlValue == null) {
            return null;
        }
        try {
            final byte[] bytes = sqlValue.getBinaryStream().readAllBytes();
            return new ByteArray(bytes);
        } catch (Exception e) {
            throw new SQLException("Error converting Blob to ByteArray", e);
        }
    }

    @Override
    public Blob convertToSqlValue(ByteArray javaValue, SqlConnection connection) throws SQLException {
        final Blob blob = connection.createBlob();
        blob.setBytes(1, javaValue.value());
        return blob;
    }
}
