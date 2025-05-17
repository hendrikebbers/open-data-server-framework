package com.openelements.data.sample.pullrequest;

import com.openelements.data.provider.DataProviderContext;
import com.openelements.data.provider.EntityUpdatesProvider;
import com.openelements.data.sample.GitHubProvider;
import com.openelements.data.sample.employee.Employee;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedSearchIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PullRequestProvider implements EntityUpdatesProvider<PullRequest> {

    private final static Logger log = LoggerFactory.getLogger(PullRequestProvider.class);

    private final static ZonedDateTime MIN_TIME = ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);

    private final Map<String, ZonedDateTime> lastUpdateByUser = new ConcurrentHashMap<>();

    @Override
    public Set<PullRequest> loadUpdatedData(DataProviderContext context) {
        final List<String> usernames = getAllGitHubUsers(context);
        final List<CompletableFuture<List<PullRequest>>> futures = usernames.stream()
                .map(username -> {
                    final CompletableFuture<List<PullRequest>> future = new CompletableFuture<>();
                    context.executor().execute(() -> {
                        try {
                            final ZonedDateTime lastUpdate = Optional.of(context.lastUpdate())
                                    .filter(date -> date.isAfter(MIN_TIME))
                                    .orElse(MIN_TIME);
                            final List<PullRequest> availablePullRequestsForAuthor = getAvailablePullRequestsForAuthor(
                                    username, lastUpdate);
                            log.info("Finished processing pull requests for author {}. Found {} pull requests",
                                    username,
                                    availablePullRequestsForAuthor.size());
                            future.complete(availablePullRequestsForAuthor);
                        } catch (Exception e) {
                            future.completeExceptionally(e);
                        }
                    });
                    return future;
                }).toList();
        return futures.stream()
                .map(future -> {
                    try {
                        return future.get(1, TimeUnit.HOURS);
                    } catch (Exception e) {
                        log.error("Error processing pull requests", e);
                        return List.<PullRequest>of();
                    }
                })
                .flatMap(List::stream)
                .collect(Collectors.toUnmodifiableSet());
    }

    private static List<String> getAllGitHubUsers(DataProviderContext context) {
        return context.repositoryFactory()
                .createRepository(Employee.class)
                .getAll()
                .stream()
                .filter(employee -> employee.getGitHubUsername() != null)
                .map(Employee::getGitHubUsername)
                .toList();
    }

    public List<PullRequest> getAvailablePullRequestsForAuthor(@NonNull final String author,
            @NonNull final ZonedDateTime lastUpdate) {
        Objects.requireNonNull(author, "author cannot be null");
        Objects.requireNonNull(lastUpdate, "lastUpdate cannot be null");
        final List<PullRequest> result = new ArrayList<>();
        try {
            final ZonedDateTime updatedAfter = lastUpdateByUser.getOrDefault(author, MIN_TIME);
            log.info("Collecting pull requests for author {} updated after {}", author, updatedAfter);
            final GitHub github = GitHubProvider.getInstance().getGithub();
            final GHUser user = github.getUser(author);
            final PagedSearchIterable<GHPullRequest> list = github.searchPullRequests()
                    .author(user)
                    .updatedAfter(updatedAfter.toLocalDate(), false)
                    .list().withPageSize(100);
            for (GHPullRequest pullRequest : list) {
                log.info("Processing next pull request for author {}", author);
                PullRequest pr = convert(pullRequest, author);
                result.add(pr);
                log.info("Processed pull requests '{}' for author '{}' ({} total)", pr.getUuid(), author,
                        result.size());
            }
            lastUpdateByUser.put(author, lastUpdate);
        } catch (final Exception e) {
            throw new RuntimeException("Error in progressing pull requests for author " + author, e);
        }
        return result;
    }

    private static PullRequest convert(GHPullRequest pullRequest, String author) throws Exception {
        final String org = pullRequest.getRepository().getOwnerName();
        final String repo = pullRequest.getRepository().getName();
        final int gitHubId = pullRequest.getNumber();
        final String uuid = org + "/" + repo + "/" + gitHubId;
        final ZonedDateTime createdAt = ZonedDateTime.ofInstant(pullRequest.getCreatedAt().toInstant(),
                ZoneOffset.UTC);
        final ZonedDateTime lastUpdateInGitHub = Optional.of(pullRequest.getUpdatedAt())
                .map(date -> ZonedDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC))
                .orElse(createdAt);
        final String title = pullRequest.getTitle();
        final boolean open = pullRequest.getState().equals(GHIssueState.OPEN);
        final boolean merged = pullRequest.isMerged();
        final boolean draft = pullRequest.isDraft();

        PullRequest pr = new PullRequest();
        pr.setUuid(uuid);
        pr.setOrg(org);
        pr.setRepository(repo);
        pr.setGitHubId(gitHubId);
        pr.setTitle(title);
        pr.setCreatedAtInGitHub(createdAt);
        pr.setLastUpdateInGitHub(lastUpdateInGitHub);
        pr.setOpen(open);
        pr.setDraft(draft);
        pr.setMerged(merged);
        pr.setAuthor(author);
        return pr;
    }
}
