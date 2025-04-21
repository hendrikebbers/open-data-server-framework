package com.openelements.data.server.internal;

public enum ContentTypes {
    APPLICATION_JSON("application/json"),
    APPLICATION_XML("application/xml"),
    TEXT_PLAIN("text/plain"),
    TEXT_CSV("text/csv"),
    APPLICATION_OCTET_STREAM("application/octet-stream");

    private final String contentType;

    ContentTypes(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}
