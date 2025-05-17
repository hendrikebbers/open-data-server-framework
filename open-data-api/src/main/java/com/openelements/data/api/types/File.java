package com.openelements.data.api.types;

public record File(String name, byte[] content) {
    public File {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("File name must not be null or empty");
        }
        if (content == null) {
            throw new IllegalArgumentException("File content must not be null");
        }
    }
}
