package com.openelements.data.server;

import com.openelements.data.data.DataType;
import com.openelements.data.db.AbstractEntity;
import com.openelements.data.db.EntityMapper;
import com.openelements.data.db.internal.DbHandler;
import com.openelements.data.provider.EntityUpdatesProvider;
import com.openelements.data.provider.internal.ProviderHandler;
import com.openelements.data.server.internal.OpenDataDefinitionStore;
import com.openelements.data.server.internal.handler.FileHandler;
import com.openelements.data.server.internal.handler.SwaggerInitHandler;
import com.openelements.data.server.internal.openapi.OpenApiFactory;
import com.openelements.data.server.internal.openapi.OpenApiHandler;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.cors.CorsSupport;
import io.helidon.webserver.staticcontent.StaticContentSupport;
import io.swagger.v3.oas.models.OpenAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataServer {

    private final static Logger log = LoggerFactory.getLogger(DataServer.class);

    private final OpenDataDefinitionStore openDataDefinitionHandler;

    private final ProviderHandler providerHandler;

    private final int port;

    private final DbHandler dbHandler;

    public DataServer(int port) {
        this.port = port;
        this.dbHandler = new DbHandler("my-unit");
        this.openDataDefinitionHandler = new OpenDataDefinitionStore(dbHandler);
        this.providerHandler = new ProviderHandler(dbHandler);
    }

    public <E extends AbstractEntity> void registerEntityDefinition(String path, DataType<E> dataType) {
        openDataDefinitionHandler.registerApiDataDefinition(path, dataType);
    }

    public <T extends AbstractEntity> void addDataProvider(Class<T> entityClass, EntityUpdatesProvider<T> provider,
            EntityMapper<T> entityMapper, long periodInSeconds) {
        providerHandler.add(entityClass, provider, entityMapper, periodInSeconds);
    }

    public <T extends AbstractEntity> void addDataProvider(Class<T> entityClass, EntityUpdatesProvider<T> provider,
            long periodInSeconds) {
        providerHandler.add(entityClass, provider, periodInSeconds);
    }

    public <T extends AbstractEntity> void addDataProvider(Class<T> entityClass, EntityUpdatesProvider<T> provider) {
        providerHandler.add(entityClass, provider);
    }

    public void start() {
        WebServer webServer = WebServer.builder()
                .port(port)
                .addRouting(createRouting())
                .build();
        webServer.start()
                .thenAccept(ws -> {
                    System.out.println("Server started at: http://localhost:" + ws.port());
                })
                .exceptionally(ex -> {
                    System.err.println("Failed to start server: " + ex.getMessage());
                    return null;
                });
    }

    private Routing createRouting() {
        final OpenAPI openAPI = OpenApiFactory.createOpenApi(openDataDefinitionHandler.getDataDefinitions());
        final Routing.Builder routingBuilder = Routing.builder();
        openDataDefinitionHandler.createRouting(routingBuilder);
        return routingBuilder.get("/openapi", new OpenApiHandler(openAPI))
                .register("/swagger-ui", StaticContentSupport.builder("public/swagger-ui").build())
                .get("/swagger-ui/swagger-initializer.js", new SwaggerInitHandler())
                .get("/files/*", new FileHandler(dbHandler))
                .register(createCorsSupport())
                .build();
    }

    private static CorsSupport createCorsSupport() {
        return CorsSupport.builder()
                .allowOrigins("*")
                .allowMethods("GET")
                .build();
    }
}
