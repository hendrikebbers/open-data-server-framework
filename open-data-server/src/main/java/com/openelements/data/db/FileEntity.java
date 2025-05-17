package com.openelements.data.db;

import com.openelements.data.server.internal.ContentTypes;
import com.openelements.data.server.internal.HashUtil;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;

@Entity
public class FileEntity extends AbstractEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated
    @Column(nullable = false)
    private ContentTypes contentType;

    @Lob
    @Basic(fetch = FetchType.LAZY, optional = false)
    private byte[] content;

    @Column(nullable = false)
    private int contentSize;

    @Column(nullable = false)
    private byte[] hash;

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
        if (content != null) {
            contentSize = content.length;
            hash = HashUtil.calculateHash(content);
        } else {
            contentSize = 0;
            hash = null;
        }
    }

    public int getContentSize() {
        return contentSize;
    }

    public byte[] getHash() {
        return hash;
    }

    public ContentTypes getContentType() {
        return contentType;
    }

    public void setContentType(ContentTypes contentType) {
        this.contentType = contentType;
    }

    @Override
    protected String calculateUUID() {
        return HashUtil.bytesToHex(hash);
    }
}
