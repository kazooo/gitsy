package com.productboard.gitsy.core.service

import com.productboard.gitsy.core.domain.organization.GithubOrganizationEntity
import com.productboard.gitsy.core.domain.repository.GithubRepositoryEntity
import com.productboard.gitsy.core.domain.repository.toDto
import com.productboard.gitsy.core.domain.repository.toEntity
import com.productboard.gitsy.core.repository.GithubOrganizationRepository
import com.productboard.gitsy.core.repository.GithubRepositoryRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.test.context.ActiveProfiles

object GithubRepositoryServiceTestConstants {
    const val ORGANIZATION_NAME = "organization"
    const val ORG_PUBLIC_REPOS = 42
    const val REPOSITORY_NAME = "repository"
}

@DataJpaTest(includeFilters = [
    ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = [
            GithubRepositoryService::class,
        ]
    )
])
@ComponentScan("com.productboard.gitsy.*")
@ActiveProfiles("test")
class GithubRepositoryServiceTest(
    @Autowired private val repositoryService: GithubRepositoryService,
    @Autowired private val repositoryRepository: GithubRepositoryRepository,
    @Autowired private val organizationRepository: GithubOrganizationRepository,
) {
    @Test
    fun successfulSaveRepositoryDto() {
        val expectedDto = repositoryService.save(repositoryEntity().toDto())
        val actualEntity = repositoryRepository.findByOrganizationNameAndName(
            GithubRepositoryServiceTestConstants.ORGANIZATION_NAME,
            GithubRepositoryServiceTestConstants.REPOSITORY_NAME,
        )

        Assertions.assertEquals(expectedDto.toEntity(), actualEntity)
    }

    @Test
    fun successfullyFindByName() {
        val expectedEntity = repositoryRepository.save(repositoryEntity())
        val expectedDto = expectedEntity.toDto()

        Assertions.assertEquals(
            expectedDto,
            repositoryService.getRepository(
                GithubRepositoryServiceTestConstants.ORGANIZATION_NAME,
                GithubRepositoryServiceTestConstants.REPOSITORY_NAME,
            )
        )
    }

    @Test
    fun successfullyFindByOrganization() {
        val expectedEntity1 = repositoryRepository.save(repositoryEntity())
        val expectedEntity2 = repositoryRepository.save(repositoryEntity())
        val expected = listOf(expectedEntity1, expectedEntity2).map { it.toDto() }
        val actual = repositoryService.getAllRepositories(
            GithubRepositoryServiceTestConstants.ORGANIZATION_NAME,
        )

        Assertions.assertEquals(expected, actual)
    }

    private fun repositoryEntity(): GithubRepositoryEntity {
        val organizationEntity = GithubOrganizationEntity(
            name = GithubRepositoryServiceTestConstants.ORGANIZATION_NAME,
            publicRepos = GithubRepositoryServiceTestConstants.ORG_PUBLIC_REPOS,
        )
        val persistedEntity = organizationRepository.save(organizationEntity)
        return GithubRepositoryEntity(
            name = GithubRepositoryServiceTestConstants.REPOSITORY_NAME,
            organization = persistedEntity,
        )
    }
}
