package com.productboard.gitsy

import com.productboard.gitsy.core.api.GithubClient
import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

@TestConfiguration
class TestBeanConfiguration {

    @Bean
    @Profile("github-client-test")
    fun githubClient() = mockk<GithubClient>()
}
