package com.openelements.data.server.internal;

import com.google.gson.JsonArray;
import com.openelements.data.data.DataType;
import com.openelements.data.data.Language;
import com.openelements.data.db.AbstractEntity;
import io.helidon.common.http.MediaType;
import io.helidon.webserver.Handler;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;

public class GetPageHandler<E extends AbstractEntity> implements Handler {

    private final OpenDataDefinition<E> endpoint;

    private final JsonFactory jsonFactory;

    public GetPageHandler(OpenDataDefinition<E> endpoint) {
        this.endpoint = endpoint;
        this.jsonFactory = new JsonFactory();
    }

    @Override
    public void accept(ServerRequest req, ServerResponse res) {
        final int page = req.queryParams().first("page")
                .map(Integer::parseInt)
                .orElse(0);
        final int pageSize = req.queryParams().first("size")
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
        res.headers().contentType(MediaType.APPLICATION_JSON);
        res.headers().add("Content-Language", HttpUtils.getContentLanguageString(requestedLanguage));
        res.send(result.toString());
    }


}
