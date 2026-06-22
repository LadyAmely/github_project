package com.example.demo.model;

import java.util.List;

/**
 * Represents a GitHub repository with its name, owner, and fork status.
 *
 * @param repositoryName the name of the repository
 * @param owner          the owner of the repository
 * @param fork           whether the repository is a fork
 */
 public record GithubRepo(
        String repositoryName,
        Owner owner,
        List<GithubBranch> branches,
        boolean fork) {
    /**
     * Represents the owner of a GitHub repository.
     *
     * @param login the owner's login name
     */
    public static record Owner(String login) {
    }
}
