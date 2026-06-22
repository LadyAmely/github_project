package com.example.demo.model;

/**
 * Represents a GitHub branch with its name and the latest commit SHA.
 *
 * @param name the name of the branch
 * @param commitSha the SHA of the latest commit on the branch
 */
public record GithubBranch(
                String name,
                String commitSHA) {
}