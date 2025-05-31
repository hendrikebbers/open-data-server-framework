package com.openelements.data.runtime.types;

import com.openelements.data.api.data.Attribute;
import com.openelements.data.api.data.Data;
import com.openelements.data.api.types.I18nString;
import com.openelements.data.runtime.data.ApiData;
import com.openelements.data.runtime.data.DataAttribute;
import com.openelements.data.runtime.data.DataType;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

@ApiData
@Data
public record DataAttributeDefinition(
        @Attribute(partOfIdentifier = true, required = true) String identifier,
        @Attribute(partOfIdentifier = true, required = true) String dataIdentifier,
        String type,
        I18nString name,
        I18nString description,
        boolean partOfIdentifier,
        boolean required) {

    public static List<DataAttributeDefinition> of(@NonNull final DataType<?> dataType) {
        Objects.requireNonNull(dataType, "dataType must not be null");
        final String dataIdentifier = dataType.name();
        return dataType.attributes().stream()
                .map(attribute -> new DataAttributeDefinition(
                        attribute.name(),
                        dataIdentifier,
                        attribute.type().getTypeName(),
                        null,
                        null,
                        attribute.partOfIdentifier(),
                        attribute.required()))
                .toList();
    }

    public static DataAttributeDefinition of(@NonNull final String dataIdentifier,
            @NonNull final DataAttribute attribute) {
        Objects.requireNonNull(dataIdentifier, "dataIdentifier must not be null");
        Objects.requireNonNull(attribute, "attribute must not be null");
        return new DataAttributeDefinition(
                attribute.name(),
                dataIdentifier,
                attribute.type().getTypeName(),
                null,
                null,
                attribute.partOfIdentifier(),
                attribute.partOfIdentifier());
    }
}
