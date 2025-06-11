package com.openelements.recordstore.server.internal.handler;

import com.openelements.data.runtime.api.Language;
import com.openelements.data.runtime.data.DataType;
import com.openelements.recordstore.server.internal.ContentTypes;
import com.openelements.recordstore.server.internal.HttpUtils;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetCountHandler<E extends Record, D extends DataType<E>> extends AbstractHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final DataHandler<E, D> dataHandler;

    public GetCountHandler(DataHandler<E, D> dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    protected boolean isPubliclyAvailable() {
        return dataHandler.isPubliclyAvailable();
    }

    @Override
    protected void handle(ServerRequest serverRequest, ContentTypes requestedContentType,
            Language requestedLanguage, ServerResponse serverResponse) {
        if (requestedContentType == ContentTypes.APPLICATION_JSON) {
            try {
                serverResponse.headers().contentType(ContentTypes.APPLICATION_JSON);
                serverResponse.header("Content-Language", HttpUtils.getContentLanguageString(requestedLanguage));
                serverResponse.send(Long.valueOf(dataHandler.getCount()).toString());
            } catch (Exception e) {
                serverResponse.status(500);
                serverResponse.send("Error processing request: " + e.getMessage());
                log.error("Error processing request", e);
            }
        } else {
            serverResponse.send("Unsupported content type: " + requestedContentType.getContentType());
            log.error("Unsupported content type requested: {}", requestedContentType.getContentType());
        }
    }
}
