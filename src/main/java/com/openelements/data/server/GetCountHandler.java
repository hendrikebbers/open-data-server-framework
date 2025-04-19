package com.openelements.data.server;

import io.helidon.webserver.Handler;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;

public class GetCountHandler<ENTITY> implements Handler {

    private final DataEndpointMetadata<ENTITY> endpoint;

    public GetCountHandler(DataEndpointMetadata<ENTITY> endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void accept(ServerRequest req, ServerResponse res) {
        res.headers().contentType(io.helidon.common.http.MediaType.TEXT_PLAIN);
        res.send(endpoint.dataProvider().getCount() + "");
    }
}
