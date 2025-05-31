package com.openelements.data.runtime.types;

import com.openelements.data.runtime.api.Attribute;
import com.openelements.data.runtime.api.Data;
import com.openelements.data.runtime.api.types.I18nString;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.data.impl.ApiData;
import java.time.ZonedDateTime;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

@ApiData
@Data
public record DataDefinition(
        @Attribute(partOfIdentifier = true, required = true) String dataIdentifier,
        I18nString name,
        I18nString description,
        boolean isVirtual,
        ZonedDateTime createdAt) {

    public static DataDefinition of(@NonNull final DataType dataType) {
        Objects.requireNonNull(dataType, "dataType");
        return new DataDefinition(
                dataType.name(),
                null,
                null,
                dataType.virtual(),
                ZonedDateTime.now());
    }
}
