package com.openelements.data.runtime.types;

import com.openelements.data.api.data.Attribute;
import com.openelements.data.api.data.Data;
import com.openelements.data.api.types.I18nString;
import com.openelements.data.runtime.data.ApiData;
import com.openelements.data.runtime.data.DataAttribute;
import com.openelements.data.runtime.data.DataType;
import java.util.List;

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

    public static List<DataAttributeDefinition> of(DataType<?> dataType) {
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

    public static DataAttributeDefinition of(String dataIdentifier, DataAttribute attribute) {
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
