package com.openelements.recordstore.server.internal.handler;

import com.openelements.data.runtime.api.Language;
import com.openelements.recordstore.server.internal.ContentTypes;
import com.openelements.recordstore.server.internal.HttpUtils;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

public abstract class AbstractHandler implements Handler {

    private final BearerTokenManager bearerTokenManager;

    protected AbstractHandler() {
        this.bearerTokenManager = new BearerTokenManager();
    }

    protected abstract boolean isPubliclyAvailable();

    protected abstract void handle(ServerRequest serverRequest, ContentTypes requestedContentType,
            Language requestedLanguage, ServerResponse serverResponse);

    @Override
    public void handle(ServerRequest serverRequest, ServerResponse serverResponse) throws Exception {
        final Language requestedLanguage = HttpUtils.getLanguage(serverRequest);
        if (!isPubliclyAvailable()) {
            final String bearerToken = HttpUtils.getBearerToken(serverRequest);
            if (bearerToken == null) {
                serverResponse.status(401);
                serverResponse.send("Unauthorized");
                return;
            }
            if (!bearerTokenManager.isValid(bearerToken)) {
                serverResponse.status(403);
                serverResponse.send("Forbidden");
                return;
            }
        }
        final ContentTypes contentType = HttpUtils.getContentType(serverRequest)
                .orElse(ContentTypes.APPLICATION_JSON);
        handle(serverRequest, contentType, requestedLanguage, serverResponse);
    }

}
