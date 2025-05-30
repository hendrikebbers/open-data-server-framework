package com.openelements.data.runtime.sql.types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

public abstract class AbstractSqlTypeSupport<T, U> implements SqlTypeSupport<T, U> {

    private final Class<T> javaType;

    private final String sqlType;

    protected AbstractSqlTypeSupport(Class<T> javaType, String sqlType) {
        this.javaType = javaType;
        this.sqlType = sqlType;
    }

    @Override
    public boolean supportsJavaType(Type type) {
        if (type instanceof Class<?> clazz) {
            return Objects.equals(clazz, javaType);
        }
        if (type instanceof ParameterizedType parameterizedType) {
            return Objects.equals(parameterizedType.getRawType(), javaType);
        }
        return false;
    }

    @Override
    public Type getJavaType() {
        return javaType;
    }

    @Override
    public String getNativeSqlType() {
        return sqlType;
    }
}
