package com.openelements.data.runtime.data;

import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record DataAttributeReference(@NonNull Class<? extends Record> toType,
                                     @NonNull String toAttribute) {

    public DataAttributeReference {
        Objects.requireNonNull(toType, "toType must not be null");
        Objects.requireNonNull(toAttribute, "toAttribute must not be null");
        if (toAttribute.isBlank()) {
            throw new IllegalArgumentException("toAttribute must not be blank");
        }
    }
}
