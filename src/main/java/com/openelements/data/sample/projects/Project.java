package com.openelements.data.sample.projects;

import com.openelements.data.db.AbstractEntity;
import com.openelements.data.db.FileEntity;
import com.openelements.data.db.I18nStringEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import java.util.Set;

@Entity
public class Project extends AbstractEntity {

    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    private I18nStringEntity description;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    private FileEntity svgLogoForBrightBackground;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    private FileEntity svgLogoForDarkBackground;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    private FileEntity pngLogoForBrightBackground;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    private FileEntity pngLogoForDarkBackground;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> matchingRepos;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public I18nStringEntity getDescription() {
        return description;
    }

    public void setDescription(I18nStringEntity description) {
        this.description = description;
    }

    public FileEntity getSvgLogoForBrightBackground() {
        return svgLogoForBrightBackground;
    }

    public void setSvgLogoForBrightBackground(FileEntity svgLogoForBrightBackground) {
        this.svgLogoForBrightBackground = svgLogoForBrightBackground;
    }

    public FileEntity getSvgLogoForDarkBackground() {
        return svgLogoForDarkBackground;
    }

    public void setSvgLogoForDarkBackground(FileEntity svgLogoForDarkBackground) {
        this.svgLogoForDarkBackground = svgLogoForDarkBackground;
    }

    public FileEntity getPngLogoForBrightBackground() {
        return pngLogoForBrightBackground;
    }

    public void setPngLogoForBrightBackground(FileEntity pngLogoForBrightBackground) {
        this.pngLogoForBrightBackground = pngLogoForBrightBackground;
    }

    public FileEntity getPngLogoForDarkBackground() {
        return pngLogoForDarkBackground;
    }

    public void setPngLogoForDarkBackground(FileEntity pngLogoForDarkBackground) {
        this.pngLogoForDarkBackground = pngLogoForDarkBackground;
    }

    public Set<String> getMatchingRepos() {
        return matchingRepos;
    }

    public void setMatchingRepos(Set<String> matchingRepos) {
        this.matchingRepos = matchingRepos;
    }

    @Override
    protected String calculateUUID() {
        return name;
    }
}
