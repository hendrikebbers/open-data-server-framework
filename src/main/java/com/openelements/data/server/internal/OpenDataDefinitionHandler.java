package com.openelements.data.server.internal;

import com.openelements.data.data.DataType;
import com.openelements.data.db.AbstractEntity;
import com.openelements.data.db.DbHandler;
import com.openelements.data.provider.UpdateRunMetadataFactory;
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
        registerDataDefinition("updates",
                UpdateRunMetadataFactory.createUpdateRunMetadata());
    }

    public <E extends AbstractEntity> void registerDataDefinition(String path, DataType<E> dataType) {
        if (started.get()) {
            throw new IllegalStateException("Cannot register data definition after the handler has started");
        }
        dataDefinitions.add(
                new OpenDataDefinition<>(path, dataType, dbHandler.createDataProvider(dataType.entityClass())));
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
