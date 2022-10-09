package com.productboard.gitsy.core.service

import com.productboard.gitsy.core.domain.organization.GithubOrganizationEntity
import com.productboard.gitsy.core.domain.organization.toDto
import com.productboard.gitsy.core.domain.organization.toEntity
import com.productboard.gitsy.core.repository.GithubOrganizationRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.test.context.ActiveProfiles

private object GithubOrganizationServiceTestConstants {
    const val ORGANIZATION_NAME = "organization"
    const val ORG_PUBLIC_REPOS = 42
}

@DataJpaTest(includeFilters = [
    ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = [
            GithubOrganizationService::class,
        ]
    )
])
@ComponentScan("com.productboard.gitsy.*")
@ActiveProfiles("test")
class GithubOrganizationServiceTest(
    @Autowired private val organizationService: GithubOrganizationService,
    @Autowired private val organizationRepository: GithubOrganizationRepository,
) {

    @Test
    fun successfulSaveOrganizationDto() {
        val expectedDto = organizationService.save(organizationEntity().toDto())
        val actualEntity = organizationRepository.findByName(GithubOrganizationServiceTestConstants.ORGANIZATION_NAME)

        assertEquals(expectedDto.toEntity(), actualEntity)
    }

    @Test
    fun successfullyFindOrganizationByName() {
        val expectedEntity = organizationRepository.save(organizationEntity())
        val expectedDto = expectedEntity.toDto()

        assertEquals(
            expectedDto,
            organizationService.getOrganizationByName(GithubOrganizationServiceTestConstants.ORGANIZATION_NAME)
        )
    }

    @Test
    fun failedFindOrganizationByNameBlankOrganizationName() {
        assertThrows<IllegalArgumentException> {
            organizationService.getOrganizationByName(
                organizationName = "",
            )
        }
    }

    private fun organizationEntity() =
        GithubOrganizationEntity(
            name = GithubOrganizationServiceTestConstants.ORGANIZATION_NAME,
            publicRepos = GithubOrganizationServiceTestConstants.ORG_PUBLIC_REPOS,
        )
}
