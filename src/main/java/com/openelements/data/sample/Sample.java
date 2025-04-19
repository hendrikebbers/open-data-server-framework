package com.openelements.data.sample;

import com.openelements.data.data.AttributeType;
import com.openelements.data.data.DataAttribute;
import com.openelements.data.data.DataProvider;
import com.openelements.data.data.DataType;
import com.openelements.data.data.I18nString;
import com.openelements.data.db.DbHandler;
import com.openelements.data.provider.ProviderHandler;
import com.openelements.data.sample.employee.Employee;
import com.openelements.data.sample.employee.EmployeeEntityMapper;
import com.openelements.data.sample.employee.EmployeeProvider;
import com.openelements.data.sample.pullrequest.PullRequest;
import com.openelements.data.sample.pullrequest.PullRequestEntityMapper;
import com.openelements.data.sample.pullrequest.PullRequestProvider;
import com.openelements.data.server.DataEndpointMetadata;
import com.openelements.data.server.DataServer;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public class Sample {

    public static void main(String[] args) {
        final DbHandler dbHandler = new DbHandler("my-unit");
        final ProviderHandler providerHandler = new ProviderHandler(dbHandler.createRepository());
        providerHandler.add(Employee.class, new EmployeeProvider(), new EmployeeEntityMapper());
        providerHandler.add(PullRequest.class, new PullRequestProvider(), new PullRequestEntityMapper());
        DataServer dataServer = new DataServer(8080, getEndpoints(dbHandler));
        dataServer.start();
    }

    private static Set<DataEndpointMetadata<?>> getEndpoints(DbHandler dbHandler) {
        return Set.of(getEmployeeEndpointMetadata(dbHandler), getPullRequestEndpointMetadata(dbHandler));
    }

    private static DataEndpointMetadata<Employee> getEmployeeEndpointMetadata(DbHandler dbHandler) {
        final DataAttribute<Employee, String> firstNameAttribute = new DataAttribute<>("firstName", "The first name",
                AttributeType.STRING,
                Employee::getFirstName);
        final DataAttribute<Employee, String> lastNameAttribute = new DataAttribute<>("lastName", "The last name",
                AttributeType.STRING,
                Employee::getLastName);
        final DataAttribute<Employee, I18nString> roleAttribute = new DataAttribute<>("role", "The role",
                AttributeType.I18N_STRING,
                Employee::getRole);
        final DataType<Employee> employeeType = new DataType<>("employee", "An employee", Employee.class,
                List.of(firstNameAttribute, lastNameAttribute, roleAttribute));
        final DataProvider<Employee> dataProvider = dbHandler.createDataProvider(Employee.class);
        return new DataEndpointMetadata<>("employees", employeeType,
                dataProvider);
    }

    private static DataEndpointMetadata<PullRequest> getPullRequestEndpointMetadata(DbHandler dbHandler) {
        final DataAttribute<PullRequest, String> orgAttribute = new DataAttribute<>("org", "The GitHub org",
                AttributeType.STRING,
                PullRequest::getOrg);
        final DataAttribute<PullRequest, String> repositoryAttribute = new DataAttribute<>("repository",
                "The GitHub repo",
                AttributeType.STRING,
                PullRequest::getRepository);
        final DataAttribute<PullRequest, Integer> gitHubIdAttribute = new DataAttribute<>("gitHubId", "The GitHub ID",
                AttributeType.NUMBER,
                PullRequest::getGitHubId);
        final DataAttribute<PullRequest, String> titleAttribute = new DataAttribute<>("title", "The title of the PR",
                AttributeType.STRING,
                PullRequest::getTitle);
        final DataAttribute<PullRequest, Boolean> openAttribute = new DataAttribute<>("open", "Is the PR open?",
                AttributeType.BOOLEAN,
                PullRequest::isOpen);
        final DataAttribute<PullRequest, Boolean> mergedAttribute = new DataAttribute<>("merged", "Is the PR merged?",
                AttributeType.BOOLEAN,
                PullRequest::isMerged);
        final DataAttribute<PullRequest, Boolean> draftAttribute = new DataAttribute<>("draft", "Is the PR a draft?",
                AttributeType.BOOLEAN,
                PullRequest::isDraft);
        final DataAttribute<PullRequest, String> authorAttribute = new DataAttribute<>("author", "The author of the PR",
                AttributeType.STRING,
                PullRequest::getAuthor);
        final DataAttribute<PullRequest, ZonedDateTime> createdAtInGitHubAttribute = new DataAttribute<>(
                "createdAtInGitHub",
                "The date the PR was created in GitHub",
                AttributeType.DATE_TIME,
                PullRequest::getCreatedAtInGitHub);
        final DataAttribute<PullRequest, ZonedDateTime> lastUpdateInGitHubAttribute = new DataAttribute<>(
                "lastUpdateInGitHub",
                "The date the PR was last updated in GitHub",
                AttributeType.DATE_TIME,
                PullRequest::getLastUpdateInGitHub);
        final DataType<PullRequest> pullRequestType = new DataType<>("pullRequest", "A GitHub pull request",
                PullRequest.class,
                List.of(orgAttribute, repositoryAttribute, gitHubIdAttribute, titleAttribute, openAttribute,
                        mergedAttribute, draftAttribute, authorAttribute, createdAtInGitHubAttribute,
                        lastUpdateInGitHubAttribute));
        final DataProvider<PullRequest> dataProvider = dbHandler.createDataProvider(PullRequest.class);
        return new DataEndpointMetadata<>("pullRequests", pullRequestType,
                dataProvider);
    }
}
