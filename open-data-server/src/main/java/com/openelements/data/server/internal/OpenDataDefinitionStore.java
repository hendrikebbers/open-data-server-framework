package com.openelements.data.server.internal;

import com.openelements.data.data.DataType;
import com.openelements.data.db.AbstractEntity;
import com.openelements.data.db.EntityMapper;
import com.openelements.data.db.I18nStringEntity;
import com.openelements.data.db.internal.DbHandler;
import com.openelements.data.internal.AttributeEntity;
import com.openelements.data.internal.AttributeEntityDataTypeFactory;
import com.openelements.data.internal.DataTypeEntityTypeFactory;
import com.openelements.data.internal.FileEntityDataTypeFactory;
import com.openelements.data.provider.internal.db.UpdateRunMetadataFactory;
import com.openelements.data.server.internal.handler.GetAllHandler;
import com.openelements.data.server.internal.handler.GetCountHandler;
import com.openelements.data.server.internal.handler.GetPageHandler;
import io.helidon.webserver.http.HttpRouting;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

public class OpenDataDefinitionStore {

    private final AtomicBoolean started = new AtomicBoolean(false);

    private final Set<OpenDataDefinition<?>> dataDefinitions = new CopyOnWriteArraySet<>();

    private final DbHandler dbHandler;

    public OpenDataDefinitionStore(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
        registerDataDefinition("/metadata/updates", UpdateRunMetadataFactory.createUpdateRunMetadata());
        registerDataDefinition("/metadata/dataTypes", DataTypeEntityTypeFactory.createDataType());
        registerDataDefinition("/metadata/attributes", AttributeEntityDataTypeFactory.createDataType());
        registerDataDefinition("/metadata/files", FileEntityDataTypeFactory.createDataType());

    }

    public <E extends AbstractEntity> void registerApiDataDefinition(String path, DataType<E> dataType) {
        registerDataDefinition("/api/" + path, dataType);
    }

    private <E extends AbstractEntity> void registerDataDefinition(String path, DataType<E> dataType) {
        if (started.get()) {
            throw new IllegalStateException("Cannot register data definition after the handler has started");
        }
        if (dataDefinitions.stream().map(d -> d.pathName())
                .anyMatch(p -> Objects.equals(p, path))) {
            throw new IllegalStateException(
                    "Cannot register data definition since path '" + path + "' already registered");
        }

        dataDefinitions.add(
                new OpenDataDefinition<>(path, dataType, dbHandler.createRepository(dataType.entityClass())));

        dataType.attributes().forEach(attribute -> {
            final AttributeEntity entity = new AttributeEntity();
            entity.setDataIdentifier(dataType.name());
            entity.setAttributeIdentifier(attribute.identifier());
            entity.setAttributeType(attribute.type().name());
            entity.setName(new I18nStringEntity(attribute.name()));
            entity.setDescription(new I18nStringEntity(attribute.description()));
            dbHandler.store(entity, EntityMapper.createDefaultMapper());
        });
    }

    public void createRouting(HttpRouting.Builder routingBuilder) {
        if (started.get()) {
            throw new IllegalStateException("Handler has already started");
        }
        started.set(true);
        for (OpenDataDefinition<?> endpoint : dataDefinitions) {
            registerAllEndpointForDefinition(routingBuilder, endpoint);
        }
    }

    public Set<OpenDataDefinition<?>> getDataDefinitions() {
        return Collections.unmodifiableSet(dataDefinitions);
    }

    private void registerAllEndpointForDefinition(HttpRouting.Builder routingBuilder, OpenDataDefinition<?> endpoint) {
        routingBuilder.get(endpoint.pathName(), new GetAllHandler<>(endpoint));
        routingBuilder.get(endpoint.pathName() + "/count", new GetCountHandler<>(endpoint));
        routingBuilder.get(endpoint.pathName() + "/page", new GetPageHandler<>(endpoint));
    }
}
