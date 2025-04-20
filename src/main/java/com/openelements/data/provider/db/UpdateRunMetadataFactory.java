package com.openelements.data.provider.db;

import com.openelements.data.data.AttributeType;
import com.openelements.data.data.DataAttribute;
import com.openelements.data.data.DataType;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

public class UpdateRunMetadataFactory {

    public static DataType<UpdateRunEntity> createUpdateRunMetadata() {
        List<DataAttribute<UpdateRunEntity, ?>> attributes = List.of(
                new DataAttribute<UpdateRunEntity, String>("type", "Typ of Entity that was updated",
                        AttributeType.STRING,
                        UpdateRunEntity::getType),
                new DataAttribute<UpdateRunEntity, ZonedDateTime>("startOfUpdate", "Start of Update",
                        AttributeType.DATE_TIME,
                        UpdateRunEntity::getStartOfUpdate),
                new DataAttribute<UpdateRunEntity, Duration>("duration", "Duration of Update",
                        AttributeType.NUMBER,
                        UpdateRunEntity::getDuration),
                new DataAttribute<UpdateRunEntity, Integer>("numberOfEntities",
                        "Number of new or updated entities in this run",
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
