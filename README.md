# demo

Simple instructions for running, testing and calling endpoints. This project uses Java 25 (configured via Gradle toolchain).

Prerequisites
- JDK 25 (Gradle toolchain configured; system JDK not required)
- Gradle wrapper included: ./gradlew
- macOS commands shown below

Run the application (development)
- Start with Gradle:
  ./gradlew bootRun
- Default port: 8080

Build and run the jar
- Build:
  ./gradlew bootJar
- Run:
  java -jar build/libs/demo-0.0.1-SNAPSHOT.jar

Run tests
- Run all tests:
  ./gradlew test
- Run a single integration test (example):
  ./gradlew test --tests com.example.demo.integration.GithubIntegrationTest

Call the endpoints
- Example endpoint (GET repositories for a user):
  GET http://localhost:8080/api/v1/github/users/{username}/repositories
- curl example:
  curl -sS http://localhost:8080/api/v1/github/users/octocat/repositories
