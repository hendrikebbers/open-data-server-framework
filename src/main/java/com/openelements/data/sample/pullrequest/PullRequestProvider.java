package com.openelements.data.sample.pullrequest;

import com.openelements.data.provider.DataProvider;
import java.time.ZonedDateTime;
import java.util.Set;

public class PullRequestProvider implements DataProvider<PullRequest> {

    @Override
    public Set<PullRequest> loadUpdateData(ZonedDateTime lastUpdate) {
        return Set.of();
    }
}
