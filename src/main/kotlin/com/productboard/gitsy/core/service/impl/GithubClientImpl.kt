package com.productboard.gitsy.core.service.impl

import com.productboard.gitsy.core.GithubApiEndpoint
import com.productboard.gitsy.core.buildApiUri
import com.productboard.gitsy.core.dto.GithubOrganizationDto
import com.productboard.gitsy.core.dto.GithubRepositoryDto
import com.productboard.gitsy.core.service.GithubClient
import mu.KotlinLogging
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
    private val restTemplate: RestTemplate,
) : GithubClient {

    override fun getOrganization(organizationName: String): GithubOrganizationDto? {
        require(organizationName.isNotBlank()) { "|organizationName| can't be blank" }
        logger.trace { "Getting information about $organizationName organization..." }

        /* build the corresponding URI template */
        val uri = buildUri(
            GithubApiEndpoint.GET_ORGANIZATION,
            mapOf("organization" to organizationName),
        )
        logger.trace { "Prepared request URI: $uri" }

        /* request GitHub for required information */
        val response = getForEntity(uri, GithubOrganizationDto::class.java)
        if (response.statusCode != HttpStatus.OK) {
            logger.trace { "GitHub API returned status code ${response.statusCode}, something went wrong!" }
        }

        return response.body
    }

    override fun getOrganizationRepositories(organizationName: String): List<GithubRepositoryDto> {
        require(organizationName.isNotBlank()) { "|organizationName| can't be blank" }
        logger.trace { "Getting list of repositories $organizationName organization owns..." }

        /* build the corresponding URI template */
        val uri = buildUri(
            GithubApiEndpoint.GET_ORG_REPOSITORIES,
            mapOf("organization" to organizationName),
        )
        logger.trace { "Prepared request URI: $uri" }

        /* request GitHub for required information */
        val response = getForEntities(uri, object : ParameterizedTypeReference<List<GithubRepositoryDto>>() {})
        if (response.statusCode != HttpStatus.OK) {
            logger.trace { "GitHub API returned status code ${response.statusCode}, something went wrong!" }
        }

        return response.body.orEmpty()
    }

    override fun getRepository(repositoryOwner: String, repositoryName: String): GithubRepositoryDto? {
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
        val response = getForEntity(uri, GithubRepositoryDto::class.java)
        if (response.statusCode != HttpStatus.OK) {
            logger.trace { "GitHub API returned status code ${response.statusCode}, something went wrong!" }
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
            GithubApiEndpoint.GET_LANGUAGES,
            mapOf(
                "owner" to repositoryOwner,
                "repository" to repositoryName,
            ),
        )
        logger.trace { "Prepared request URI: $uri" }

        /* request GitHub for required information */
        val response = getForEntities(uri, object : ParameterizedTypeReference<Map<String, Long>>() {})
        if (response.statusCode != HttpStatus.OK) {
            logger.trace { "GitHub API returned status code ${response.statusCode}, something went wrong!" }
        }

        return response.body.orEmpty()
    }

    private fun <T> getForEntity(uri: URI, entityClass: Class<T>): ResponseEntity<T> = try {
        restTemplate.getForEntity(uri, entityClass)
    } catch (e: RestClientResponseException) {
        ResponseEntity.status(e.rawStatusCode).build()
    }

    private fun <T> getForEntities(uri: URI, responseType: ParameterizedTypeReference<T>): ResponseEntity<T> = try {
        restTemplate.exchange(
            uri,
            HttpMethod.GET,
            null,
            responseType,
        )
    } catch (e: RestClientResponseException) {
        ResponseEntity.status(e.rawStatusCode).build()
    }

    private fun buildUri(relativeUrl: String, uriVariables: Map<String, Any>) =
        buildApiUri(githubApiBaseUrlScheme, githubApiBaseUrl, relativeUrl, uriVariables)
}
