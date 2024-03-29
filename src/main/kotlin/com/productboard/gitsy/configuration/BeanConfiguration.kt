package com.productboard.gitsy.configuration

import com.productboard.gitsy.core.api.GithubClient
import com.productboard.gitsy.core.api.impl.GithubClientImpl
import com.productboard.gitsy.core.getConfiguredTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.client.RestTemplate

/**
 * Configuration for Spring boot beans.
 *
 * @param appEnvironment configuration handling environment variables
 * @author Aleksei Ermak
 * @date 07.10.2022
 */
@Configuration
class BeanConfiguration(val appEnvironment: AppEnvironment) {

    /**
     * Configures and returns Spring Boot RestTemplate bean.
     */
    @Bean
    @Profile("!rest-test")
    fun restTemplate(): RestTemplate = getConfiguredTemplate()

    /**
     * Configures and returns GitHub REST API client bean.
     */
    @Bean
    @Profile("!github-client-test")
    fun githubClient(restTemplate: RestTemplate): GithubClient =
        GithubClientImpl(
            appEnvironment.githubApiBaseUrlScheme,
            appEnvironment.githubApiBaseUrl,
            appEnvironment.authenticationBearerToken,
            restTemplate,
        )
}
