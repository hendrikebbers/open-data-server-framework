package com.openelements.data.sample.employee;

import com.openelements.data.db.AbstractEntity;
import com.openelements.data.db.FileEntity;
import com.openelements.data.db.I18nStringEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Entity
public class Employee extends AbstractEntity {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    private I18nStringEntity role;

    @Column
    private String gitHubUsername;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    private FileEntity profilePicture;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final @NonNull String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final @NonNull String lastName) {
        this.lastName = lastName;
    }

    public I18nStringEntity getRole() {
        return role;
    }

    public void setRole(I18nStringEntity role) {
        this.role = role;
    }

    public String getGitHubUsername() {
        return gitHubUsername;
    }

    public void setGitHubUsername(@Nullable final String gitHubUsername) {
        this.gitHubUsername = gitHubUsername;
    }

    @Override
    protected String calculateUUID() {
        return firstName + lastName;
    }
}
