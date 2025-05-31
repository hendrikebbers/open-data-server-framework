package com.openelements.data.server;

import com.openelements.data.runtime.DataSource;
import com.openelements.data.runtime.data.DataLoader;
import com.openelements.data.runtime.data.DataRepository;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.runtime.sql.SqlDataContext;
import com.openelements.data.runtime.sql.repositories.TableRepository;
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
import java.util.concurrent.ThreadFactory;
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
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("data-server-worker");
                thread.setDaemon(true);
                thread.setUncaughtExceptionHandler(
                        (t, e) -> log.error("Uncaught exception in thread {}", t.getName(), e));
                return thread;
            }
        };
        SqlDataContext dataContext = new SqlDataContext(Executors.newScheduledThreadPool(4, threadFactory),
                sqlConnection);
        try {
            for (DataType<?> dataType : DataLoader.loadData()) {
                final DataRepository<?> dataRepository = new TableRepository(dataType, sqlConnection);
                dataContext.addDataType(dataType);
                DataHandler handler = new DataHandlerImpl(dataType, dataRepository);
                routingBuilder.get("/" + handler.getName(), new GetAllHandler<>(handler));
                log.info("Registered handler: {}", "/" + handler.getName());
            }
            dataContext.initialize();
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
