package com.openelements.data.runtime.data.impl;

import com.openelements.data.runtime.api.types.Binary;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record BinaryData(@Nullable String name, @NonNull byte[] content) implements Binary {

    public BinaryData {
        Objects.requireNonNull(content, "Content cannot be null");
    }
}
