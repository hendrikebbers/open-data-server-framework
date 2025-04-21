package com.openelements.data.provider.db;

import com.openelements.data.data.AttributeType;
import com.openelements.data.data.DataAttribute;
import com.openelements.data.data.DataType;
import com.openelements.data.data.I18nString;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

public class UpdateRunMetadataFactory {

    public static DataType<UpdateRunEntity> createUpdateRunMetadata() {
        List<DataAttribute<UpdateRunEntity, ?>> attributes = List.of(
                new DataAttribute<UpdateRunEntity, String>("type",
                        I18nString.of("Type"),
                        I18nString.of("Typ of Entity that was updated"),
                        AttributeType.STRING,
                        UpdateRunEntity::getType),
                new DataAttribute<UpdateRunEntity, ZonedDateTime>("startOfUpdate",
                        I18nString.of("Start of Update"),
                        I18nString.of("Start of Update"),
                        AttributeType.DATE_TIME,
                        UpdateRunEntity::getStartOfUpdate),
                new DataAttribute<UpdateRunEntity, Duration>("duration",
                        I18nString.of("Duration"),
                        I18nString.of("Duration of Update"),
                        AttributeType.NUMBER,
                        UpdateRunEntity::getDuration),
                new DataAttribute<UpdateRunEntity, Integer>("numberOfEntities",
                        I18nString.of("Number of Entities"),
                        I18nString.of("Number of new or updated entities in this run"),
                        AttributeType.NUMBER,
                        UpdateRunEntity::getNumberOfEntities)
        );
        return new DataType<>(
                "UpdateRun",
                "Metadata about the update run",
                UpdateRunEntity.class,
                attributes);
    }
}
