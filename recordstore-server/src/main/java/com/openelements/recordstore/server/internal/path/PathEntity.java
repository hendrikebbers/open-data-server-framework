package com.openelements.recordstore.server.internal.path;

import com.openelements.data.runtime.api.Attribute;
import com.openelements.data.runtime.api.Data;

@Data(isVirtual = true)
public record PathEntity(@Attribute(required = true, partOfIdentifier = true) String dataType,
                         @Attribute(required = true, partOfIdentifier = true) String pathType,
                         @Attribute(required = true) String path) {

    public PathEntity {
        if (dataType == null || dataType.isBlank()) {
            throw new IllegalArgumentException("Data type must not be null or empty");
        }
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Path must not be null or empty");
        }
    }
}
