package com.productboard.gitsy.languages.services

import com.productboard.gitsy.core.domain.organization.GithubOrganizationEntity
import com.productboard.gitsy.core.domain.repository.GithubRepositoryEntity
import com.productboard.gitsy.core.repository.GithubOrganizationRepository
import com.productboard.gitsy.core.repository.GithubRepositoryRepository
import com.productboard.gitsy.language.domain.calculation.CalculatedLanguageSet
import com.productboard.gitsy.language.domain.original.RepositoryLanguagesEntity
import com.productboard.gitsy.language.domain.original.toDto
import com.productboard.gitsy.language.domain.original.toEntity
import com.productboard.gitsy.language.repository.RepositoryLanguagesRepository
import com.productboard.gitsy.language.service.RepositoryLanguagesService
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.Duration
import org.joda.time.Instant
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.test.context.ActiveProfiles

private object RepositoryLanguagesServiceTestConstants {
    const val ORGANIZATION_NAME = "organization"
    const val ORG_PUBLIC_REPOS = 42
    const val REPOSITORY_NAME = "repository"
    val LANGUAGE_MAP = mapOf("Kotlin" to 42L, "Java" to 42L)
    val CALCULATED_LANGUAGE_MAP = mapOf("Kotlin" to 0.5, "Java" to 0.5)
    const val OLDER_TIMESTAMP_MILLIS_DELTA = 1000L
}

