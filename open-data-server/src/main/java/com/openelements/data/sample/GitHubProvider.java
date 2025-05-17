package com.openelements.data.sample;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitHubProvider {

    private final static Logger log = LoggerFactory.getLogger(GitHubProvider.class);

    private final static GitHubProvider instance = new GitHubProvider();

    private final GitHub github;

    private GitHubProvider() {
        final Dotenv dotenv = new DotenvBuilder()
                .ignoreIfMissing()
                .ignoreIfMalformed()
                .load();
        final String gitHubToken = dotenv.get("GITHUB_TOKEN");
        try {
            github = new GitHubBuilder().withOAuthToken(gitHubToken).build();
        } catch (Exception e) {
            log.error("Failed to create GitHub instance", e);
            throw new RuntimeException("Failed to create GitHub instance", e);
        }
    }

    public static GitHubProvider getInstance() {
        return instance;
    }

    public GitHub getGithub() {
        return github;
    }
}
