package com.openelements.data.sample.pullrequest;

import com.openelements.data.db.EntityMapper;

public class PullRequestEntityMapper implements EntityMapper<PullRequest> {

    @Override
    public PullRequest updateEntity(PullRequest updated, PullRequest toUpdate) {
        toUpdate.setOrg(updated.getOrg());
        toUpdate.setRepository(updated.getRepository());
        toUpdate.setGitHubId(updated.getGitHubId());
        toUpdate.setTitle(updated.getTitle());
        toUpdate.setOpen(updated.isOpen());
        toUpdate.setMerged(updated.isMerged());
        toUpdate.setAuthor(updated.getAuthor());
        toUpdate.setCreatedAtInGitHub(updated.getCreatedAtInGitHub());
        toUpdate.setLastUpdateInGitHub(updated.getLastUpdateInGitHub());
        return toUpdate;
    }
}
