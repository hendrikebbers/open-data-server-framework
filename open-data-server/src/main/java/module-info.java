module com.openelements.data.server {
    exports com.openelements.data.server.internal.handler;
    exports com.openelements.data.server;
    requires com.google.gson;
    requires org.slf4j;
    requires io.helidon.webclient;
    requires io.helidon.webserver.cors;
    requires io.helidon.webserver.staticcontent;
    requires io.helidon.webserver;
    requires io.swagger.v3.oas.models;
    requires com.openelements.data.runtime;
    requires com.openelements.data.api;
    requires static org.jspecify;
    requires java.sql;
}