
package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.client.GithubClient;
import com.example.demo.dto.response.BranchResponse;
import com.example.demo.dto.response.RepositoryResponse;
import com.example.demo.model.GithubBranch;
import com.example.demo.model.GithubRepo;

@Service
public class GithubService {

    private final GithubClient githubClient;

    public GithubService(GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    public List<RepositoryResponse> getUserRepositories(String username) {
        return githubClient.getUserRepos(username).stream()
                .filter(repo -> !repo.fork())
                .map((GithubRepo repo) -> new RepositoryResponse(
                        repo.repositoryName(),
                        repo.owner() != null ? repo.owner().login() : null,
                        repo.branches() == null
                            ? List.of()
                            : repo.branches().stream()
                                .map((GithubBranch b) -> new BranchResponse(b.name(), b.commitSHA()))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }
    public List<BranchResponse> getBranches(String owner, String repo) {
        return githubClient.getBranches(owner, repo).stream()
                .map(b -> new BranchResponse(b.name(), b.commitSHA()))
                .collect(Collectors.toList());
    }
}