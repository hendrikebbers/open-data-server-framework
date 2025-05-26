package com.openelements.data.server;

import com.openelements.data.api.DataSource;
import com.openelements.data.runtime.data.DataLoader;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.SqlDataContext;
import com.openelements.data.runtime.sql.repositories.DataRepository;
import com.openelements.data.runtime.sql.repositories.DataRepositoryImpl;
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
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataServer {

    private final static Logger log = LoggerFactory.getLogger(DataServer.class);

    private final int port;

    private final SqlConnection sqlConnection;

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
        SqlDataContext dataContext = new SqlDataContext(Executors.newScheduledThreadPool(4));
        for (DataType<?> dataType : DataLoader.loadData()) {
            final DataRepository<?> dataRepository = new DataRepositoryImpl(dataType, sqlConnection);
            dataContext.addRepository(dataType.dataClass(), dataRepository);
            DataHandler handler = new DataHandlerImpl(dataType, dataRepository);
            routingBuilder.get("/" + handler.getName(), new GetAllHandler<>(handler));
            log.info("Registered handler: {}", "/" + handler.getName());
        }
        try {
            dataContext.initialize(sqlConnection);
        } catch (SQLException e) {
            throw new RuntimeException("Error in sql init", e);
        }
        for (DataSource provider : DataSource.getInstances()) {
            provider.install(dataContext);
        }
    }

    private static CorsSupport createCorsSupport() {
        return CorsSupport.builder()
                .allowOrigins("*")
                .allowMethods("GET")
                .build();
    }
}
