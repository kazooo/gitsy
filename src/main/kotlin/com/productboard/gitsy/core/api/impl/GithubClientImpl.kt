package com.productboard.gitsy.core.api.impl

import com.productboard.gitsy.core.GithubApiEndpoint
import com.productboard.gitsy.core.api.GithubClient
import com.productboard.gitsy.core.buildApiUri
import com.productboard.gitsy.core.domain.organization.GithubOrganizationResponseDto
import com.productboard.gitsy.core.domain.repository.GithubRepositoryResponseDto
import mu.KotlinLogging
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import java.net.URI

private val logger = KotlinLogging.logger { }

/**
 * Main implementation of REST API client for GitHub REST API.
 *
 * @author Aleksei Ermak
 * @date 07.10.2022
 */
class GithubClientImpl(
    private val githubApiBaseUrlScheme: String,
    private val githubApiBaseUrl: String,
    private val authenticationBearerToken: String?,
    private val restTemplate: RestTemplate,
) : GithubClient {

    override fun getOrganization(organizationName: String): GithubOrganizationResponseDto? {
        require(organizationName.isNotBlank()) { "|organizationName| can't be blank" }
        logger.trace { "Getting information about $organizationName organization..." }

        /* build the corresponding URI template */
        val uri = buildUri(
            GithubApiEndpoint.GET_ORGANIZATION,
            mapOf("organization" to organizationName),
        )
        logger.trace { "Prepared request URI: $uri" }

        /* request GitHub for required information */
        val response = getForEntity(uri, GithubOrganizationResponseDto::class.java)
        if (response.statusCode != HttpStatus.OK) {
            logger.warn {
                "GitHub API returned status code ${response.statusCode}, " +
                    "when trying to to get information about $organizationName organization, " +
                    "something went wrong!"
            }
        }

        return response.body
    }

    override fun getOrganizationRepositories(organizationName: String): List<GithubRepositoryResponseDto> {
        require(organizationName.isNotBlank()) { "|organizationName| can't be blank" }
        logger.trace { "Getting list of repositories $organizationName organization owns..." }
        return requestOrganizationPages(organizationName)
    }

    private fun requestOrganizationPages(organizationName: String): List<GithubRepositoryResponseDto> {
        var page = 1
        val resultRepositoryList = mutableListOf<GithubRepositoryResponseDto>()

        /* request organization repositories until GitHub returns empty list */
        do {
            val queryParams = LinkedMultiValueMap<String, String>()
            queryParams.add("page", page.toString())

            /* build the corresponding URI template */
            val uri = buildUri(
                relativeUrl = GithubApiEndpoint.GET_ORG_REPOSITORIES,
                uriVariables = mapOf("organization" to organizationName),
                queryParams = queryParams,
            )
            logger.trace { "Prepared request URI: $uri" }

            /* request GitHub for required information */
            val response = getForEntities(
                uri = uri,
                responseType = object : ParameterizedTypeReference<List<GithubRepositoryResponseDto>>() {}
            )
            if (response.statusCode != HttpStatus.OK) {
                logger.warn {
                    "GitHub API returned status code ${response.statusCode}, " +
                        "when trying to to get repositories for $organizationName organization, " +
                        "something went wrong!"
                }
                break
            }

            val repositoryList = response.body.orEmpty()
            resultRepositoryList.addAll(repositoryList)
            page++
        } while (repositoryList.isNotEmpty())

        return resultRepositoryList
    }

    override fun getRepository(repositoryOwner: String, repositoryName: String): GithubRepositoryResponseDto? {
        require(repositoryOwner.isNotBlank()) { "|repositoryOwner| can't be blank" }
        require(repositoryName.isNotBlank()) { "|repositoryName| can't be blank" }
        logger.trace {
            "Getting information about $repositoryName repository owned by $repositoryOwner organization..."
        }

        /* build the corresponding URI template */
        val uri = buildUri(
            GithubApiEndpoint.GET_REPOSITORY,
            mapOf(
                "owner" to repositoryOwner,
                "repository" to repositoryName,
            ),
        )
        logger.trace { "Prepared request URI: $uri" }

        /* request GitHub for required information */
        val response = getForEntity(uri, GithubRepositoryResponseDto::class.java)
        if (response.statusCode != HttpStatus.OK) {
            logger.warn {
                "GitHub API returned status code ${response.statusCode}, " +
                    "when trying to to get information about $repositoryName repository, " +
                    "something went wrong!"
            }
        }

        return response.body
    }

    override fun getRepositoryLanguages(repositoryOwner: String, repositoryName: String): Map<String, Long> {
        require(repositoryOwner.isNotBlank()) { "|repositoryOwner| can't be blank!" }
        require(repositoryName.isNotBlank()) { "|repositoryName| can't be blank" }
        logger.trace {
            "Getting information about languages used in $repositoryName " +
                "repository owned by $repositoryOwner organization..."
        }

        /* build the corresponding URI template */
        val uri = buildUri(
            relativeUrl = GithubApiEndpoint.GET_LANGUAGES,
            uriVariables = mapOf(
                "owner" to repositoryOwner,
                "repository" to repositoryName,
            ),
        )
        logger.trace { "Prepared request URI: $uri" }

        /* request GitHub for required information */
        val response = getForEntities(uri, object : ParameterizedTypeReference<Map<String, Long>>() {})
        if (response.statusCode != HttpStatus.OK) {
            logger.warn {
                "GitHub API returned status code ${response.statusCode}, " +
                    "when trying to to get language set of $repositoryName repository, " +
                    "something went wrong!"
            }
        }

        return response.body.orEmpty()
    }

    private fun <T> getForEntity(uri: URI, entityClass: Class<T>): ResponseEntity<T> =
        try {
            restTemplate.exchange(uri, HttpMethod.GET, buildAuthHttpEntity(), entityClass)
        } catch (e: RestClientResponseException) {
            ResponseEntity.status(e.rawStatusCode).build()
        }

    private fun <T> getForEntities(uri: URI, responseType: ParameterizedTypeReference<T>): ResponseEntity<T> =
        try {
            restTemplate.exchange(uri, HttpMethod.GET, buildAuthHttpEntity(), responseType)
        } catch (e: RestClientResponseException) {
            ResponseEntity.status(e.rawStatusCode).build()
        }

    private fun buildUri(
        relativeUrl: String,
        uriVariables: Map<String, Any>,
        queryParams: MultiValueMap<String, String> = LinkedMultiValueMap()
    ) = buildApiUri(githubApiBaseUrlScheme, githubApiBaseUrl, relativeUrl, uriVariables, queryParams)

    private fun buildAuthHttpEntity(): HttpEntity<Void> {
        val authHeader = HttpHeaders()
        if (!authenticationBearerToken.isNullOrBlank()) {
            authHeader.setBearerAuth(authenticationBearerToken)
        }
        return HttpEntity<Void>(authHeader)
    }
}
