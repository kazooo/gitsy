package com.productboard.gitsy.languages.domain

import com.productboard.gitsy.core.domain.organization.GithubOrganizationEntity
import com.productboard.gitsy.core.domain.repository.GithubRepositoryEntity
import com.productboard.gitsy.language.domain.RepositoryLanguagesDto
import com.productboard.gitsy.language.domain.RepositoryLanguagesEntity
import com.productboard.gitsy.language.domain.toDto
import com.productboard.gitsy.language.domain.toEntity
import com.productboard.gitsy.language.domain.toRepositoryLanguagesDto
import org.joda.time.Instant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

object RepositoryLanguagesMapperTestConstants {
    const val ORGANIZATION_NAME = "organization"
    const val REPOSITORY_NAME = "repository"
    const val ORG_PUBLIC_REPOS = 42
    val LANGUAGES = mapOf(
        "Kotlin" to 42L,
        "Java" to 42L,
    )
}

class RepositoryLanguagesMapperTest {

    @Test
    fun successfullyMapEntityToDto() {
        val timestamp = Instant.now()

        val expected = RepositoryLanguagesDto(
            languageMap = RepositoryLanguagesMapperTestConstants.LANGUAGES,
            createdOn = timestamp,
        )

        val obj = RepositoryLanguagesEntity(
            languageMap = RepositoryLanguagesMapperTestConstants.LANGUAGES,
            repository = GithubRepositoryEntity(
                name = RepositoryLanguagesMapperTestConstants.REPOSITORY_NAME,
                organization = GithubOrganizationEntity(
                    name = RepositoryLanguagesMapperTestConstants.ORGANIZATION_NAME,
                    publicRepos = RepositoryLanguagesMapperTestConstants.ORG_PUBLIC_REPOS,
                )
            ),
            createdOn = timestamp,
        )

        assertEquals(expected, obj.toDto())
    }

    @Test
    fun successfullyMapDtoToEntity() {
        val repository = GithubRepositoryEntity(
            name = RepositoryLanguagesMapperTestConstants.REPOSITORY_NAME,
            organization = GithubOrganizationEntity(
                name = RepositoryLanguagesMapperTestConstants.ORGANIZATION_NAME,
                publicRepos = RepositoryLanguagesMapperTestConstants.ORG_PUBLIC_REPOS,
            )
        )

        val expected = RepositoryLanguagesEntity(
            languageMap = RepositoryLanguagesMapperTestConstants.LANGUAGES,
            repository = repository,
        )

        val obj = RepositoryLanguagesDto(
            languageMap = RepositoryLanguagesMapperTestConstants.LANGUAGES,
            createdOn = Instant.now(),
        )

        assertEquals(expected, obj.toEntity(repository))
    }

    @Test
    fun successfullyMapCommonMapToLanguagesDto() {
        val timestamp = Instant.now()

        val expected = RepositoryLanguagesDto(
            languageMap = RepositoryLanguagesMapperTestConstants.LANGUAGES,
            createdOn = timestamp,
        )

        val actual = RepositoryLanguagesMapperTestConstants.LANGUAGES.toRepositoryLanguagesDto(timestamp)

        assertEquals(expected, actual)
    }
}