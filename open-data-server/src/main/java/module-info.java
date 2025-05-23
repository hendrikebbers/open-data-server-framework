module com.openelements.data {
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
}