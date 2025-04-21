package com.openelements.data.server.internal.handler;

import io.helidon.webserver.Handler;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;

public class SwaggerInitHandler implements Handler {

    @Override
    public void accept(ServerRequest req, ServerResponse res) {
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
        res.headers().add("Content-Type", "application/javascript");
        res.send(script);
    }
}
