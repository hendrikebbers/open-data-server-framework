package com.openelements.data.db;

import com.openelements.data.data.Language;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.Objects;
import java.util.UUID;

@Entity
public class TranslationMessage {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private Language language;

    @Column(nullable = false)
    private String message;

    public TranslationMessage() {
    }

    public TranslationMessage(Language language, String value) {
        this.language = Objects.requireNonNull(language, "language must not be null");
        this.message = value;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }
}
