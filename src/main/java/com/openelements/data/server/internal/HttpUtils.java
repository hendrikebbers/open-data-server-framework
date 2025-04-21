package com.openelements.data.server.internal;

import com.openelements.data.data.Language;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import java.util.Objects;
import java.util.Optional;

public class HttpUtils {

    public static void setContentDispositionAsAttachment(ServerRequest request, String filename) {
        request.headers().add("Content-Disposition", "Content-Disposition: attachment; filename=\"" + filename + "\"");
    }

    public static Optional<ContentTypes> getContentType(ServerRequest request) {
        return request.headers().first("Content-Type").map(contentType -> {
            for (ContentTypes type : ContentTypes.values()) {
                if (type.getContentType().equalsIgnoreCase(contentType)) {
                    return type;
                }
            }
            return null;
        });
    }

    public static Language getLanguage(ServerRequest request) {
        final String languageHeader = request.headers().first("Accept-Language").orElse("en");
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

    public static void setContentType(ServerResponse res, ContentTypes contentType) {
        Objects.requireNonNull(res, "res must not be null");
        Objects.requireNonNull(contentType, "contentType must not be null");
        if (contentType == ContentTypes.APPLICATION_JSON) {
            res.headers().contentType(io.helidon.common.http.MediaType.APPLICATION_JSON);
        } else if (contentType == ContentTypes.APPLICATION_XML) {
            res.headers().contentType(io.helidon.common.http.MediaType.APPLICATION_XML);
        } else if (contentType == ContentTypes.TEXT_PLAIN) {
            res.headers().contentType(io.helidon.common.http.MediaType.TEXT_PLAIN);
        } else {
            res.headers().contentType(io.helidon.common.http.MediaType.APPLICATION_OCTET_STREAM);
        }
    }
}
