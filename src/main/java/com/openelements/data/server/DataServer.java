package com.openelements.data.server;

import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.cors.CorsSupport;
import java.util.Set;

public class DataServer {

    private final WebServer webServer;

    public DataServer(int port, Set<DataEndpointMetadata<?>> endpoints) {
        final Routing routing = createRouting(endpoints);
        webServer = WebServer.builder()
                .port(port)
                .addRouting(routing)
                .build();
    }

    public void start() {
        webServer.start()
                .thenAccept(ws -> {
                    System.out.println("Server started at: http://localhost:" + ws.port());
                })
                .exceptionally(ex -> {
                    System.err.println("Failed to start server: " + ex.getMessage());
                    return null;
                });
    }

    public void stop() {
        webServer.shutdown()
                .thenAccept(ws -> {
                    System.out.println("Server stopped");
                })
                .exceptionally(ex -> {
                    System.err.println("Failed to stop server: " + ex.getMessage());
                    return null;
                });
    }

    private Routing createRouting(Set<DataEndpointMetadata<?>> endpoints) {
        final Routing.Builder routingBuilder = Routing.builder();
        endpoints.forEach(endpoint -> {
            routingBuilder.get("/" + endpoint.path(), new GetAllHandler<>(endpoint));
            routingBuilder.get("/" + endpoint.path() + "/count", new GetCountHandler<>(endpoint));
            routingBuilder.get("/" + endpoint.path() + "/page", new GetPageHandler<>(endpoint));
        });
        return routingBuilder
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
