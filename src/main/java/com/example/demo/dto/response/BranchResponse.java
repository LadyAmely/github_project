package com.example.demo.dto.response;

/**
 * Represents a response containing branch information.
 *
 * @param name the name of the branch
 * @param commitSha the SHA of the latest commit on the branch
 */
public record BranchResponse(
        String name,
        String commitSha) {
}
