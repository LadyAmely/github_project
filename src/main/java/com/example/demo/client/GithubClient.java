package com.example.demo.client;

import java.util.List;
import com.example.demo.model.GithubRepo;
import com.example.demo.model.GithubBranch;

public interface GithubClient {
    /**
     * Retrieves the list of GitHub repositories for the specified user.
     *
     * @param username the GitHub username whose repositories are to be fetched
     * @return a list of GithubRepo objects representing the user's repositories
     */
    List<GithubRepo> getUserRepos(String username);

    /**
     * Retrieves the list of branches for the specified repository owned by the
     * given user.
     *
     * @param owner the owner of the repository
     * @param repo  the name of the repository
     * @return a list of GithubBranch objects representing the branches of the
     *         repository
     */
    List<GithubBranch> getBranches(String owner, String repo);
}
