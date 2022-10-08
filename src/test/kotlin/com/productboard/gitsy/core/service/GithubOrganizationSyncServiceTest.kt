package com.productboard.gitsy.core.service

import com.productboard.gitsy.TestBeanConfiguration
import com.productboard.gitsy.core.api.GithubClient
import com.productboard.gitsy.core.domain.organization.GithubOrganizationEntity
import com.productboard.gitsy.core.domain.organization.GithubOrganizationResponseDto
import com.productboard.gitsy.core.domain.repository.GithubRepositoryEntity
import com.productboard.gitsy.core.domain.repository.GithubRepositoryResponseDto
import com.productboard.gitsy.core.repository.GithubOrganizationRepository
import com.productboard.gitsy.core.repository.GithubRepositoryRepository
import com.productboard.gitsy.languages.services.RepositoryLanguageSynchronizationServiceTestConstants
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

object GithubOrganizationSyncServiceTestConstants {
    const val ORGANIZATION_NAME = "organization"
    const val ORG_PUBLIC_REPOS = 42
}

@DataJpaTest(includeFilters = [
    ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = [
            GithubOrganizationSyncService::class,
        ]
    )
])
@Import(TestBeanConfiguration::class)
@ComponentScan("com.productboard.gitsy.*")
@ActiveProfiles(value = ["test", "github-client-test"])
class GithubOrganizationSyncServiceTest(
    @Autowired private val organizationRepository: GithubOrganizationRepository,
    @Autowired private val repositoryRepository: GithubRepositoryRepository,
    @Autowired private val organizationSyncService: GithubOrganizationSyncService,
) {

    @Autowired
    lateinit var githubClient: GithubClient

    @Test
    fun successfullySynchronizeNewOrganization() {
        synchronize()
        validate()
    }

    @Test
    fun successfullySynchronizedExistingOrganization() {
        synchronize()
        synchronize()
        validate()
    }

    @Test
    fun failedSynchronizeNewOrganizationBlankOrganizationName() {
        assertThrows<IllegalArgumentException> {
            organizationSyncService.synchronizeOrganization(
                organizationName = ""
            )
        }
    }

    @Test
    fun successfullySynchronizeNewOrganizationNotFound() {
        every {
            githubClient.getOrganization(GithubOrganizationSyncServiceTestConstants.ORGANIZATION_NAME)
        }.returns(null)

        organizationSyncService.synchronizeOrganization(GithubOrganizationSyncServiceTestConstants.ORGANIZATION_NAME)

        val persistedOrganization = organizationRepository.findByName(
            GithubOrganizationSyncServiceTestConstants.ORGANIZATION_NAME,
        )

        assertNull(persistedOrganization)
    }

    private fun synchronize() {
        every {
            githubClient.getOrganization(GithubOrganizationSyncServiceTestConstants.ORGANIZATION_NAME)
        }.returns(
            GithubOrganizationResponseDto(
                name = GithubOrganizationSyncServiceTestConstants.ORGANIZATION_NAME,
                publicRepos = GithubOrganizationSyncServiceTestConstants.ORG_PUBLIC_REPOS,
            )
        )

        every {
            githubClient.getOrganizationRepositories(GithubOrganizationSyncServiceTestConstants.ORGANIZATION_NAME)
        }.returns(
            listOf(
                GithubRepositoryResponseDto(name = "repositoryOne"),
                GithubRepositoryResponseDto(name = "repositoryTwo"),
            )
        )

        every {
            githubClient.getRepositoryLanguages(
                RepositoryLanguageSynchronizationServiceTestConstants.ORGANIZATION_NAME,
                repositoryName = "repositoryOne",
            )
        }.returns(mapOf("Kotlin" to 42L))

        every {
            githubClient.getRepositoryLanguages(
                RepositoryLanguageSynchronizationServiceTestConstants.ORGANIZATION_NAME,
                repositoryName = "repositoryTwo",
            )
        }.returns(mapOf("Java" to 42L))

        organizationSyncService.synchronizeOrganization(GithubOrganizationSyncServiceTestConstants.ORGANIZATION_NAME)
    }

    private fun validate() {
        val persistedOrganization = organizationRepository.findByName(
            GithubOrganizationSyncServiceTestConstants.ORGANIZATION_NAME,
        )

        assertNotNull(persistedOrganization)

        val expectedOrganizationEntity = GithubOrganizationEntity(
            id = persistedOrganization!!.id,
            name = GithubOrganizationSyncServiceTestConstants.ORGANIZATION_NAME,
            publicRepos = GithubOrganizationSyncServiceTestConstants.ORG_PUBLIC_REPOS,
        )

        assertEquals(expectedOrganizationEntity, persistedOrganization)

        val persistedRepositoryEntities = repositoryRepository.findAllByOrganizationName(
            GithubOrganizationSyncServiceTestConstants.ORGANIZATION_NAME,
        )

        assertThat(persistedRepositoryEntities.size == 2)

        val expectedRepositoryEntities = listOf(
            GithubRepositoryEntity(
                id = persistedRepositoryEntities[0].id,
                name = "repositoryOne",
                organization = persistedOrganization,
            ),
            GithubRepositoryEntity(
                id = persistedRepositoryEntities[1].id,
                name = "repositoryTwo",
                organization = persistedOrganization,
            ),
        )

        assertThat(persistedRepositoryEntities).usingRecursiveComparison().isEqualTo(expectedRepositoryEntities)
    }
}
