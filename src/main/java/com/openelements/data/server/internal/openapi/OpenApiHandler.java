package com.openelements.data.server.internal.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openelements.data.server.internal.ContentTypes;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.swagger.v3.oas.models.OpenAPI;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public class OpenApiHandler implements Handler {

    private final String openApiJson;

    public OpenApiHandler(@NonNull final OpenAPI openAPI) {
        Objects.requireNonNull(openAPI, "openAPI is null");
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        try {
            openApiJson = mapper.writeValueAsString(openAPI);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException("Can no create OpenApi JSON defintion", e);
        }
    }

    @Override
    public void handle(ServerRequest req, ServerResponse res) throws Exception {
        res.headers().contentType(ContentTypes.APPLICATION_JSON);
        res.send(openApiJson);
    }
}
