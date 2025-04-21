package com.openelements.data.server.internal;

import com.openelements.data.data.DataType;
import com.openelements.data.data.db.AttributeEntity;
import com.openelements.data.data.db.AttributeEntityDataTypeFactory;
import com.openelements.data.data.db.DataTypeEntityTypeFactory;
import com.openelements.data.db.AbstractEntity;
import com.openelements.data.db.EntityMapper;
import com.openelements.data.db.I18nStringEntity;
import com.openelements.data.db.internal.DbHandler;
import com.openelements.data.provider.db.UpdateRunMetadataFactory;
import io.helidon.webserver.Routing;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

public class OpenDataDefinitionHandler {

    private final AtomicBoolean started = new AtomicBoolean(false);

    private final Set<OpenDataDefinition<?>> dataDefinitions = new CopyOnWriteArraySet<>();

    private final DbHandler dbHandler;

    public OpenDataDefinitionHandler(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
        registerDataDefinition("updates", UpdateRunMetadataFactory.createUpdateRunMetadata());
        registerDataDefinition("dataTypes", DataTypeEntityTypeFactory.createDataType());
        registerDataDefinition("attributes", AttributeEntityDataTypeFactory.createDataType());
    }

    public <E extends AbstractEntity> void registerDataDefinition(String path, DataType<E> dataType) {
        if (started.get()) {
            throw new IllegalStateException("Cannot register data definition after the handler has started");
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

    public void createRouting(Routing.Builder routingBuilder) {
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

    private void registerAllEndpointForDefinition(Routing.Builder routingBuilder, OpenDataDefinition<?> endpoint) {
        routingBuilder.get("/api/" + endpoint.pathName(), new GetAllHandler<>(endpoint));
        routingBuilder.get("/api/" + endpoint.pathName() + "/count", new GetCountHandler<>(endpoint));
        routingBuilder.get("/api/" + endpoint.pathName() + "/page", new GetPageHandler<>(endpoint));
    }
}
