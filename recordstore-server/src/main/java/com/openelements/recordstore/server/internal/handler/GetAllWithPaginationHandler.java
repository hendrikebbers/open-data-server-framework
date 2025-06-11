package com.openelements.recordstore.server.internal.handler;

import com.openelements.data.runtime.api.Language;
import com.openelements.data.runtime.api.Page;
import com.openelements.data.runtime.data.DataType;
import com.openelements.recordstore.server.internal.ContentTypes;
import com.openelements.recordstore.server.internal.HttpUtils;
import com.openelements.recordstore.server.internal.gson.JsonFactory;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAllWithPaginationHandler<E extends Record, D extends DataType<E>> extends AbstractHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final DataHandler<E, D> dataHandler;

    private final JsonFactory jsonFactory;

    public GetAllWithPaginationHandler(DataHandler<E, D> dataHandler, JsonFactory jsonFactory) {
        this.dataHandler = dataHandler;
        this.jsonFactory = jsonFactory;
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
                final int page = serverRequest.query().first("page").map(Integer::parseInt).orElse(1);
                final int pageSize = serverRequest.query().first("pageSize").map(Integer::parseInt).orElse(10);
                final Page<E> data = dataHandler.getPage(page, pageSize);
                serverResponse.headers().contentType(ContentTypes.APPLICATION_JSON);
                serverResponse.header("Content-Language", HttpUtils.getContentLanguageString(requestedLanguage));
                serverResponse.send(jsonFactory.createJsonObject(data, requestedLanguage).toString());
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
