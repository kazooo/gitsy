package com.productboard.gitsy.core.repository

import com.productboard.gitsy.core.domain.organization.GithubOrganizationEntity
import com.productboard.gitsy.core.domain.repository.GithubRepositoryEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles

private object GithubOrganizationRepositoryTestConstants {
    const val ORGANIZATION_NAME = "organization"
    const val ORGANIZATION_REPOS = 42
    const val REPOSITORY_NAME = "repository"
}

@DataJpaTest
@ComponentScan("com.productboard.core.*")
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class GithubCoreRepositoryTest(
    @Autowired val organizationRepository: GithubOrganizationRepository,
    @Autowired val repositoryRepository: GithubRepositoryRepository,
) {

    @Test
    fun successfullySaveToRepository() {
        val entity = GithubOrganizationEntity(
            name = GithubOrganizationRepositoryTestConstants.ORGANIZATION_NAME,
            publicRepos = GithubOrganizationRepositoryTestConstants.ORGANIZATION_REPOS,
        )
        entity.repositories = listOf(
            GithubRepositoryEntity(
                name = GithubOrganizationRepositoryTestConstants.REPOSITORY_NAME,
                organization = entity,
            )
        )

        organizationRepository.save(entity)

        val persisted = organizationRepository.findByName(
            GithubOrganizationRepositoryTestConstants.ORGANIZATION_NAME
        )

        assertThat(persisted).isNotNull
        assertThat(persisted!!.id).isNotNull
        assertEquals(entity, persisted)

        val persistedRepository = repositoryRepository.findByName(
            GithubOrganizationRepositoryTestConstants.REPOSITORY_NAME
        )

        assertThat(persistedRepository).isNotNull
        assertThat(persistedRepository!!.id).isNotNull
    }

    @Test
    fun notFoundInRepository() {
        val persisted = organizationRepository.findByName(
            GithubOrganizationRepositoryTestConstants.ORGANIZATION_NAME
        )

        assertThat(persisted).isNull()
    }

    @Test
    fun successfullyDeleteByNameOrganizationCascade() {
        val entity = GithubOrganizationEntity(
            name = GithubOrganizationRepositoryTestConstants.ORGANIZATION_NAME,
            publicRepos = GithubOrganizationRepositoryTestConstants.ORGANIZATION_REPOS,
        )
        entity.repositories = listOf(
            GithubRepositoryEntity(
                name = GithubOrganizationRepositoryTestConstants.REPOSITORY_NAME,
                organization = entity,
            )
        )

        val persisted = organizationRepository.save(entity)

        assertThat(persisted).isNotNull
        assertThat(persisted.id).isNotNull

        organizationRepository.deleteByName(GithubOrganizationRepositoryTestConstants.ORGANIZATION_NAME)

        val deletedOrganization = organizationRepository.findByName(
            GithubOrganizationRepositoryTestConstants.ORGANIZATION_NAME
        )

        assertThat(deletedOrganization).isNull()

        val deletedRepository = repositoryRepository.findByName(
            GithubOrganizationRepositoryTestConstants.REPOSITORY_NAME
        )

        assertThat(deletedRepository).isNull()
    }
}
