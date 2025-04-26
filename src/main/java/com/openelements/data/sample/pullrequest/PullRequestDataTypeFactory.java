package com.openelements.data.sample.pullrequest;

import com.openelements.data.data.AttributeType;
import com.openelements.data.data.DataAttribute;
import com.openelements.data.data.DataType;
import com.openelements.data.data.I18nString;
import java.time.ZonedDateTime;
import java.util.List;

public class PullRequestDataTypeFactory {

    public static DataType<PullRequest> createDataType() {
        final DataAttribute<PullRequest, String> orgAttribute = new DataAttribute<>("org",
                I18nString.of("GitHub org"),
                I18nString.of("The GitHub org"),
                AttributeType.STRING,
                PullRequest::getOrg);
        final DataAttribute<PullRequest, String> repositoryAttribute = new DataAttribute<>("repository",
                I18nString.of("GitHub repo"),
                I18nString.of("The GitHub repo"),
                AttributeType.STRING,
                PullRequest::getRepository);
        final DataAttribute<PullRequest, Integer> gitHubIdAttribute = new DataAttribute<>("gitHubId",
                I18nString.of("GitHub ID"),
                I18nString.of("The GitHub ID"),
                AttributeType.NUMBER,
                PullRequest::getGitHubId);
        final DataAttribute<PullRequest, String> titleAttribute = new DataAttribute<>("title",
                I18nString.of("Title"),
                I18nString.of("The title of the PR"),
                AttributeType.STRING,
                PullRequest::getTitle);
        final DataAttribute<PullRequest, Boolean> openAttribute = new DataAttribute<>("open",
                I18nString.of("Open"),
                I18nString.of("Is the PR open?"),
                AttributeType.BOOLEAN,
                PullRequest::isOpen);
        final DataAttribute<PullRequest, Boolean> mergedAttribute = new DataAttribute<>("merged",
                I18nString.of("Merged"),
                I18nString.of("Is the PR merged?"),
                AttributeType.BOOLEAN,
                PullRequest::isMerged);
        final DataAttribute<PullRequest, Boolean> draftAttribute = new DataAttribute<>("draft",
                I18nString.of("Draft"),
                I18nString.of("Is the PR a draft?"),
                AttributeType.BOOLEAN,
                PullRequest::isDraft);
        final DataAttribute<PullRequest, String> authorAttribute = new DataAttribute<>("author",
                I18nString.of("Author"),
                I18nString.of("The author of the PR"),
                AttributeType.STRING,
                PullRequest::getAuthor);
        final DataAttribute<PullRequest, ZonedDateTime> createdAtInGitHubAttribute = new DataAttribute<>(
                "createdAtInGitHub",
                I18nString.of("Created at in GitHub"),
                I18nString.of("The date the PR was created in GitHub"),
                AttributeType.DATE_TIME,
                PullRequest::getCreatedAtInGitHub);
        final DataAttribute<PullRequest, ZonedDateTime> lastUpdateInGitHubAttribute = new DataAttribute<>(
                "lastUpdateInGitHub",
                I18nString.of("Last update in GitHub"),
                I18nString.of("The date the PR was last updated in GitHub"),
                AttributeType.DATE_TIME,
                PullRequest::getLastUpdateInGitHub);
        return new DataType<>("pullRequest", "A GitHub pull request",
                PullRequest.class,
                List.of(orgAttribute, repositoryAttribute, gitHubIdAttribute, titleAttribute, openAttribute,
                        mergedAttribute, draftAttribute, authorAttribute, createdAtInGitHubAttribute,
                        lastUpdateInGitHubAttribute));
    }
}
