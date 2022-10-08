package com.productboard.gitsy.core.domain.organization

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

object GithubOrganizationMapperTestConstants {
    const val ORGANIZATION_NAME = "organization"
    const val ORG_PUBLIC_REPOS = 42
}

class GithubOrganizationMapperTest {

    @Test
    fun successfullyMapOrganizationDtoToEntity() {
        val expected = GithubOrganizationEntity(
            name = GithubOrganizationMapperTestConstants.ORGANIZATION_NAME,
            publicRepos = GithubOrganizationMapperTestConstants.ORG_PUBLIC_REPOS,
        )

        val obj = GithubOrganizationDto(
            name = GithubOrganizationMapperTestConstants.ORGANIZATION_NAME,
            publicRepos = GithubOrganizationMapperTestConstants.ORG_PUBLIC_REPOS,
        )

        assertEquals(expected, obj.toEntity())
    }

    @Test
    fun successfullyMapOrganizationInternalDtoToEntity() {
        val expected = GithubOrganizationDto(
            name = GithubOrganizationMapperTestConstants.ORGANIZATION_NAME,
            publicRepos = GithubOrganizationMapperTestConstants.ORG_PUBLIC_REPOS,
        )

        val obj = GithubOrganizationEntity(
            name = GithubOrganizationMapperTestConstants.ORGANIZATION_NAME,
            publicRepos = GithubOrganizationMapperTestConstants.ORG_PUBLIC_REPOS,
        )

        assertEquals(expected, obj.toDto())
    }

    @Test
    fun successfullyMapOrganizationResponseDtoToInternalDto() {
        val expected = GithubOrganizationDto(
            name = GithubOrganizationMapperTestConstants.ORGANIZATION_NAME,
            publicRepos = GithubOrganizationMapperTestConstants.ORG_PUBLIC_REPOS,
        )

        val obj = GithubOrganizationResponseDto(
            name = GithubOrganizationMapperTestConstants.ORGANIZATION_NAME,
            publicRepos = GithubOrganizationMapperTestConstants.ORG_PUBLIC_REPOS,
        )

        assertEquals(expected, obj.toDto())
    }
}