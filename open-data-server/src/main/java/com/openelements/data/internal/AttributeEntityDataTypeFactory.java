package com.openelements.data.internal;

import com.openelements.data.data.AttributeType;
import com.openelements.data.data.DataAttribute;
import com.openelements.data.data.DataType;
import com.openelements.data.data.I18nString;
import java.util.List;

public class AttributeEntityDataTypeFactory {

    public static DataType<AttributeEntity> createDataType() {
        return new DataType<>(
                "AttributeEntity",
                "An entity that represents an attribute.",
                AttributeEntity.class,
                List.of(
                        new DataAttribute<>("dataIdentifier",
                                I18nString.of("Data Identifier"),
                                I18nString.of("The identifier/name of the data type this attribute belongs to"),
                                AttributeType.STRING,
                                AttributeEntity::getDataIdentifier),
                        new DataAttribute<>("attributeIdentifier",
                                I18nString.of("Attribute Identifier"),
                                I18nString.of("The identifier/name of the attribute"),
                                AttributeType.STRING,
                                AttributeEntity::getAttributeIdentifier),
                        new DataAttribute<>("type",
                                I18nString.of("Type"),
                                I18nString.of("The type of the attribute"),
                                AttributeType.STRING,
                                AttributeEntity::getAttributeType),
                        new DataAttribute<>("name",
                                I18nString.of("Name"),
                                I18nString.of("The name of the attribute"),
                                AttributeType.I18N_STRING,
                                AttributeEntity::getName),
                        new DataAttribute<>("description",
                                I18nString.of("Description"),
                                I18nString.of("The description of the attribute"),
                                AttributeType.I18N_STRING,
                                AttributeEntity::getDescription)
                )
        );
    }
}
