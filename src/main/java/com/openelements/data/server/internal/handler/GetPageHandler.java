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

public class GetPageHandler<E extends AbstractEntity> implements Handler {

    private final OpenDataDefinition<E> endpoint;

    private final JsonFactory jsonFactory;

    public GetPageHandler(OpenDataDefinition<E> endpoint) {
        this.endpoint = endpoint;
        this.jsonFactory = new JsonFactory();
    }

    @Override
    public void handle(ServerRequest req, ServerResponse res) throws Exception {

        final int page = req.query().first("page")
                .map(Integer::parseInt)
                .orElse(0);
        final int pageSize = req.query().first("size")
                .map(Integer::parseInt)
                .orElse(10);
        final Language requestedLanguage = HttpUtils.getLanguage(req);
        final JsonArray result = new JsonArray();
        final DataType<E> dataType = endpoint.dataType();
        endpoint.dataProvider().getPage(page, pageSize).stream()
                .map(entity -> {
                    return jsonFactory.createJsonObject(req, requestedLanguage, entity, dataType);
                })
                .forEach(result::add);
        res.headers().contentType(ContentTypes.APPLICATION_JSON);
        res.header("Content-Language", HttpUtils.getContentLanguageString(requestedLanguage));
        res.send(result.toString());
    }
}
