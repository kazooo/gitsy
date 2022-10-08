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
     * Logs all configured environment variables of the application.
     */
    @PostConstruct
    fun logConfiguration() {
        logger.info { "Application started with following environment variables:" }
        logger.info { "GitHub REST API base URL scheme: $githubApiBaseUrlScheme" }
        logger.info { "GitHub REST API base URL: $githubApiBaseUrl" }
    }
}