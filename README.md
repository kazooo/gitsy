# Gitsy

![Gitsy main branch](https://github.com/kazooo/gitsy/actions/workflows/github-actions-main.yml/badge.svg?branch=main)
[![codecov](https://codecov.io/gh/kazooo/gitsy/branch/main/graph/badge.svg?token=VEFCTMFHEO)](https://codecov.io/gh/kazooo/gitsy)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

Gitsy is a simple application that periodically retrieves and stores information
about the required GitHub organizations and their repositories. Also, Gitsy provides
REST API endpoints to access stored data. 
The application is written in [Kotlin](https://kotlinlang.org/) using [Spring Boot](https://spring.io/projects/spring-boot) framework.

## Table of contents

- [Prerequisites](#prerequisites)
- [Application parameters](#application-parameters)
- [REST API endpoints](#rest-api-endpoints)
- [Docker](#docker)
- [Dev notes](#dev-notes)
- [Known issues and limitations](#known-issues-and-limitations)
- [License](#license)
- [Author](#author)

## Prerequisites

Gitsy requires 

 - Java (v17)
 - [Gradle](https://gradle.org/) (v7.5) to build executable JAR
 - [Docker](https://www.docker.com/) (v20.10.14) to build Docker image
 - [Postgres](https://www.postgresql.org/) database (v14.5) that Gitsy uses to store data
 - [Docker compose](https://docs.docker.com/compose/) (v1.29.2) to run docker-compose configurations

to be installed on your local machine.

## Application parameters

You must configure Gitsy via specified parameters:

| parameter                  | description                                          | example                               | required | default value                          |
|----------------------------|------------------------------------------------------|---------------------------------------|----------|----------------------------------------|
| GITHUB_API_BASE_URL_SCHEME | GitHub API base endpoint scheme                      | https                                 | false    | https                                  |
| GITHUB_API_BASE_URL        | GitHub API base endpoint                             | api.github.com                        | false    | api.github.com                         |
| GITHUB_BEARER_TOKEN        | GitHub API authentication Bearer token               |                                       | false    |                                        |
| SYNC_ORGS                  | Organizations to synchronize, list separated by coma | productboard,microsoft                | false    | productboard                           |
| ENABLE_SYNC                | Enable/disable synchronization flag                  | true                                  | false    | false                                  |
| ORG_SYNC_CRON              | Synchronization cron                                 | 0 0 6 * * * (6AM every day)           | false    | 0 0 6 * * *                            |
| POSTGRES_SRC_URL           | Postgres data source URL                             | jdbc:postgresql://postgres:5432/gitsy | false    | jdbc:postgresql://localhost:5432/gitsy |
| POSTGRES_USER              | Postgres username                                    | postgres                              | false    | postgres                               |
| POSTGRES_PSWD              | Postgres user password                               | postgres                              | false    | postgres                               |
| SPRING_PROFILES_ACTIVE     | Spring Boot application profile                      | prod                                  | false    | dev                                    |
| APP_LOGGING_LEVEL          | Application logging level                            | INFO                                  | false    | INFO                                   |
| SERVER_PORT                | Server port                                          | 8080                                  | false    | 8080                                   |

Note that [GitHub REST API](https://docs.github.com/en/rest) has [request rate limit](https://docs.github.com/en/rest/overview/resources-in-the-rest-api#rate-limiting),
that can limit Gitsy functionality. To avoid those limitations please provide authentication
Bearer token via `GITHUB_BEARER_TOKEN` parameter. You can generate a personal Bearer token by following 
[the instruction](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token).

You can configure the frequency of data retrieving by synchronization cron via parameter `ORG_SYNC_CRON`.

To use Gitsy in production, set `SPRING_PROFILES_ACTIVE` to `prod` to enable production mode.

With parameters above you can start Gitsy executable jar file

```bash
./gradlew bootJar
java -DGITHUB_API_BASE_URL=api.github.com <another parameters with '-D' prefix> -jar gitsy-1.0.0.jar 
```

or using Gradle `bootRun`

```bash
./gradlew bootRun --args='--GITHUB_API_BASE_URL=api.github.com <another parameters with "--" prefix>'
```

Gitsy can accept mentioned parameters from environment variables.

## REST API endpoints

### `/api/language/{organization}`
Get the aggregation of language sets of all repositories the given organization owns.

<sup>Request example</sup>
```
http://localhost:8080/api/language/productboard
```

<sup>Response example</sup>
```json
{
  "C#": 0.02,
  "Procfile": 0.0,
  "C": 0.0,
  "Makefile": 0.0,
  "Go": 0.0,
  "HTML": 0.03,
  "Svelte": 0.0,
  "TypeScript": 0.48,
  "Shell": 0.0,
  "JavaScript": 0.2,
  "Lua": 0.0,
  "Ruby": 0.14,
  "Python": 0.0,
  "PowerShell": 0.0,
  "Java": 0.12,
  "CSS": 0.0,
  "C++": 0.0,
  "Vue": 0.0,
  "Logos": 0.0,
  "Dockerfile": 0.0,
  "CoffeeScript": 0.0,
  "Batchfile": 0.0,
  "Gherkin": 0.0,
  "ASP.NET": 0.0,
  "Roff": 0.0,
  "Nix": 0.0,
  "TSQL": 0.0
}
```

### `/api/language/{organization}/{repository}`
Get the history of language set changes for the given repository.

<sup>Request example</sup>
```
http://localhost:8080/api/language/productboard/locatorjs
```

<sup>Response example</sup>
```json
[
  {
    "languageMap": {
      "TypeScript": 0.89,
      "JavaScript": 0.05,
      "CSS": 0.04,
      "HTML": 0.01,
      "Svelte": 0.01,
      "Vue": 0
    },
    "timestamp": "2022-10-10T12:31:20.180Z"
  },
  {
    "languageMap": {
      "TypeScript": 0.89,
      "JavaScript": 0.05,
      "CSS": 0.04,
      "HTML": 0.01,
      "Svelte": 0.01,
      "Vue": 0
    },
    "timestamp": "2022-10-10T12:32:17.528Z"
  }
]
```

### `/api/language/{organization}/{repository}/latest`
Get the latest language set for the given repository.

<sup>Request example</sup>
```
http://localhost:8080/api/language/productboard/locatorjs/latest
```

<sup>Response example</sup>
```json
{
    "TypeScript": 0.89,
    "JavaScript": 0.05,
    "CSS": 0.04,
    "HTML": 0.01,
    "Svelte": 0.01,
    "Vue": 0
}
```

Gitsy endpoints are also described by [Swagger(OpenAPI)](https://swagger.io/).
You can find detailed REST API documentation on [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
after running the application with the command `bootRun` described in the section above.

## Docker

Gitsy could be run in Docker container.
You can normally dockerize Gitsy using built-in Spring Boot command:

```bash
./gradlew bootBuildImage
```

Also, you can use Docker compose to quickly configure and launch Gitsy.
You can find example of production ready docker-compose file [here](https://github.com/kazooo/gitsy/blob/main/docker/docker-compose-prod.yml).

## Dev notes

For developing there is a special profile `dev` which provides embedded [H2 database](https://www.h2database.com/html/main.html)
for development and testing. 

Gitsy has also 
 - [GitHub Actions CI/CD](https://github.com/features/actions) connected, detailed configuration is [here](https://github.com/kazooo/gitsy/blob/main/.github/workflows/github-actions-main.yml)
 - Swagger (OpenAPI) that describes REST API endpoints in detail
 - [Detekt](https://detekt.dev/) for syntax and formatting analysis
 - [Kover](https://lengrand.fr/kover-code-coverage-plugin-for-kotlin/) for code coverage verification and reporting

## Known issues and limitations

 - currently Gitsy uses authentication Bearer token as a string, which is wrong from a security point of view
 - Gitsy doesn't properly handle the case when it reaches request rate limit
 - there are no configurable sorting for language set history records
 - Gitsy doesn't handle cron with high frequency, so synchronizations may run in parallel which may lead to data inconsistencies

## License

[GPL v3](https://www.gnu.org/licenses/gpl-3.0)

## Author

[Aleksei Ermak](https://github.com/kazooo)
