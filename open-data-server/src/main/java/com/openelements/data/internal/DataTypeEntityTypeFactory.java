package com.openelements.data.internal;

import com.openelements.data.data.AttributeType;
import com.openelements.data.data.DataAttribute;
import com.openelements.data.data.DataType;
import com.openelements.data.data.I18nString;
import java.util.List;

public class DataTypeEntityTypeFactory {

    public static DataType<DataTypeEntity> createDataType() {
        return new DataType<>(
                "DataTypeEntity",
                "An entity that represents a data type.",
                DataTypeEntity.class,
                List.of(
                        new DataAttribute<>("dataIdentifier",
                                I18nString.of("Data Identifier"),
                                I18nString.of("The identifier/name of the data type"),
                                AttributeType.STRING,
                                DataTypeEntity::getDataIdentifier),
                        new DataAttribute<>("name",
                                I18nString.of("Name"),
                                I18nString.of("The name of the data type"),
                                AttributeType.I18N_STRING,
                                DataTypeEntity::getName),
                        new DataAttribute<>("description",
                                I18nString.of("Description"),
                                I18nString.of("The description of the data type"),
                                AttributeType.I18N_STRING,
                                DataTypeEntity::getDescription)
                )
        );
    }
}
