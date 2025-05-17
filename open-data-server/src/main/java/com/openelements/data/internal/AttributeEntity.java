package com.openelements.data.internal;

import com.openelements.data.db.AbstractEntity;
import com.openelements.data.db.I18nStringEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class AttributeEntity extends AbstractEntity {

    @Column(nullable = false)
    private String dataIdentifier;

    @Column(nullable = false)
    private String attributeIdentifier;

    @Column(nullable = false)
    private String attributeType;

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

    public String getAttributeIdentifier() {
        return attributeIdentifier;
    }

    public void setAttributeIdentifier(String attributeIdentifier) {
        this.attributeIdentifier = attributeIdentifier;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
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
        return dataIdentifier + attributeIdentifier;
    }
}
