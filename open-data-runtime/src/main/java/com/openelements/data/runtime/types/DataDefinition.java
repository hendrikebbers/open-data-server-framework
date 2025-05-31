package com.openelements.data.runtime.types;

import com.openelements.data.api.data.Attribute;
import com.openelements.data.api.data.Data;
import com.openelements.data.api.types.I18nString;
import com.openelements.data.runtime.data.DataType;
import java.time.ZonedDateTime;

@Data(name = "OE_DATA_DEFINITION")
public record DataDefinition(
        @Attribute(partOfIdentifier = true, required = true) String dataIdentifier,
        I18nString name,
        I18nString description,
        boolean isVirtual,
        ZonedDateTime createdAt) {

    public static DataDefinition of(DataType dataType) {
        return new DataDefinition(
                dataType.name(),
                null,
                null,
                dataType.virtual(),
                ZonedDateTime.now());
    }
}
