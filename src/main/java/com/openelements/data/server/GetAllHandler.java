package com.openelements.data.server;

import com.google.gson.JsonArray;
import com.openelements.data.data.DataType;
import com.openelements.data.data.Language;
import io.helidon.common.http.MediaType;
import io.helidon.webserver.Handler;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;

public class GetAllHandler<ENTITY> implements Handler {

    private final DataEndpointMetadata<ENTITY> endpoint;

    private final JsonFactory jsonFactory;

    public GetAllHandler(DataEndpointMetadata<ENTITY> endpoint) {
        this.endpoint = endpoint;
        this.jsonFactory = new JsonFactory();
    }

    @Override
    public void accept(ServerRequest req, ServerResponse res) {
        final Language requestedLanguage = HttpUtils.getLanguage(req);
        final JsonArray result = new JsonArray();
        final DataType<ENTITY> dataType = endpoint.dataType();
        endpoint.dataProvider().getAll().stream()
                .map(entity -> {
                    return jsonFactory.createJsonObject(req, requestedLanguage, entity, dataType);
                })
                .forEach(result::add);
        res.headers().contentType(MediaType.APPLICATION_JSON);
        res.headers().add("Content-Language", HttpUtils.getContentLanguageString(requestedLanguage));
        res.send(result.toString());
    }

}
