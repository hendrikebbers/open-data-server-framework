package com.openelements.data.server.internal;

import com.openelements.data.runtime.api.Language;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.util.Objects;
import java.util.Optional;

public class HttpUtils {

    public static void setContentDispositionAsAttachment(ServerResponse response, String filename) {
        response.header("Content-Disposition", "Content-Disposition: attachment; filename=\"" + filename + "\"");
    }

    public static Optional<ContentTypes> getContentType(ServerRequest request) {
        return request.headers().stream()
                .filter(h -> h.name().equalsIgnoreCase("Content-Type"))
                .map(h -> h.value())
                .map(contentType -> {
                    for (ContentTypes type : ContentTypes.values()) {
                        if (type.getContentType().equalsIgnoreCase(contentType)) {
                            return type;
                        }
                    }
                    return null;
                })
                .findFirst();
    }

    public static Language getLanguage(ServerRequest request) {
        final String languageHeader = request.headers().stream()
                .filter(h -> h.name().equalsIgnoreCase("Accept-Language"))
                .map(h -> h.value())
                .findFirst().orElse("en");
        if (languageHeader == null) {
            return null;
        }
        String[] parts = languageHeader.split(",");
        for (String part : parts) {
            String trimmedPart = part.trim();
            if (trimmedPart.equalsIgnoreCase("de")) {
                return Language.DE;
            } else if (trimmedPart.equalsIgnoreCase("en")) {
                return Language.EN;
            }
        }
        return Language.EN;
    }

    public static String getContentLanguageString(Language language) {
        return language.toString();
    }

    public static String getBearerToken(ServerRequest serverRequest) {
        Objects.requireNonNull(serverRequest, "serverRequest cannot be null");
        return serverRequest.headers().stream()
                .filter(h -> h.name().equalsIgnoreCase("Authorization"))
                .map(h -> h.value())
                .map(h -> h.substring("Bearer ".length()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Authorization header is missing"));
    }
}
