package com.openelements.data.runtime.api.types;

import com.openelements.data.runtime.data.impl.BinaryData;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface Binary {

    @Nullable
    String name();

    @NonNull
    byte[] content();

    default long getSize() {
        return content().length;
    }

    static Binary of(@Nullable String name, @NonNull byte[] content) {
        return new BinaryData(name, content);
    }

    static Binary of(@NonNull byte[] content) {
        return of(null, content);
    }
}
