package com.openelements.data.server.internal;

import com.openelements.data.db.AbstractEntity;
import io.helidon.webserver.Handler;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;

public class GetCountHandler<E extends AbstractEntity> implements Handler {

    private final OpenDataDefinition<E> endpoint;

    public GetCountHandler(OpenDataDefinition<E> endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void accept(ServerRequest req, ServerResponse res) {
        res.headers().contentType(io.helidon.common.http.MediaType.TEXT_PLAIN);
        res.send(endpoint.dataProvider().getCount() + "");
    }
}
