package com.openelements.data.server.internal;

import com.openelements.data.data.Language;
import io.helidon.webserver.ServerRequest;

public class HttpUtils {

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
}
