package com.productboard.gitsy.languages.services

import com.productboard.gitsy.TestBeanConfiguration
import com.productboard.gitsy.core.api.GithubClient
import com.productboard.gitsy.core.domain.organization.GithubOrganizationDto
import com.productboard.gitsy.core.domain.organization.GithubOrganizationEntity
import com.productboard.gitsy.core.domain.repository.GithubRepositoryDto
import com.productboard.gitsy.core.domain.repository.GithubRepositoryEntity
import com.productboard.gitsy.core.domain.repository.toDto
import com.productboard.gitsy.core.repository.GithubOrganizationRepository
import com.productboard.gitsy.core.repository.GithubRepositoryRepository
import com.productboard.gitsy.language.domain.original.RepositoryLanguagesEntity
import com.productboard.gitsy.language.repository.RepositoryLanguagesRepository
import com.productboard.gitsy.language.service.RepositoryLanguageSynchronizationService
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.Instant
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

object RepositoryLanguageSynchronizationServiceTestConstants {
    const val ORGANIZATION_NAME = "organization"
    const val ORG_PUBLIC_REPOS = 42
    const val REPOSITORY_NAME = "repository"
    val LANGUAGE_MAP = mapOf("Kotlin" to 42L, "Java" to 42L)
}

@DataJpaTest(includeFilters = [
    ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = [
            RepositoryLanguageSynchronizationService::class,
        ]
    )
])
@Import(TestBeanConfiguration::class)
@ComponentScan("com.productboard.gitsy.*")
@ActiveProfiles(value = ["test", "github-client-test"])
class RepositoryLanguageSynchronizationServiceTest(
    @Autowired private val organizationRepository: GithubOrganizationRepository,
    @Autowired private val repositoryRepository: GithubRepositoryRepository,
    @Autowired private val languageRepository: RepositoryLanguagesRepository,
    @Autowired private val languageSyncService: RepositoryLanguageSynchronizationService,
) {

    @Autowired
    lateinit var githubClient: GithubClient

    @Test
    fun successfulRepositorySynchronization() {
        every {
            githubClient.getRepositoryLanguages(
                RepositoryLanguageSynchronizationServiceTestConstants.ORGANIZATION_NAME,
                RepositoryLanguageSynchronizationServiceTestConstants.REPOSITORY_NAME,
            )
        }.returns(
            RepositoryLanguageSynchronizationServiceTestConstants.LANGUAGE_MAP,
        )

        val repositoryEntity = repository()

        languageSyncService.synchronizeRepositoryLanguages(repositoryEntity.toDto())

        val persistedLanguages = languageRepository.findAllByRepositoryOrganizationNameAndRepositoryName(
            RepositoryLanguageSynchronizationServiceTestConstants.ORGANIZATION_NAME,
            RepositoryLanguageSynchronizationServiceTestConstants.REPOSITORY_NAME
        )

        assertThat(persistedLanguages.size == 1)

        val expectedTimestamp = Instant.now()

        val persistedLanguageSet = persistedLanguages[0]
        persistedLanguageSet.createdOn = expectedTimestamp

        val expectedLanguageSet = RepositoryLanguagesEntity(
            id = persistedLanguageSet.id,
            languageMap = RepositoryLanguageSynchronizationServiceTestConstants.LANGUAGE_MAP,
            repository = repositoryEntity,
            createdOn = expectedTimestamp,
        )

        assertThat(persistedLanguageSet).usingRecursiveComparison().isEqualTo(expectedLanguageSet)
    }

    @Test
    fun languageSetNotFoundRepositorySynchronization() {
        every {
            githubClient.getRepositoryLanguages(
                RepositoryLanguageSynchronizationServiceTestConstants.ORGANIZATION_NAME,
                RepositoryLanguageSynchronizationServiceTestConstants.REPOSITORY_NAME,
            )
        }.returns(emptyMap())

        val repositoryEntity = repository()

        languageSyncService.synchronizeRepositoryLanguages(repositoryEntity.toDto())

        val persistedLanguages = languageRepository.findAllByRepositoryOrganizationNameAndRepositoryName(
            RepositoryLanguageSynchronizationServiceTestConstants.ORGANIZATION_NAME,
            RepositoryLanguageSynchronizationServiceTestConstants.REPOSITORY_NAME,
        )

        assertThat(persistedLanguages.isEmpty())
    }

    @Test
    fun failedSyncBlankOrganizationName() {
        assertThrows<IllegalArgumentException> {
            languageSyncService.synchronizeRepositoryLanguages(
                GithubRepositoryDto(
                    name = RepositoryLanguageSynchronizationServiceTestConstants.REPOSITORY_NAME,
                    organization = GithubOrganizationDto(
                        name = "",
                        publicRepos = RepositoryLanguageSynchronizationServiceTestConstants.ORG_PUBLIC_REPOS,
                    )
                )
            )
        }
    }

    @Test
    fun failedSyncBlankRepositoryName() {
        assertThrows<IllegalArgumentException> {
            languageSyncService.synchronizeRepositoryLanguages(
                GithubRepositoryDto(
                    name = "",
                    organization = GithubOrganizationDto(
                        name = RepositoryLanguageSynchronizationServiceTestConstants.ORGANIZATION_NAME,
                        publicRepos = RepositoryLanguageSynchronizationServiceTestConstants.ORG_PUBLIC_REPOS,
                    )
                )
            )
        }
    }

    private fun organization() = organizationRepository.save(
        GithubOrganizationEntity(
            name = RepositoryLanguageSynchronizationServiceTestConstants.ORGANIZATION_NAME,
            publicRepos = RepositoryLanguageSynchronizationServiceTestConstants.ORG_PUBLIC_REPOS,
        )
    )

    private fun repository() = repositoryRepository.save(
        GithubRepositoryEntity(
            name = RepositoryLanguageSynchronizationServiceTestConstants.REPOSITORY_NAME,
            organization = organization(),
        )
    )
}
