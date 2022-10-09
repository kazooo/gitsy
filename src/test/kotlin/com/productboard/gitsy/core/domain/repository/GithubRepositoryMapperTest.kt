package com.productboard.gitsy.core.domain.repository

import com.productboard.gitsy.core.domain.organization.GithubOrganizationDto
import com.productboard.gitsy.core.domain.organization.GithubOrganizationEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private object GithubRepositoryMapperTestConstants {
    const val ORGANIZATION_NAME = "organization"
    const val REPOSITORY_NAME = "repository"
    const val ORG_PUBLIC_REPOS = 42
}

class GithubRepositoryMapperTest {

    @Test
    fun successfullyMapRepositoryDtoToEntity() {
        val expected = GithubRepositoryEntity(
            name = GithubRepositoryMapperTestConstants.REPOSITORY_NAME,
            organization = GithubOrganizationEntity(
                name = GithubRepositoryMapperTestConstants.ORGANIZATION_NAME,
                publicRepos = GithubRepositoryMapperTestConstants.ORG_PUBLIC_REPOS,
            )
        )

        val obj = GithubRepositoryDto(
            name = GithubRepositoryMapperTestConstants.REPOSITORY_NAME,
            organization = GithubOrganizationDto(
                name = GithubRepositoryMapperTestConstants.ORGANIZATION_NAME,
                publicRepos = GithubRepositoryMapperTestConstants.ORG_PUBLIC_REPOS,
            )
        )

        assertEquals(expected, obj.toEntity())
    }

    @Test
    fun successfullyMapRepositoryEntityToDto() {
        val expected = GithubRepositoryDto(
            name = GithubRepositoryMapperTestConstants.REPOSITORY_NAME,
            organization = GithubOrganizationDto(
                name = GithubRepositoryMapperTestConstants.ORGANIZATION_NAME,
                publicRepos = GithubRepositoryMapperTestConstants.ORG_PUBLIC_REPOS,
            )
        )

        val obj = GithubRepositoryEntity(
            name = GithubRepositoryMapperTestConstants.REPOSITORY_NAME,
            organization = GithubOrganizationEntity(
                name = GithubRepositoryMapperTestConstants.ORGANIZATION_NAME,
                publicRepos = GithubRepositoryMapperTestConstants.ORG_PUBLIC_REPOS,
            )
        )

        assertEquals(expected, obj.toDto())
    }

    @Test
    fun successfullyMapRepositoryResponseDtoToInternalDto() {
        val expected = GithubRepositoryDto(
            name = GithubRepositoryMapperTestConstants.REPOSITORY_NAME,
        )

        val obj = GithubRepositoryResponseDto(name = GithubRepositoryMapperTestConstants.REPOSITORY_NAME)

        assertEquals(expected, obj.toDto())
    }
}
