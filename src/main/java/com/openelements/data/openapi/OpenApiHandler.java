package com.openelements.data.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.helidon.common.http.MediaType;
import io.helidon.webserver.Handler;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
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
    public void accept(@NonNull final ServerRequest req, @NonNull final ServerResponse res) {
        res.headers().contentType(MediaType.APPLICATION_JSON);
        res.send(openApiJson);
    }
}
