package com.openelements.data.sample.pullrequest;

import com.openelements.data.db.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.ZonedDateTime;
import org.jspecify.annotations.NonNull;

@Entity
public class PullRequest extends AbstractEntity {

    @Column(nullable = false)
    private String org;

    @Column(nullable = false)
    private String repository;

    @Column(nullable = false)
    private long gitHubId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean open;

    @Column(nullable = false)
    private boolean merged;

    @Column(nullable = false)
    private boolean draft;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private ZonedDateTime createdAtInGitHub;

    @Column(nullable = false)
    private ZonedDateTime lastUpdateInGitHub;

    public String getOrg() {
        return org;
    }

    public void setOrg(@NonNull final String org) {
        this.org = org;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(@NonNull final String repository) {
        this.repository = repository;
    }

    public long getGitHubId() {
        return gitHubId;
    }

    public void setGitHubId(@NonNull final long gitHubId) {
        this.gitHubId = gitHubId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull final String title) {
        this.title = title;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(@NonNull final boolean open) {
        this.open = open;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(@NonNull final String author) {
        this.author = author;
    }

    public boolean isMerged() {
        return merged;
    }

    public void setMerged(@NonNull final boolean merged) {
        this.merged = merged;
    }

    public ZonedDateTime getCreatedAtInGitHub() {
        return createdAtInGitHub;
    }

    public void setCreatedAtInGitHub(@NonNull final ZonedDateTime createdAtInGitHub) {
        this.createdAtInGitHub = createdAtInGitHub;
    }

    public ZonedDateTime getLastUpdateInGitHub() {
        return lastUpdateInGitHub;
    }

    public void setLastUpdateInGitHub(@NonNull final ZonedDateTime lastUpdateInGitHub) {
        this.lastUpdateInGitHub = lastUpdateInGitHub;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }
}
