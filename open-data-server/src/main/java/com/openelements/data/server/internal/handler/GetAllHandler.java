package com.openelements.data.server.internal.handler;

import com.google.gson.JsonArray;
import com.openelements.data.api.data.Language;
import com.openelements.data.runtime.data.DataType;
import com.openelements.data.server.internal.ContentTypes;
import com.openelements.data.server.internal.HttpUtils;
import com.openelements.data.server.internal.JsonFactory;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAllHandler<E extends Record, D extends DataType<E>> extends AbstractHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final DataHandler<E, D> dataHandler;

    private final JsonFactory jsonFactory;

    public GetAllHandler(DataHandler<E, D> dataHandler) {
        this.dataHandler = dataHandler;
        this.jsonFactory = new JsonFactory();
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
                final JsonArray result = new JsonArray();
                final List<E> data = dataHandler.getAll();
                for (E entry : data) {
                    result.add(jsonFactory.createJsonObject(entry, dataHandler.getDataClass(), requestedLanguage));
                }
                serverResponse.headers().contentType(ContentTypes.APPLICATION_JSON);
                serverResponse.header("Content-Language", HttpUtils.getContentLanguageString(requestedLanguage));
                serverResponse.send(result.toString());
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
