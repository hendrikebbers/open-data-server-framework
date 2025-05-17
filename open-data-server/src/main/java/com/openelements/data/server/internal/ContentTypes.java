package com.openelements.data.server.internal;

import io.helidon.common.media.type.MediaType;

public enum ContentTypes implements MediaType {
    APPLICATION_JSON("application", "json"),
    APPLICATION_XML("application", "xml"),
    TEXT_PLAIN("text", "plain"),
    TEXT_CSV("text", "csv"),
    APPLICATION_OCTET_STREAM("application", "octet-stream"),
    JPEG("image", "jpeg");

    private final String type;

    private final String subtype;

    ContentTypes(String type, String subtype) {
        this.type = type;
        this.subtype = subtype;
    }

    public String getContentType() {
        return text();
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String subtype() {
        return subtype;
    }

    @Override
    public String text() {
        return type + "/" + subtype;
    }
}
