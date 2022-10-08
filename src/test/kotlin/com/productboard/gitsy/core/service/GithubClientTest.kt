package com.productboard.gitsy.core.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.productboard.gitsy.core.GithubApiEndpoint
import com.productboard.gitsy.core.buildApiUri
import com.productboard.gitsy.core.dto.GithubOrganizationDto
import com.productboard.gitsy.core.dto.GithubRepositoryDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

private object GithubClientTestConstants {
    const val DUMMY_GITHUB_API_BASE_URL_SCHEME = "https"
    const val DUMMY_GITHUB_API_BASE_URL = "base-api-url.com"
    const val DUMMY_GITHUB_ORGANIZATION = "organization"
    const val DUMMY_GITHUB_ORGANIZATION_REPOS = 42
    const val DUMMY_GITHUB_REPOSITORY_NAME = "repository"
    val DUMMY_REPO_LANGUAGES = mapOf("Kotlin" to 12L, "Java" to 10L)
}

@RestClientTest
@AutoConfigureWebClient(registerRestTemplate = true)
@ComponentScan("com.productboard.*")
@TestPropertySource(
    properties = [
        "gitsy.githubApiBaseUrlScheme=${GithubClientTestConstants.DUMMY_GITHUB_API_BASE_URL_SCHEME}",
        "gitsy.githubApiBaseUrl=${GithubClientTestConstants.DUMMY_GITHUB_API_BASE_URL}",
    ]
)
@ActiveProfiles("test")
class GithubClientTest(
    @Autowired val server: MockRestServiceServer,
    @Autowired val mapper: ObjectMapper,
    @Autowired val githubClient: GithubClient,
) {

    @Test
    fun returnsOrganizationWhenSuccessful() {
        val uri = buildApiUri(
            baseApiUrlScheme = GithubClientTestConstants.DUMMY_GITHUB_API_BASE_URL_SCHEME,
            baseApiUrl = GithubClientTestConstants.DUMMY_GITHUB_API_BASE_URL,
            relativeUrl = GithubApiEndpoint.GET_ORGANIZATION,
            uriVariables = mapOf("organization" to GithubClientTestConstants.DUMMY_GITHUB_ORGANIZATION),
        )
        val expected = GithubOrganizationDto(
            name = GithubClientTestConstants.DUMMY_GITHUB_ORGANIZATION,
            publicRepos = GithubClientTestConstants.DUMMY_GITHUB_ORGANIZATION_REPOS,
        )

        server
            .expect(requestTo(uri.toString()))
            .andRespond(withSuccess(mapper.writeValueAsString(expected), MediaType.APPLICATION_JSON))

        val actual = githubClient.getOrganization(GithubClientTestConstants.DUMMY_GITHUB_ORGANIZATION)

        assertEquals(expected, actual)
    }

    @Test
    fun returnsRepositoryListWhenSuccessful() {
        val uri = buildApiUri(
            baseApiUrlScheme = GithubClientTestConstants.DUMMY_GITHUB_API_BASE_URL_SCHEME,
            baseApiUrl = GithubClientTestConstants.DUMMY_GITHUB_API_BASE_URL,
            relativeUrl = GithubApiEndpoint.GET_ORG_REPOSITORIES,
            uriVariables = mapOf("organization" to GithubClientTestConstants.DUMMY_GITHUB_ORGANIZATION),
        )
        val expected = listOf(
            GithubRepositoryDto(name = GithubClientTestConstants.DUMMY_GITHUB_REPOSITORY_NAME),
            GithubRepositoryDto(name = GithubClientTestConstants.DUMMY_GITHUB_REPOSITORY_NAME),
            GithubRepositoryDto(name = GithubClientTestConstants.DUMMY_GITHUB_REPOSITORY_NAME),
        )

        server
            .expect(requestTo(uri.toString()))
            .andRespond(withSuccess(mapper.writeValueAsString(expected), MediaType.APPLICATION_JSON))

        val actual = githubClient.getOrganizationRepositories(GithubClientTestConstants.DUMMY_GITHUB_ORGANIZATION)

        assertEquals(expected, actual)
    }

    @Test
    fun returnsRepositoryWhenSuccessful() {
        val uri = buildApiUri(
            baseApiUrlScheme = GithubClientTestConstants.DUMMY_GITHUB_API_BASE_URL_SCHEME,
            baseApiUrl = GithubClientTestConstants.DUMMY_GITHUB_API_BASE_URL,
            relativeUrl = GithubApiEndpoint.GET_REPOSITORY,
            uriVariables = mapOf(
                "owner" to GithubClientTestConstants.DUMMY_GITHUB_ORGANIZATION,
                "repository" to GithubClientTestConstants.DUMMY_GITHUB_REPOSITORY_NAME,
            )
        )
        val expected = GithubRepositoryDto(name = GithubClientTestConstants.DUMMY_GITHUB_REPOSITORY_NAME)

        server
            .expect(requestTo(uri.toString()))
            .andRespond(withSuccess(mapper.writeValueAsString(expected), MediaType.APPLICATION_JSON))

        val actual = githubClient.getRepository(
            repositoryOwner = GithubClientTestConstants.DUMMY_GITHUB_ORGANIZATION,
            repositoryName = GithubClientTestConstants.DUMMY_GITHUB_REPOSITORY_NAME,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun returnsLanguagesWhenSuccessful() {
        val uri = buildApiUri(
            baseApiUrlScheme = GithubClientTestConstants.DUMMY_GITHUB_API_BASE_URL_SCHEME,
            baseApiUrl = GithubClientTestConstants.DUMMY_GITHUB_API_BASE_URL,
            relativeUrl = GithubApiEndpoint.GET_LANGUAGES,
            uriVariables = mapOf(
                "owner" to GithubClientTestConstants.DUMMY_GITHUB_ORGANIZATION,
                "repository" to GithubClientTestConstants.DUMMY_GITHUB_REPOSITORY_NAME,
            )
        )
        val expected = GithubClientTestConstants.DUMMY_REPO_LANGUAGES

        server
            .expect(requestTo(uri.toString()))
            .andRespond(withSuccess(mapper.writeValueAsString(expected), MediaType.APPLICATION_JSON))

        val actual = githubClient.getRepositoryLanguages(
            repositoryOwner = GithubClientTestConstants.DUMMY_GITHUB_ORGANIZATION,
            repositoryName = GithubClientTestConstants.DUMMY_GITHUB_REPOSITORY_NAME,
        )

        assertEquals(expected, actual)
    }
}