@DataJpaTest(includeFilters = [
    ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = [
            RepositoryLanguagesService::class,
        ]
    )
])
@ComponentScan("com.productboard.gitsy.*")
@ActiveProfiles("test")
class RepositoryLanguagesServiceTest(
    @Autowired private val organizationRepository: GithubOrganizationRepository,
    @Autowired private val repositoryRepository: GithubRepositoryRepository,
    @Autowired private val languageRepository: RepositoryLanguagesRepository,
    @Autowired private val languageService: RepositoryLanguagesService,
) {

    @Test
    fun successfullySaveLanguageSet() {
        val timestamp = Instant.now()
        val repositoryEntity = repository()

        val entity = RepositoryLanguagesEntity(
            languageMap = RepositoryLanguagesServiceTestConstants.LANGUAGE_MAP,
            repository = repositoryEntity,
        )
        entity.createdOn = timestamp

        val actualDto = languageService.save(entity.toDto())
        val persistedOpt = languageRepository.findById(actualDto.id!!)
        actualDto.createdOn = timestamp

        assertThat(persistedOpt.isPresent)
        val persistedEntity = persistedOpt.get()
        persistedEntity.createdOn = timestamp

        val expectedEntity = actualDto.toEntity(repositoryEntity)
        assertThat(persistedEntity).usingRecursiveComparison().isEqualTo(expectedEntity)
    }

    @Test
    fun successfullyGetLatestLanguageSet() {
        val timestamp = Instant.now()
        val latestTimestamp = Instant.now()

        val languageEntity = language()
        languageEntity.createdOn = timestamp

        val latestLanguageEntity = language()
        latestLanguageEntity.createdOn = latestTimestamp

        languageRepository.save(languageEntity)
        languageRepository.save(latestLanguageEntity)

        val latestLanguageSetByService = languageService.getLatestLanguageSet(
            RepositoryLanguagesServiceTestConstants.ORGANIZATION_NAME,
            RepositoryLanguagesServiceTestConstants.REPOSITORY_NAME,
        )
        val expectedLanguageSet = CalculatedLanguageSet(
            languageMap = RepositoryLanguagesServiceTestConstants.CALCULATED_LANGUAGE_MAP,
            timestamp = latestTimestamp,
        )

        assertThat(latestLanguageSetByService).usingRecursiveComparison().isEqualTo(expectedLanguageSet)
    }

    @Test
    fun failedGetLatestLanguageSetBlankOrganizationName() {
        assertThrows<IllegalArgumentException> {
            languageService.getLatestLanguageSet(
                organizationName = "",
                RepositoryLanguagesServiceTestConstants.REPOSITORY_NAME,
            )
        }
    }

    @Test
    fun failedGetLatestLanguageSetBlankRepositoryName() {
        assertThrows<IllegalArgumentException> {
            languageService.getLatestLanguageSet(
                RepositoryLanguagesServiceTestConstants.ORGANIZATION_NAME,
                repositoryName = "",
            )
        }
    }

    @Test
    fun notFoundAnyLatestLanguageSet() {
        val latestLanguageSetByService = languageService.getLatestLanguageSet(
            RepositoryLanguagesServiceTestConstants.ORGANIZATION_NAME,
            RepositoryLanguagesServiceTestConstants.REPOSITORY_NAME,
        )
        assertNull(latestLanguageSetByService)
    }

    @Test
    fun successfullyGetLanguageEvolution() {
        val languageEntity = languageRepository.save(language())
        val latestLanguageEntity = languageRepository.save(language())

        val expected = listOf(
            CalculatedLanguageSet(
                languageMap = RepositoryLanguagesServiceTestConstants.CALCULATED_LANGUAGE_MAP,
                timestamp = languageEntity.createdOn,
            ),
            CalculatedLanguageSet(
                languageMap = RepositoryLanguagesServiceTestConstants.CALCULATED_LANGUAGE_MAP,
                timestamp = latestLanguageEntity.createdOn,
            )
        )

        val actual = languageService.getLanguageEvolution(
            RepositoryLanguagesServiceTestConstants.ORGANIZATION_NAME,
            RepositoryLanguagesServiceTestConstants.REPOSITORY_NAME,
        )

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    @Test
    fun failedGetLanguageEvolutionBlankOrganizationName() {
        assertThrows<IllegalArgumentException> {
            languageService.getLanguageEvolution(
                organizationName = "",
                RepositoryLanguagesServiceTestConstants.REPOSITORY_NAME,
            )
        }
    }

    @Test
    fun failedGetLanguageEvolutionBlankRepositoryName() {
        assertThrows<IllegalArgumentException> {
            languageService.getLanguageEvolution(
                RepositoryLanguagesServiceTestConstants.ORGANIZATION_NAME,
                repositoryName = "",
            )
        }
    }

    @Test
    fun successfullyGetLanguageEvolutionForDateRange() {
        val timestamp = Instant.now().minus(
            RepositoryLanguagesServiceTestConstants.OLDER_TIMESTAMP_MILLIS_DELTA
        )
        val latestTimestamp = Instant.now()

        val languageEntity = language()
        languageEntity.createdOn = timestamp

        val latestLanguageEntity = language()
        latestLanguageEntity.createdOn = latestTimestamp

        languageRepository.save(languageEntity)
        languageRepository.save(latestLanguageEntity)

        val expected = listOf(
            CalculatedLanguageSet(
                languageMap = RepositoryLanguagesServiceTestConstants.CALCULATED_LANGUAGE_MAP,
                timestamp = latestLanguageEntity.createdOn,
            )
        )

        val actual = languageService.getLanguageEvolution(
            RepositoryLanguagesServiceTestConstants.ORGANIZATION_NAME,
            RepositoryLanguagesServiceTestConstants.REPOSITORY_NAME,
            latestTimestamp,
            Instant.now(),
        )

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    @Test
    fun failedGetLanguageEvolutionForDateRangeBadDateRange() {
        assertThrows<IllegalArgumentException> {
            languageService.getLanguageEvolution(
                RepositoryLanguagesServiceTestConstants.ORGANIZATION_NAME,
                RepositoryLanguagesServiceTestConstants.REPOSITORY_NAME,
                Instant.now().plus(
                    RepositoryLanguagesServiceTestConstants.OLDER_TIMESTAMP_MILLIS_DELTA
                ),
                Instant.now(),
            )
        }
    }

    @Test
    fun failedGetLanguageEvolutionForDateRangeBlankOrganizationName() {
        assertThrows<IllegalArgumentException> {
            languageService.getLanguageEvolution(
                organizationName = "",
                RepositoryLanguagesServiceTestConstants.REPOSITORY_NAME,
                Instant.now(),
                Instant.now(),
            )
        }
    }

    @Test
    fun failedGetLanguageEvolutionForDateRangeBlankRepositoryName() {
        assertThrows<IllegalArgumentException> {
            languageService.getLanguageEvolution(
                RepositoryLanguagesServiceTestConstants.ORGANIZATION_NAME,
                repositoryName = "",
                Instant.now(),
                Instant.now(),
            )
        }
    }

    @Test
    fun successfullyGetAggregatedLanguageSet() {
        val repositoryOne = repositoryRepository.save(
            GithubRepositoryEntity(
                name = "repositoryOne",
                organization = organization(),
            )
        )

        languageRepository.save(
            RepositoryLanguagesEntity(
                languageMap = mapOf("Kotlin" to 42L),
                repository = repositoryOne,
            )
        )


        val repositoryTwo = repositoryRepository.save(
            GithubRepositoryEntity(
                name = "repositoryTwo",
                organization = organization(),
            )
        )

        val languageSetTwo = languageRepository.save(
            RepositoryLanguagesEntity(
                languageMap = mapOf("Java" to 42L),
                repository = repositoryTwo,
            )
        )

        val expected = CalculatedLanguageSet(
            languageMap = mapOf(
                "Kotlin" to 0.5,
                "Java" to 0.5,
            ),
            timestamp = languageSetTwo.createdOn,
        )

        val actual = languageService.getAggregatedLanguageSetForOrganization(
            RepositoryLanguagesServiceTestConstants.ORGANIZATION_NAME,
        )

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    @Test
    fun failedGetAggregatedLanguageSetNotFoundAnyLanguageSets() {
        val aggregatedLanguageSet = languageService.getAggregatedLanguageSetForOrganization(
            RepositoryLanguagesServiceTestConstants.ORGANIZATION_NAME,
        )
        assertNull(aggregatedLanguageSet)
    }

    @Test
    fun failedGetAggregatedLanguageSetBlankOrganizationName() {
        assertThrows<IllegalArgumentException> {
            languageService.getAggregatedLanguageSetForOrganization(
                organizationName = "",
            )
        }
    }

    private fun organization() = organizationRepository.save(
        GithubOrganizationEntity(
            name = RepositoryLanguagesServiceTestConstants.ORGANIZATION_NAME,
            publicRepos = RepositoryLanguagesServiceTestConstants.ORG_PUBLIC_REPOS,
        )
    )

    private fun repository() = repositoryRepository.save(
        GithubRepositoryEntity(
            name = RepositoryLanguagesServiceTestConstants.REPOSITORY_NAME,
            organization = organization(),
        )
    )

    private fun language(repository: GithubRepositoryEntity = repository()) =
        RepositoryLanguagesEntity(
            languageMap = RepositoryLanguagesServiceTestConstants.LANGUAGE_MAP,
            repository = repository,
        )
}
