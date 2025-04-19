package com.openelements.data.server;

import com.openelements.data.openapi.OpenApiFactory;
import com.openelements.data.openapi.OpenApiHandler;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.cors.CorsSupport;
import io.helidon.webserver.staticcontent.StaticContentSupport;
import io.swagger.v3.oas.models.OpenAPI;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataServer {

    private final static Logger log = LoggerFactory.getLogger(DataServer.class);

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
            routingBuilder.get("/api/" + endpoint.path(), new GetAllHandler<>(endpoint));
            routingBuilder.get("/api/" + endpoint.path() + "/count", new GetCountHandler<>(endpoint));
            routingBuilder.get("/api/" + endpoint.path() + "/page", new GetPageHandler<>(endpoint));
        });
        OpenAPI openAPI = OpenApiFactory.createOpenApi(endpoints);
        routingBuilder.get("/openapi", new OpenApiHandler(openAPI));
        routingBuilder.register("/swagger-ui", StaticContentSupport.builder("public/swagger-ui").build());
        routingBuilder.get("/swagger-ui/swagger-initializer.js", (req, res) -> {
            String apiUrl = "/openapi";
            String script = """
                    window.onload = () => {
                      window.ui = SwaggerUIBundle({
                        url: "%s",
                        dom_id: '#swagger-ui',
                        presets: [
                          SwaggerUIBundle.presets.apis,
                          SwaggerUIStandalonePreset
                        ],
                        layout: "BaseLayout"
                      });
                    };
                    """.formatted(apiUrl);
            res.headers().add("Content-Type", "application/javascript");
            res.send(script);
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
