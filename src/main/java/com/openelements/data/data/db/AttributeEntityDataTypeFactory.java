package com.openelements.data.data.db;

import com.openelements.data.data.AttributeType;
import com.openelements.data.data.DataAttribute;
import com.openelements.data.data.DataType;
import java.util.List;

public class AttributeEntityDataTypeFactory {

    public static DataType<AttributeEntity> createDataType() {
        return new DataType<>(
                "AttributeEntity",
                "An entity that represents an attribute.",
                AttributeEntity.class,
                List.of(
                        new DataAttribute<>("dataIdentifier",
                                "The identifier/name of the data type this attribute belongs to",
                                AttributeType.STRING,
                                AttributeEntity::getDataIdentifier),
                        new DataAttribute<>("attributeIdentifier", "The identifier/name of the attribute",
                                AttributeType.STRING,
                                AttributeEntity::getAttributeIdentifier),
                        new DataAttribute<>("type", "The type of the attribute", AttributeType.STRING,
                                AttributeEntity::getAttributeType),
                        new DataAttribute<>("name", "The name of the attribute", AttributeType.I18N_STRING,
                                AttributeEntity::getName),
                        new DataAttribute<>("description", "The description of the attribute",
                                AttributeType.I18N_STRING,
                                AttributeEntity::getDescription)
                )
        );
    }
}
