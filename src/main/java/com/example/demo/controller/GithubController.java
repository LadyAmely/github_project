package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.response.RepositoryResponse;
import com.example.demo.service.GithubService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/github")
@RequiredArgsConstructor
public class GithubController {

    private final GithubService githubService;

    /**
     * Returns a list of non-forked repositories for the specified GitHub user.
     *
     * @param username the GitHub username
     * @return ResponseEntity containing a list of non-forked RepositoryResponse
     *         objects
     */
    @GetMapping("/users/{username}/repositories")
    public ResponseEntity<List<RepositoryResponse>> getUserRepositories(@PathVariable("username") String username) {
        List<RepositoryResponse> repositories = githubService.getUserRepositories(username);
        return ResponseEntity.ok(repositories);
    }
}
