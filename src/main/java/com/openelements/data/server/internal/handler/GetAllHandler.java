package com.openelements.data.server.internal.handler;

import com.google.gson.JsonArray;
import com.openelements.data.data.DataType;
import com.openelements.data.data.Language;
import com.openelements.data.db.AbstractEntity;
import com.openelements.data.server.internal.ContentTypes;
import com.openelements.data.server.internal.HttpUtils;
import com.openelements.data.server.internal.JsonFactory;
import com.openelements.data.server.internal.OpenDataDefinition;
import io.helidon.common.http.MediaType;
import io.helidon.webserver.Handler;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;

public class GetAllHandler<E extends AbstractEntity> implements Handler {

    private final OpenDataDefinition<E> endpoint;

    private final JsonFactory jsonFactory;

    public GetAllHandler(OpenDataDefinition<E> endpoint) {
        this.endpoint = endpoint;
        this.jsonFactory = new JsonFactory();
    }

    @Override
    public void accept(ServerRequest req, ServerResponse res) {
        final Language requestedLanguage = HttpUtils.getLanguage(req);
        final JsonArray result = new JsonArray();
        final ContentTypes contentType = HttpUtils.getContentType(req)
                .orElse(ContentTypes.APPLICATION_JSON);
        if (contentType == ContentTypes.APPLICATION_JSON) {
            final DataType<E> dataType = endpoint.dataType();
            endpoint.dataProvider().getAll().stream()
                    .map(entity -> {
                        return jsonFactory.createJsonObject(req, requestedLanguage, entity, dataType);
                    })
                    .forEach(result::add);
            res.headers().contentType(MediaType.APPLICATION_JSON);
            res.headers().add("Content-Language", HttpUtils.getContentLanguageString(requestedLanguage));
            res.send(result.toString());
        } else {
            res.send("Unsupported content type: " + contentType.getContentType());
        }
    }
}
