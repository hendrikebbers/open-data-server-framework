package com.openelements.data.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class FileEntity extends EntityWithId {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private byte[] content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
