import com.openelements.data.runtime.api.DataTypeProvider;

module com.openelements.recordstore.server {
    exports com.openelements.recordstore.server.internal.handler;
    exports com.openelements.recordstore.server;

    provides DataTypeProvider
            with com.openelements.recordstore.server.internal.ServerDataTypeProvider;

    opens com.openelements.recordstore.server.internal.path to
            com.openelements.data.runtime;

    requires com.google.gson;
    requires org.slf4j;
    requires io.helidon.webclient;
    requires io.helidon.webserver.cors;
    requires io.helidon.webserver.staticcontent;
    requires io.helidon.webserver;
    requires io.swagger.v3.oas.models;
    requires com.openelements.data.runtime;
    requires org.yaml.snakeyaml;
    requires static org.jspecify;
}