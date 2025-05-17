package com.openelements.data.db;

import com.openelements.data.data.Language;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.util.Objects;

@Entity
public class TranslationMessageEntity extends EntityWithId {

    @Column(nullable = false)
    private Language language;

    @Column(nullable = false)
    private String message;

    public TranslationMessageEntity() {
    }

    public TranslationMessageEntity(Language language, String value) {
        this.language = Objects.requireNonNull(language, "language must not be null");
        this.message = value;
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
