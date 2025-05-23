package com.openelements.data.server;

import com.openelements.data.api.DataSource;
import com.openelements.data.api.context.DataContext;
import com.openelements.data.runtime.data.DataLoader;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.sql.ConnectionProvider;
import com.openelements.data.runtime.sql.repositories.DataRepository;
import com.openelements.data.runtime.sql.repositories.DataRepositoryImpl;
import com.openelements.data.server.internal.DataContextImpl;
import com.openelements.data.server.internal.handler.DataHandler;
import com.openelements.data.server.internal.handler.DataHandlerImpl;
import com.openelements.data.server.internal.handler.GetAllHandler;
import com.openelements.data.server.internal.handler.SwaggerInitHandler;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.cors.CorsSupport;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.http.HttpRouting.Builder;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.staticcontent.ClasspathHandlerConfig;
import io.helidon.webserver.staticcontent.StaticContentFeature;
import java.sql.SQLException;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataServer {

    private final static Logger log = LoggerFactory.getLogger(DataServer.class);

    private final int port;

    private final ConnectionProvider connectionProvider;

    public DataServer(int port, ConnectionProvider connectionProvider) {
        this.port = port;
        this.connectionProvider = connectionProvider;
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
        final Set<DataType<?>> dataTypes = DataLoader.loadData();
        for (DataType<?> dataType : dataTypes) {
            final DataRepository<?> dataRepository = new DataRepositoryImpl(dataType, connectionProvider);
            DataContextImpl.getInstance().addRepository(dataType.dataClass(), dataRepository);
            try {
                dataRepository.createTable();
            } catch (SQLException e) {
                throw new RuntimeException("Error creating table", e);
            }
            DataHandler handler = new DataHandlerImpl(dataType, dataRepository);
            routingBuilder.get("/", new GetAllHandler<>(handler));

            final DataContext dataContext = DataContextImpl.getInstance();
            final Set<DataSource> instances = DataSource.getInstances();
            for (DataSource provider : instances) {
                provider.install(dataContext);
            }
        }
    }

    private static CorsSupport createCorsSupport() {
        return CorsSupport.builder()
                .allowOrigins("*")
                .allowMethods("GET")
                .build();
    }
}
