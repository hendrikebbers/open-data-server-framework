package com.openelements.data.server;

import com.openelements.data.data.Language;
import io.helidon.webserver.ServerRequest;

public class HttpUtils {

    public static Language getLanguage(ServerRequest request) {
        final String languageHeader = request.headers().first("Accept-Language").orElse("en");
        return Language.fromString(languageHeader);
    }

    public static String getContentLanguageString(Language language) {
        return language.toString();
    }
}
