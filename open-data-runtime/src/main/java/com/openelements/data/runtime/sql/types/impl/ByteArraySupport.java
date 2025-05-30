package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.sql.api.SqlConnection;
import com.openelements.data.runtime.sql.h2.H2Dialect;
import com.openelements.data.runtime.sql.types.AbstractSqlTypeSupport;
import com.openelements.data.runtime.types.ByteArray;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Set;

public class ByteArraySupport extends AbstractSqlTypeSupport<ByteArray, Blob> {

    public ByteArraySupport() {
        super(ByteArray.class, "BINARY VARYING");
    }

    @Override
    public Class<Blob> getSqlType() {
        return Blob.class;
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
        return new Blob() {
            @Override
            public long length() throws SQLException {
                return 0;
            }

            @Override
            public byte[] getBytes(long pos, int length) throws SQLException {
                return new byte[0];
            }

            @Override
            public InputStream getBinaryStream() throws SQLException {
                return null;
            }

            @Override
            public long position(byte[] pattern, long start) throws SQLException {
                return 0;
            }

            @Override
            public long position(Blob pattern, long start) throws SQLException {
                return 0;
            }

            @Override
            public int setBytes(long pos, byte[] bytes) throws SQLException {
                return 0;
            }

            @Override
            public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
                return 0;
            }

            @Override
            public OutputStream setBinaryStream(long pos) throws SQLException {
                return null;
            }

            @Override
            public void truncate(long len) throws SQLException {

            }

            @Override
            public void free() throws SQLException {

            }

            @Override
            public InputStream getBinaryStream(long pos, long length) throws SQLException {
                return null;
            }
        };
    }
}
