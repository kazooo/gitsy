package com.productboard.gitsy

import com.productboard.gitsy.core.api.GithubClient
import com.productboard.gitsy.language.service.RepositoryLanguagesService
import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

@TestConfiguration
class TestBeanConfiguration {

    @Bean
    @Profile("github-client-test")
    fun githubClient() = mockk<GithubClient>()

    @Bean
    @Profile("language-rest-test")
    fun languageService() = mockk<RepositoryLanguagesService>()
}
