package com.productboard.gitsy.core.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.productboard.gitsy.core.GithubApiEndpoint
import com.productboard.gitsy.core.buildApiUri
import com.productboard.gitsy.core.domain.organization.GithubOrganizationResponseDto
import com.productboard.gitsy.core.domain.repository.GithubRepositoryResponseDto
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
    const val API_BASE_URL_SCHEME = "https"
    const val API_BASE_URL = "base-api-url.com"
    const val ORGANIZATION = "organization"
    const val ORGANIZATION_REPOS = 42
    const val REPOSITORY_NAME = "repository"
    val REPO_LANGUAGES = mapOf("Kotlin" to 12L, "Java" to 10L)
}

@RestClientTest
@AutoConfigureWebClient(registerRestTemplate = true)
@ComponentScan("com.productboard.*")
@TestPropertySource(
    properties = [
        "gitsy.githubApiBaseUrlScheme=${GithubClientTestConstants.API_BASE_URL_SCHEME}",
        "gitsy.githubApiBaseUrl=${GithubClientTestConstants.API_BASE_URL}",
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
            baseApiUrlScheme = GithubClientTestConstants.API_BASE_URL_SCHEME,
            baseApiUrl = GithubClientTestConstants.API_BASE_URL,
            relativeUrl = GithubApiEndpoint.GET_ORGANIZATION,
            uriVariables = mapOf("organization" to GithubClientTestConstants.ORGANIZATION),
        )
        val expected = GithubOrganizationResponseDto(
            name = GithubClientTestConstants.ORGANIZATION,
            publicRepos = GithubClientTestConstants.ORGANIZATION_REPOS,
        )

        server
            .expect(requestTo(uri.toString()))
            .andRespond(withSuccess(mapper.writeValueAsString(expected), MediaType.APPLICATION_JSON))

        val actual = githubClient.getOrganization(GithubClientTestConstants.ORGANIZATION)

        assertEquals(expected, actual)
    }

    @Test
    fun returnsRepositoryListWhenSuccessful() {
        val uri = buildApiUri(
            baseApiUrlScheme = GithubClientTestConstants.API_BASE_URL_SCHEME,
            baseApiUrl = GithubClientTestConstants.API_BASE_URL,
            relativeUrl = GithubApiEndpoint.GET_ORG_REPOSITORIES,
            uriVariables = mapOf("organization" to GithubClientTestConstants.ORGANIZATION),
        )
        val expected = listOf(
            GithubRepositoryResponseDto(name = GithubClientTestConstants.REPOSITORY_NAME),
            GithubRepositoryResponseDto(name = GithubClientTestConstants.REPOSITORY_NAME),
            GithubRepositoryResponseDto(name = GithubClientTestConstants.REPOSITORY_NAME),
        )

        server
            .expect(requestTo(uri.toString()))
            .andRespond(withSuccess(mapper.writeValueAsString(expected), MediaType.APPLICATION_JSON))

        val actual = githubClient.getOrganizationRepositories(GithubClientTestConstants.ORGANIZATION)

        assertEquals(expected, actual)
    }

    @Test
    fun returnsRepositoryWhenSuccessful() {
        val uri = buildApiUri(
            baseApiUrlScheme = GithubClientTestConstants.API_BASE_URL_SCHEME,
            baseApiUrl = GithubClientTestConstants.API_BASE_URL,
            relativeUrl = GithubApiEndpoint.GET_REPOSITORY,
            uriVariables = mapOf(
                "owner" to GithubClientTestConstants.ORGANIZATION,
                "repository" to GithubClientTestConstants.REPOSITORY_NAME,
            )
        )
        val expected = GithubRepositoryResponseDto(name = GithubClientTestConstants.REPOSITORY_NAME)

        server
            .expect(requestTo(uri.toString()))
            .andRespond(withSuccess(mapper.writeValueAsString(expected), MediaType.APPLICATION_JSON))

        val actual = githubClient.getRepository(
            repositoryOwner = GithubClientTestConstants.ORGANIZATION,
            repositoryName = GithubClientTestConstants.REPOSITORY_NAME,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun returnsLanguagesWhenSuccessful() {
        val uri = buildApiUri(
            baseApiUrlScheme = GithubClientTestConstants.API_BASE_URL_SCHEME,
            baseApiUrl = GithubClientTestConstants.API_BASE_URL,
            relativeUrl = GithubApiEndpoint.GET_LANGUAGES,
            uriVariables = mapOf(
                "owner" to GithubClientTestConstants.ORGANIZATION,
                "repository" to GithubClientTestConstants.REPOSITORY_NAME,
            )
        )
        val expected = GithubClientTestConstants.REPO_LANGUAGES

        server
            .expect(requestTo(uri.toString()))
            .andRespond(withSuccess(mapper.writeValueAsString(expected), MediaType.APPLICATION_JSON))

        val actual = githubClient.getRepositoryLanguages(
            repositoryOwner = GithubClientTestConstants.ORGANIZATION,
            repositoryName = GithubClientTestConstants.REPOSITORY_NAME,
        )

        assertEquals(expected, actual)
    }
}
