package com.openelements.data.runtime.sql.support;

import com.openelements.data.runtime.sql.DataAttributeTypeSupport;
import com.openelements.data.runtime.sql.PersistenceContext;
import com.openelements.data.runtime.sql.QueryContext;
import com.openelements.data.runtime.sql.SqlDataType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface MatchingDataAttributeTypeSupport<D> extends DataAttributeTypeSupport<D, D> {

    @Override
    default Class<D> getJavaType() {
        final SqlDataType<D> sqlDataType = getSqlDataType();
        if (sqlDataType == null) {
            throw new IllegalStateException("SQL data type is not set");
        }
        return sqlDataType.getJavaType();
    }

    @Override
    default D convertValueFromSqlResult(D sqlValue, QueryContext queryContext) {
        return sqlValue;
    }

    @Override
    default D convertValueForSqlPersit(@Nullable D newValue, @Nullable D currentValue,
            @NonNull PersistenceContext persistenceContext) {
        return newValue;
    }
}
