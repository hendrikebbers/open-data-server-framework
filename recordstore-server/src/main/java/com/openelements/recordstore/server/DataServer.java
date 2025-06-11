package com.openelements.recordstore.server;

import com.openelements.data.runtime.api.DataContext;
import com.openelements.data.runtime.api.DataSource;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.integration.DataContextFactory;
import com.openelements.data.runtime.integration.DataRepository;
import com.openelements.data.runtime.sql.api.SqlConnection;
import com.openelements.recordstore.server.internal.ServerThreadFactory;
import com.openelements.recordstore.server.internal.gson.JsonFactory;
import com.openelements.recordstore.server.internal.handler.DataHandler;
import com.openelements.recordstore.server.internal.handler.DataHandlerImpl;
import com.openelements.recordstore.server.internal.handler.GetAllHandler;
import com.openelements.recordstore.server.internal.handler.GetAllWithPaginationHandler;
import com.openelements.recordstore.server.internal.handler.GetCountHandler;
import com.openelements.recordstore.server.internal.handler.OpenApiHandler;
import com.openelements.recordstore.server.internal.handler.SwaggerInitHandler;
import com.openelements.recordstore.server.internal.openapi.OpenApiFactory;
import com.openelements.recordstore.server.internal.path.PathEntity;
import com.openelements.recordstore.server.internal.path.PathResolverImpl;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.cors.CorsSupport;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.http.HttpRouting.Builder;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.staticcontent.ClasspathHandlerConfig;
import io.helidon.webserver.staticcontent.StaticContentFeature;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataServer {

    private final static Logger log = LoggerFactory.getLogger(DataServer.class);

    private final int port;

    private final SqlConnection sqlConnection;

    private final ServerThreadFactory threadFactory = new ServerThreadFactory();

    private final PathResolverImpl pathResolver = new PathResolverImpl();

    public DataServer(int port, SqlConnection sqlConnection) {
        this.port = port;
        this.sqlConnection = sqlConnection;
    }

    public void start() {
        log.info("Starting DataServer on port {}", port);
        WebServer webServer = WebServer.builder()
                .port(port)
                .addRouting(createRouting())
                .build();
        webServer.start();
        log.info("DataServer started successfully on port {}", port);
    }

    private HttpRouting.Builder createRouting() {
        final ClasspathHandlerConfig classpathHandlerConfig = ClasspathHandlerConfig.create("public/swagger-ui");
        final HttpService service = StaticContentFeature.createService(classpathHandlerConfig);
        final HttpRouting.Builder routingBuilder = HttpRouting.builder();

        initData(routingBuilder);
        routingBuilder.register("/swagger-ui", service)
                .get("/swagger-ui/swagger-initializer.js", new SwaggerInitHandler())
                .register(createCorsSupport());
        log.info("Swagger UI configured at {}", "/swagger-ui");
        return routingBuilder;
    }

    private void initData(Builder routingBuilder) {
        log.debug("Loading data types");
        final Set<DataType<?>> dataTypes = DataType.loadData();
        log.debug("Loaded {} data types", dataTypes.size());
        log.debug("Initializing data context");
        final DataContext dataContext = DataContextFactory.createDataContext(
                Executors.newScheduledThreadPool(8, threadFactory), sqlConnection, dataTypes);
        log.debug("Initializing json factory");
        final JsonFactory jsonFactory = new JsonFactory(dataTypes, pathResolver);
        for (DataType<?> dataType : dataTypes) {
            final DataRepository<?> dataRepository = DataRepository.of(dataType, sqlConnection);
            final DataHandler handler = new DataHandlerImpl(dataType, dataRepository);
            final String allPath;
            if (dataType.api()) {
                allPath = "/api/" + toRestUrlPath(dataType.name()) + "/all";
            } else {
                allPath = "/records/" + toRestUrlPath(dataType.name()) + "/all";
            }
            pathResolver.registerGetAllPath(dataType, allPath);
            routingBuilder.get(allPath, new GetAllHandler<>(handler, jsonFactory));
            log.info("Registered handler: {}", allPath);

            final String paginationPath;
            if (dataType.api()) {
                paginationPath = "/api/" + toRestUrlPath(dataType.name());
            } else {
                paginationPath = "/records/" + toRestUrlPath(dataType.name());
            }
            pathResolver.registerGetAllWithPaginationPath(dataType, paginationPath);
            routingBuilder.get(paginationPath, new GetAllWithPaginationHandler<>(handler, jsonFactory));
            log.info("Registered handler: {}", paginationPath);

            final String countPath;
            if (dataType.api()) {
                countPath = "/api/" + toRestUrlPath(dataType.name()) + "/count";
            } else {
                countPath = "/records/" + toRestUrlPath(dataType.name()) + "/count";
            }
            pathResolver.registerCountPath(dataType, countPath);
            routingBuilder.get(countPath, new GetCountHandler<>(handler));
            log.info("Registered handler: {}", countPath);
        }
        for (DataSource provider : DataSource.getInstances()) {
            provider.install(dataContext);
        }
        dataContext.scheduleWithFixedDelay(0, 1, TimeUnit.MINUTES, context -> {
            context.store(PathEntity.class, pathResolver.getAllPaths());
        });
        OpenApiFactory openApiFactory = new OpenApiFactory(pathResolver, dataTypes);
        routingBuilder.get("/openapi", new OpenApiHandler(openApiFactory));
        log.info("OpenAPI endpoint registered at {}", "/openapi");
        log.debug("Data context and repositories initialized successfully");
    }

    private static CorsSupport createCorsSupport() {
        return CorsSupport.builder()
                .allowOrigins("*")
                .allowMethods("GET")
                .build();
    }

    public static String toRestUrlPath(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        final StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (Character.isUpperCase(c) && i > 0 &&
                    (Character.isLowerCase(chars[i - 1]) || Character.isDigit(chars[i - 1]))) {
                result.append('-');
            } else if (Character.isUpperCase(c) && i > 0 &&
                    Character.isUpperCase(chars[i - 1]) &&
                    i + 1 < chars.length &&
                    Character.isLowerCase(chars[i + 1])) {
                result.append('-');
            }
            result.append(Character.toLowerCase(c));
        }
        return result.toString();
    }
}
