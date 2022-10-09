package com.productboard.gitsy.configuration

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

private val logger = KotlinLogging.logger { }

/**
 * Configuration handling environment variables the application is configurable with.
 *
 * @author Aleksei Ermak
 * @date 07.10.2022
 */
@Configuration
class AppEnvironment {

    /**
     * GitHub REST API base URL scheme. "https" by default.
     */
    @Value("\${gitsy.githubApiBaseUrlScheme}")
    val githubApiBaseUrlScheme: String = "https"

    /**
     * GitHub REST API base URL. "api.github.com" by default.
     */
    @Value("\${gitsy.githubApiBaseUrl}")
    val githubApiBaseUrl: String = "api.github.com"

    /**
     * GitHub API authentication Bearer token.
     */
    @Value("\${gitsy.authBearerToken}")
    val authenticationBearerToken: String? = null

    /**
     * List of GitHub organizations to periodically synchronize.
     */
    @Value("\${gitsy.synchronizeOrganizations}")
    val synchronizeOrganizations: List<String> = emptyList()

    /**
     * Flag indicating if the application can synchronize configured organizations.
     */
    @Value("\${gitsy.synchronizationEnabled}")
    val synchronizationEnabled: Boolean = false

    /**
     * Synchronization cron.
     */
    @Value("\${gitsy.organizationSyncCron}")
    val syncCron: String = "0 0 12 1 * *"

    /**
     * Logs all configured environment variables of the application.
     */
    @PostConstruct
    fun logConfiguration() {
        logger.info { "*********************************************************" }
        logger.info { "Application started with following environment variables:" }
        logger.info { "GitHub REST API base URL scheme: $githubApiBaseUrlScheme" }
        logger.info { "GitHub REST API base URL: $githubApiBaseUrl" }
        logger.info { "Auth Bearer token provided: ${!authenticationBearerToken.isNullOrBlank()}" }
        logger.info { "Synchronization enabled: $synchronizationEnabled" }
        logger.info { "Organizations to synchronize: $synchronizeOrganizations" }
        logger.info { "Synchronization cron: $syncCron" }

        if (authenticationBearerToken.isNullOrBlank()) {
            logger.warn { "" }
            logger.warn {
                "No authentication token provided, " +
                    "this may lead to limited application performance due to GitHub API rate limits!"
            }
            logger.warn {
                "Please provide authentication token to use the application without limitations."
            }
        }

        logger.info { "*********************************************************" }
    }
}
