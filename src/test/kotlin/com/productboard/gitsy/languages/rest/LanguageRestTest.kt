package com.productboard.gitsy.languages.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.productboard.gitsy.TestBeanConfiguration
import com.productboard.gitsy.language.domain.calculation.CalculatedLanguageSet
import com.productboard.gitsy.language.service.RepositoryLanguagesService
import io.mockk.every
import org.joda.time.Instant
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

private object LanguageRestTestConstants {
    const val BASE_API_ENDPOINT = "/api/language"
    const val ORGANIZATION_NAME = "organization"
    const val REPOSITORY_NAME = "repository"
    val LANGUAGES = mapOf(
        "Kotlin" to 0.8,
        "Java" to 0.2,
    )
}

@WebMvcTest
@Import(TestBeanConfiguration::class)
@ActiveProfiles(value = ["test", "language-rest-test"])
class LanguageRestTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val mapper: ObjectMapper,
    @Autowired private val languagesService: RepositoryLanguagesService,
) {

    @Test
    fun successfullyGetLatestLanguageSet() {
        val calculatedLanguageSet = CalculatedLanguageSet(
            languageMap = LanguageRestTestConstants.LANGUAGES,
        )
        every {
            languagesService.getLatestLanguageSet(
                organizationName = LanguageRestTestConstants.ORGANIZATION_NAME,
                repositoryName = LanguageRestTestConstants.REPOSITORY_NAME,
            )
        }.returns(calculatedLanguageSet)

        mockMvc.perform(get(
            LanguageRestTestConstants.BASE_API_ENDPOINT +
                    "/${LanguageRestTestConstants.ORGANIZATION_NAME}" +
                    "/${LanguageRestTestConstants.REPOSITORY_NAME}" +
                    "/latest")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(LanguageRestTestConstants.LANGUAGES.toString()))
    }

    @Test
    fun successfullyGetLatestLanguageSetNotFound() {
        every {
            languagesService.getLatestLanguageSet(
                organizationName = LanguageRestTestConstants.ORGANIZATION_NAME,
                repositoryName = LanguageRestTestConstants.REPOSITORY_NAME,
            )
        }.returns(null)

        mockMvc.perform(get(
            LanguageRestTestConstants.BASE_API_ENDPOINT +
                    "/${LanguageRestTestConstants.ORGANIZATION_NAME}" +
                    "/${LanguageRestTestConstants.REPOSITORY_NAME}" +
                    "/latest"
        ))
            .andExpect(status().isNotFound)
    }

    @Test
    fun successfullyLanguageEvolution() {
        val languageSets = listOf(
            CalculatedLanguageSet(
                languageMap = LanguageRestTestConstants.LANGUAGES,
            ),
            CalculatedLanguageSet(
                languageMap = LanguageRestTestConstants.LANGUAGES,
            )
        )
        every {
            languagesService.getLanguageEvolution(
                organizationName = LanguageRestTestConstants.ORGANIZATION_NAME,
                repositoryName = LanguageRestTestConstants.REPOSITORY_NAME,
            )
        }.returns(languageSets)

        mockMvc.perform(get(
            LanguageRestTestConstants.BASE_API_ENDPOINT +
                    "/${LanguageRestTestConstants.ORGANIZATION_NAME}" +
                    "/${LanguageRestTestConstants.REPOSITORY_NAME}"
        ))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(mapper.writeValueAsString(languageSets)))
    }

    @Test
    fun successfullyLanguageEvolutionForDateRange() {
        val languageSets = listOf(
            CalculatedLanguageSet(
                languageMap = LanguageRestTestConstants.LANGUAGES,
            ),
            CalculatedLanguageSet(
                languageMap = LanguageRestTestConstants.LANGUAGES,
            )
        )
        val timestamp = Instant.now()
        every {
            languagesService.getLanguageEvolution(
                organizationName = LanguageRestTestConstants.ORGANIZATION_NAME,
                repositoryName = LanguageRestTestConstants.REPOSITORY_NAME,
                timestamp,
                timestamp
            )
        }.returns(languageSets)

        mockMvc.perform(get(
            LanguageRestTestConstants.BASE_API_ENDPOINT +
                    "/${LanguageRestTestConstants.ORGANIZATION_NAME}" +
                    "/${LanguageRestTestConstants.REPOSITORY_NAME}"
            )
            .param("from", timestamp.toString())
            .param("to", timestamp.toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(mapper.writeValueAsString(languageSets)))
    }

    @Test
    fun successfullyGetRepositoryAggregation() {
        val calculatedLanguageSet = CalculatedLanguageSet(
            languageMap = LanguageRestTestConstants.LANGUAGES,
        )
        every {
            languagesService.getAggregatedLanguageSetForOrganization(
                organizationName = LanguageRestTestConstants.ORGANIZATION_NAME,
            )
        }.returns(calculatedLanguageSet)

        mockMvc.perform(get(
            LanguageRestTestConstants.BASE_API_ENDPOINT +
                    "/${LanguageRestTestConstants.ORGANIZATION_NAME}"
        ))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(LanguageRestTestConstants.LANGUAGES.toString()))
    }

    @Test
    fun successfullyGetRepositoryAggregationNotFound() {
        every {
            languagesService.getAggregatedLanguageSetForOrganization(
                organizationName = LanguageRestTestConstants.ORGANIZATION_NAME,
            )
        }.returns(null)

        mockMvc.perform(get(
            LanguageRestTestConstants.BASE_API_ENDPOINT +
                    "/${LanguageRestTestConstants.ORGANIZATION_NAME}"
        ))
            .andExpect(status().isNotFound)
    }
}
