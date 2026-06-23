
package com.example.demo.client;

import com.example.demo.model.GithubRepo;
import com.example.demo.model.GithubBranch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GithubClientImpl implements GithubClient {
    private final RestTemplate restTemplate;
    private final String githubApiBase;

    public GithubClientImpl(RestTemplate restTemplate,
            @Value("${github.api.url:https://api.github.com}") String githubApiBase) {
        this.restTemplate = restTemplate;
        this.githubApiBase = githubApiBase;
    }

    @Override
    public List<GithubRepo> getUserRepos(String username) {
        if (username == null || username.isBlank()) {
            return List.of();
        }
        String url = String.format("%s/users/%s/repos", githubApiBase, username);
        try {
            RepoDto[] resp = restTemplate.getForObject(url, RepoDto[].class);
            if (resp == null) {
                return List.of();
            }

            return Arrays.stream(resp)
                    .map(r -> {
                        GithubRepo.Owner owner = new GithubRepo.Owner(r.owner != null ? r.owner.login : null);

                        List<GithubBranch> branches = List.of();
                        try {
                            String branchesUrl = UriComponentsBuilder.fromUriString(githubApiBase)
                                    .pathSegment("repos", owner.login(), r.name, "branches")
                                    .build()
                                    .toUriString();

                            BranchDto[] branchDtos = restTemplate.getForObject(branchesUrl, BranchDto[].class);
                            if (branchDtos != null) {
                                branches = Arrays.stream(branchDtos)
                                        .map(b -> new GithubBranch(b.name, b.commit != null ? b.commit.sha : null))
                                        .collect(Collectors.toList());
                            }
                        } catch (HttpClientErrorException.NotFound nf) {
                        } catch (Exception ignored) {
                        }

                        return new GithubRepo(r.name, owner, branches, r.fork);
                    })
                    .collect(Collectors.toList());
        } catch (HttpClientErrorException.NotFound nf) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", nf);
        } catch (HttpClientErrorException.BadRequest br) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request to upstream", br);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Upstream service error", e);
        }
    }

    private static class RepoDto {
        public String name;
        public boolean fork;
        public OwnerDto owner;
    }

    private static class OwnerDto {
        public String login;
    }

    private static class BranchDto {
        public String name;
        public CommitDto commit;
    }

    private static class CommitDto {
        public String sha;
    }

    @Override
    public List<GithubBranch> getBranches(String owner, String repo) {
        if (owner == null || owner.isBlank() || repo == null || repo.isBlank()) {
            return List.of();
        }
        try {
            String branchesUrl = UriComponentsBuilder.fromUriString(githubApiBase)
                    .pathSegment("repos", owner, repo, "branches")
                    .queryParam("per_page", "100")
                    .build()
                    .toUriString();

            BranchDto[] branchDtos = restTemplate.getForObject(branchesUrl, BranchDto[].class);
            if (branchDtos == null) {
                return List.of();
            }
            return Arrays.stream(branchDtos)
                    .map(b -> new GithubBranch(b.name, b.commit != null ? b.commit.sha : null))
                    .collect(Collectors.toList());
        } catch (HttpClientErrorException.NotFound nf) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Repository or branches not found", nf);
        } catch (HttpClientErrorException.BadRequest br) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request to upstream", br);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Upstream service error", e);
        }
    }
}
