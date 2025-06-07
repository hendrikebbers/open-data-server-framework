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
import com.openelements.recordstore.server.internal.handler.SwaggerInitHandler;
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
        WebServer webServer = WebServer.builder()
                .port(port)
                .addRouting(createRouting())
                .build();
        webServer.start();
    }

    private HttpRouting.Builder createRouting() {
        final ClasspathHandlerConfig classpathHandlerConfig = ClasspathHandlerConfig.create("public/swagger-ui");
        final HttpService service = StaticContentFeature.createService(classpathHandlerConfig);
        final HttpRouting.Builder routingBuilder = HttpRouting.builder();

        initData(routingBuilder);
        return routingBuilder.register("/swagger-ui", service)
                .get("/swagger-ui/swagger-initializer.js", new SwaggerInitHandler())
                .register(createCorsSupport());
    }

    private void initData(Builder routingBuilder) {
        final DataContext dataContext = DataContextFactory.createDataContext(
                Executors.newScheduledThreadPool(8, threadFactory), sqlConnection);
        final Set<DataType<?>> dataTypes = DataType.loadData();
        final JsonFactory jsonFactory = new JsonFactory(dataTypes, pathResolver);
        for (DataType<?> dataType : dataTypes) {
            final DataRepository<?> dataRepository = DataRepository.of(dataType, sqlConnection);
            DataHandler handler = new DataHandlerImpl(dataType, dataRepository);
            final String path;
            if (dataType.api()) {
                path = "/api/" + toRestUrlPath(dataType.name());
            } else {
                path = "/records/" + toRestUrlPath(dataType.name());
            }
            pathResolver.registerGetAllPath(dataType, path);
            routingBuilder.get(path, new GetAllHandler<>(handler, jsonFactory));
            log.info("Registered handler: {}", path);
        }
        for (DataSource provider : DataSource.getInstances()) {
            provider.install(dataContext);
        }
        dataContext.scheduleWithFixedDelay(0, 1, TimeUnit.MINUTES, context -> {
            context.store(PathEntity.class, pathResolver.getAllPaths());
        });
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
