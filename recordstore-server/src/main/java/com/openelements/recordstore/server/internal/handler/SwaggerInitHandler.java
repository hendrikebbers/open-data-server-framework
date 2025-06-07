package com.openelements.recordstore.server.internal.handler;


import com.openelements.recordstore.server.internal.ContentTypes;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

public class SwaggerInitHandler implements Handler {

    @Override
    public void handle(ServerRequest req, ServerResponse res) throws Exception {
        String apiUrl = "/openapi";
        String script = """
                window.onload = () => {
                  window.ui = SwaggerUIBundle({
                    url: "%s",
                    dom_id: '#swagger-ui',
                    presets: [
                      SwaggerUIBundle.presets.apis,
                      SwaggerUIStandalonePreset
                    ],
                    layout: "BaseLayout"
                  });
                };
                """.formatted(apiUrl);
        res.headers().contentType(ContentTypes.APPLICATION_JSON);
        res.send(script);
    }
}
