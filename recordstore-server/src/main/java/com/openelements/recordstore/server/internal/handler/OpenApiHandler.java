package com.openelements.recordstore.server.internal.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.openelements.recordstore.server.internal.ContentTypes;
import com.openelements.recordstore.server.internal.openapi.OpenApiFactory;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import java.io.IOException;

public class OpenApiHandler implements Handler {

    private final OpenApiFactory openApiFactory;

    public OpenApiHandler(OpenApiFactory openApiFactory) {
        this.openApiFactory = openApiFactory;
    }

    @Override
    public void handle(ServerRequest req, ServerResponse res) throws Exception {
        final OpenAPI openAPI = openApiFactory.create();
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(SecurityScheme.Type.class, new SecuritySchemeTypeAdapter())
                .setPrettyPrinting()
                .create();
        final String json = gson.toJson(openAPI);
        res.headers().contentType(ContentTypes.APPLICATION_JSON);
        res.send(json);
    }

    class SecuritySchemeTypeAdapter extends TypeAdapter<Type> {

        @Override
        public void write(JsonWriter out, SecurityScheme.Type value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            // OpenAPI requires lowercase
            out.value(value.name().toLowerCase());
        }

        @Override
        public SecurityScheme.Type read(JsonReader in) throws IOException {
            String value = in.nextString();
            return SecurityScheme.Type.valueOf(value.toUpperCase());
        }
    }
}
