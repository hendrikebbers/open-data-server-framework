package com.openelements.data.runtime.sql.support;

import com.openelements.data.runtime.sql.SqlConnection;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface MatchingDataAttributeTypeSupport<D> extends DataAttributeTypeSupport<D, D> {

    @Override
    default D convertValueFromSqlResult(D sqlValue, SqlConnection connection) {
        return sqlValue;
    }

    @Override
    default D convertValueForSqlPersit(@Nullable D newValue, @Nullable D currentValue,
            @NonNull SqlConnection connection) {
        return newValue;
    }
}
