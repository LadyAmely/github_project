package com.example.demo.dto.response;

import java.util.List;

/**
 * Response DTO representing a repository with its name, owner, and branches.
 *
 * @param repositoryName the name of the repository
 * @param ownerLogin     the login of the repository owner
 * @param branches       the list of branches in the repository
 */
public record RepositoryResponse(String repositoryName, String ownerLogin, List<BranchResponse> branches) {
}
