package com.productboard.gitsy.core.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.productboard.gitsy.configuration.SynchronizationConfiguration
import com.productboard.gitsy.core.GithubApiEndpoint
import com.productboard.gitsy.core.buildApiUri
import com.productboard.gitsy.core.domain.organization.GithubOrganizationResponseDto
import com.productboard.gitsy.core.domain.repository.GithubRepositoryResponseDto
import com.productboard.gitsy.core.service.GithubOrganizationService
import com.productboard.gitsy.core.service.GithubOrganizationSyncService
import com.productboard.gitsy.core.service.GithubRepositoryService
import com.productboard.gitsy.language.rest.LanguageRest
import com.productboard.gitsy.language.service.RepositoryLanguageSynchronizationService
import com.productboard.gitsy.language.service.RepositoryLanguagesService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withServerError
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
@ComponentScan(
    basePackages = ["com.productboard.*"],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = [
                SynchronizationConfiguration::class,
                GithubOrganizationSyncService::class,
                GithubRepositoryService::class,
                GithubOrganizationService::class,
                RepositoryLanguageSynchronizationService::class,
                RepositoryLanguagesService::class,
                LanguageRest::class,
            ]
        ),
    ],
)
@TestPropertySource(
    properties = [
        "gitsy.githubApiBaseUrlScheme=${GithubClientTestConstants.API_BASE_URL_SCHEME}",
        "gitsy.githubApiBaseUrl=${GithubClientTestConstants.API_BASE_URL}",
    ]
)
@ActiveProfiles(value = ["test", "rest-test"])
class GithubClientTest(
    @Autowired private val server: MockRestServiceServer,
    @Autowired private val mapper: ObjectMapper,
    @Autowired private val githubClient: GithubClient,
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

    @Test
    fun failedGetOrganizationBlankOrganizationName() {
        assertThrows<IllegalArgumentException> {
            githubClient.getOrganization(organizationName = "")
        }
    }

    @Test
    fun successfullyGetOrganizationReturnNull() {
        val uri = buildApiUri(
            baseApiUrlScheme = GithubClientTestConstants.API_BASE_URL_SCHEME,
            baseApiUrl = GithubClientTestConstants.API_BASE_URL,
            relativeUrl = GithubApiEndpoint.GET_ORGANIZATION,
            uriVariables = mapOf("organization" to GithubClientTestConstants.ORGANIZATION),
        )

        server
            .expect(requestTo(uri.toString()))
            .andRespond(withServerError())

        val actual = githubClient.getOrganization(GithubClientTestConstants.ORGANIZATION)
        assertNull(actual)
    }

    @Test
    fun successfullyGetOrganizationRepositoriesReturnEmptyList() {
        val uri = buildApiUri(
            baseApiUrlScheme = GithubClientTestConstants.API_BASE_URL_SCHEME,
            baseApiUrl = GithubClientTestConstants.API_BASE_URL,
            relativeUrl = GithubApiEndpoint.GET_ORG_REPOSITORIES,
            uriVariables = mapOf("organization" to GithubClientTestConstants.ORGANIZATION),
        )

        server
            .expect(requestTo(uri.toString()))
            .andRespond(withServerError())

        val actual = githubClient.getOrganizationRepositories(GithubClientTestConstants.ORGANIZATION)
        assertEquals(emptyList<GithubRepositoryResponseDto>(), actual)
    }

    @Test
    fun failedGetOrganizationRepositoriesBlankOrganizationName() {
        assertThrows<IllegalArgumentException> {
            githubClient.getOrganizationRepositories(organizationName = "")
        }
    }

    @Test
    fun successfullyGetRepositoryReturnNull() {
        val uri = buildApiUri(
            baseApiUrlScheme = GithubClientTestConstants.API_BASE_URL_SCHEME,
            baseApiUrl = GithubClientTestConstants.API_BASE_URL,
            relativeUrl = GithubApiEndpoint.GET_REPOSITORY,
            uriVariables = mapOf(
                "owner" to GithubClientTestConstants.ORGANIZATION,
                "repository" to GithubClientTestConstants.REPOSITORY_NAME,
            )
        )

        server
            .expect(requestTo(uri.toString()))
            .andRespond(withServerError())

        val actual = githubClient.getRepository(
            repositoryOwner = GithubClientTestConstants.ORGANIZATION,
            repositoryName = GithubClientTestConstants.REPOSITORY_NAME,
        )
        assertNull(actual)
    }

    @Test
    fun failedGetRepositoryBlankOrganizationName() {
        assertThrows<IllegalArgumentException> {
            githubClient.getRepository(
                repositoryOwner = "",
                repositoryName = GithubClientTestConstants.REPOSITORY_NAME,
            )
        }
    }

    @Test
    fun failedGetRepositoryBlankRepositoryName() {
        assertThrows<IllegalArgumentException> {
            githubClient.getRepository(
                repositoryOwner = GithubClientTestConstants.ORGANIZATION,
                repositoryName = "",
            )
        }
    }

    @Test
    fun successfullyGetRepositoryLanguageSetReturnNull() {
        val uri = buildApiUri(
            baseApiUrlScheme = GithubClientTestConstants.API_BASE_URL_SCHEME,
            baseApiUrl = GithubClientTestConstants.API_BASE_URL,
            relativeUrl = GithubApiEndpoint.GET_LANGUAGES,
            uriVariables = mapOf(
                "owner" to GithubClientTestConstants.ORGANIZATION,
                "repository" to GithubClientTestConstants.REPOSITORY_NAME,
            )
        )

        server
            .expect(requestTo(uri.toString()))
            .andRespond(withServerError())

        val actual = githubClient.getRepositoryLanguages(
            repositoryOwner = GithubClientTestConstants.ORGANIZATION,
            repositoryName = GithubClientTestConstants.REPOSITORY_NAME,
        )
        assertEquals(emptyMap<String, Long>(), actual)
    }

    @Test
    fun failedGetRepositoryLanguageSetBlankOrganizationName() {
        assertThrows<IllegalArgumentException> {
            githubClient.getRepositoryLanguages(
                repositoryOwner = "",
                repositoryName = GithubClientTestConstants.REPOSITORY_NAME,
            )
        }
    }

    @Test
    fun failedGetRepositoryLanguageSetBlankRepositoryName() {
        assertThrows<IllegalArgumentException> {
            githubClient.getRepositoryLanguages(
                repositoryOwner = GithubClientTestConstants.ORGANIZATION,
                repositoryName = "",
            )
        }
    }
}
