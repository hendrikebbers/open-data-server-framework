package com.openelements.data.runtime.data;

import com.openelements.data.runtime.api.types.Binary;
import java.util.Objects;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record BinaryReference(@NonNull UUID id) implements Binary, DataReference {

    public BinaryReference {
        Objects.requireNonNull(id, "id must not be null");
    }

    @Override
    public @Nullable String name() {
        throw new UnsupportedOperationException("Data access is not suppoerted for a reference");
    }

    @Override
    public @NonNull byte[] content() {
        throw new UnsupportedOperationException("Data access is not suppoerted for a reference");
    }

}
