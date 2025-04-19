package com.openelements.data.openapi;

import io.swagger.v3.oas.models.PathItem;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record OpenApiPath(@NonNull String path, @NonNull PathItem pathItem) {

    public OpenApiPath {
        Objects.requireNonNull(path, "path is null");
        Objects.requireNonNull(pathItem, "pathItem is null");
        if (path.isBlank()) {
            throw new IllegalArgumentException("path must not be blank");
        }
    }
}
