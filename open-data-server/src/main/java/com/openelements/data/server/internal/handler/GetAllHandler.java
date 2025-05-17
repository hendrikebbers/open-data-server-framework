package com.openelements.data.server.internal.handler;

import com.google.gson.JsonArray;
import com.openelements.data.data.DataType;
import com.openelements.data.data.Language;
import com.openelements.data.db.AbstractEntity;
import com.openelements.data.server.internal.ContentTypes;
import com.openelements.data.server.internal.HttpUtils;
import com.openelements.data.server.internal.JsonFactory;
import com.openelements.data.server.internal.OpenDataDefinition;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

public class GetAllHandler<E extends AbstractEntity> implements Handler {

    private final OpenDataDefinition<E> endpoint;

    private final JsonFactory jsonFactory;

    public GetAllHandler(OpenDataDefinition<E> endpoint) {
        this.endpoint = endpoint;
        this.jsonFactory = new JsonFactory();
    }

    @Override
    public void handle(ServerRequest serverRequest, ServerResponse serverResponse) throws Exception {
        final Language requestedLanguage = HttpUtils.getLanguage(serverRequest);
        final JsonArray result = new JsonArray();
        final ContentTypes contentType = HttpUtils.getContentType(serverRequest)
                .orElse(ContentTypes.APPLICATION_JSON);
        if (contentType == ContentTypes.APPLICATION_JSON) {
            final DataType<E> dataType = endpoint.dataType();
            endpoint.dataProvider().getAll().stream()
                    .map(entity -> {
                        return jsonFactory.createJsonObject(serverRequest, requestedLanguage, entity, dataType);
                    })
                    .forEach(result::add);
            serverResponse.headers().contentType(ContentTypes.APPLICATION_JSON);
            serverResponse.header("Content-Language", HttpUtils.getContentLanguageString(requestedLanguage));
            serverResponse.send(result.toString());
        } else {
            serverResponse.send("Unsupported content type: " + contentType.getContentType());
        }
    }
}
