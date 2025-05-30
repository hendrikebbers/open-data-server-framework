package com.openelements.data.runtime.sql.types.impl;

import com.openelements.data.runtime.h2.H2Dialect;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.types.AbstractSqlTypeSupport;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Set;

public class URISupport extends AbstractSqlTypeSupport<URI, String> {

    public URISupport() {
        super(URI.class, "VARCHAR");
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
    public URI convertToJavaValue(String sqlValue, SqlConnection connection) throws SQLException {
        if (sqlValue == null) {
            return null;
        }
        try {
            return new URI(sqlValue);
        } catch (URISyntaxException e) {
            throw new SQLException("Can not convert to URI", e);
        }
    }

    @Override
    public String convertToSqlValue(URI javaValue, SqlConnection connection) throws SQLException {
        if (javaValue == null) {
            return null;
        }
        return javaValue.toString();
    }
}
