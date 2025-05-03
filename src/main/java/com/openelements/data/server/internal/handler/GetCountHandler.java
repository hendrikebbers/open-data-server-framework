package com.openelements.data.server.internal.handler;

import com.openelements.data.db.AbstractEntity;
import com.openelements.data.server.internal.ContentTypes;
import com.openelements.data.server.internal.OpenDataDefinition;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

public class GetCountHandler<E extends AbstractEntity> implements Handler {

    private final OpenDataDefinition<E> endpoint;

    public GetCountHandler(OpenDataDefinition<E> endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void handle(ServerRequest req, ServerResponse res) throws Exception {
        res.headers().contentType(ContentTypes.TEXT_PLAIN);
        res.send(endpoint.dataProvider().getCount() + "");
    }
}
