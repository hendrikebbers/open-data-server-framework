package com.openelements.data.data.internal.db;

import com.openelements.data.db.AbstractEntity;
import com.openelements.data.db.I18nStringEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class DataTypeEntity extends AbstractEntity {

    @Column(nullable = false)
    private String dataIdentifier;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    private I18nStringEntity name;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    private I18nStringEntity description;

    public String getDataIdentifier() {
        return dataIdentifier;
    }

    public void setDataIdentifier(String dataIdentifier) {
        this.dataIdentifier = dataIdentifier;
    }

    public I18nStringEntity getName() {
        return name;
    }

    public void setName(I18nStringEntity name) {
        this.name = name;
    }

    public I18nStringEntity getDescription() {
        return description;
    }

    public void setDescription(I18nStringEntity description) {
        this.description = description;
    }

    @Override
    protected String calculateUUID() {
        return dataIdentifier;
    }
}
