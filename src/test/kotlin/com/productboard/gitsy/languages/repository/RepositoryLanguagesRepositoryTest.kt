package com.productboard.gitsy.languages.repository

import com.productboard.gitsy.core.domain.organization.GithubOrganizationEntity
import com.productboard.gitsy.core.domain.repository.GithubRepositoryEntity
import com.productboard.gitsy.core.repository.GithubOrganizationRepository
import com.productboard.gitsy.core.repository.GithubRepositoryRepository
import com.productboard.gitsy.language.domain.original.RepositoryLanguagesEntity
import com.productboard.gitsy.language.repository.RepositoryLanguagesRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles

private object RepositoryLanguagesRepositoryTestConstants {
    const val ORGANIZATION_NAME = "organization"
    const val ORGANIZATION_REPOS = 42
    const val REPOSITORY_NAME = "repository"
    val LANGUAGES = mapOf(
        "Kotlin" to 42L,
        "Java" to 42L,
    )
}

@DataJpaTest
@ComponentScan(value = [
    "com.productboard.core.*",
    "com.productboard.languages.*",
])
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class RepositoryLanguagesRepositoryTest(
    @Autowired val organizationRepository: GithubOrganizationRepository,
    @Autowired val repositoryRepository: GithubRepositoryRepository,
    @Autowired val languagesRepository: RepositoryLanguagesRepository,
) {

    @Test
    fun successfullySaveLanguages() {
        val repositoryEntity = initRepository()
        repositoryEntity.languageHistory = listOf(
            languagesEntity(repositoryEntity),
            languagesEntity(repositoryEntity),
        )

        repositoryRepository.save(repositoryEntity)

        val languagesHistory = languagesRepository.findAllByRepositoryOrganizationNameAndRepositoryName(
            RepositoryLanguagesRepositoryTestConstants.ORGANIZATION_NAME,
            RepositoryLanguagesRepositoryTestConstants.REPOSITORY_NAME,
        )

        assertThat(languagesHistory.size == repositoryEntity.languageHistory.size)
    }

    @Test
    fun notFoundLanguages() {
        val languagesHistory = languagesRepository.findAllByRepositoryOrganizationNameAndRepositoryName(
            RepositoryLanguagesRepositoryTestConstants.ORGANIZATION_NAME,
            RepositoryLanguagesRepositoryTestConstants.REPOSITORY_NAME
        )
        assertThat(languagesHistory.isEmpty())
    }

    @Test
    fun successfullyDeleteLanguages() {
        val repositoryEntity = initRepository()
        repositoryEntity.languageHistory = listOf(
            languagesEntity(repositoryEntity),
            languagesEntity(repositoryEntity),
        )

        val persistedRepository = repositoryRepository.save(repositoryEntity)
        val languagesHistory = persistedRepository.languageHistory

        assertThat(languagesHistory.size == repositoryEntity.languageHistory.size)

        languagesRepository.deleteById(languagesHistory[0].id!!)

        val modifiedLanguagesHistory = languagesRepository.findAllByRepositoryOrganizationNameAndRepositoryName(
            RepositoryLanguagesRepositoryTestConstants.ORGANIZATION_NAME,
            RepositoryLanguagesRepositoryTestConstants.REPOSITORY_NAME
        )

        assertThat(modifiedLanguagesHistory.size == languagesHistory.size - 1)
    }

    @Test
    fun successfullyDeleteRepository() {
        val repositoryEntity = initRepository()
        repositoryEntity.languageHistory = listOf(
            languagesEntity(repositoryEntity),
            languagesEntity(repositoryEntity),
        )

        val persistedRepository = repositoryRepository.save(repositoryEntity)
        val languagesHistory = languagesRepository.findAllByRepositoryOrganizationNameAndRepositoryName(
            RepositoryLanguagesRepositoryTestConstants.ORGANIZATION_NAME,
            RepositoryLanguagesRepositoryTestConstants.REPOSITORY_NAME
        )

        assertThat(languagesHistory.size == repositoryEntity.languageHistory.size)

        repositoryRepository.deleteById(persistedRepository.id!!)
        val modifiedLanguagesHistory = languagesRepository.findAllByRepositoryOrganizationNameAndRepositoryName(
            RepositoryLanguagesRepositoryTestConstants.ORGANIZATION_NAME,
            RepositoryLanguagesRepositoryTestConstants.REPOSITORY_NAME
        )

        assertThat(modifiedLanguagesHistory.isEmpty())
    }

    private fun initRepository(): GithubRepositoryEntity {
        val organization = organization()
        organizationRepository.save(organization)
        return repository(organization)
    }

    private fun repository(organization: GithubOrganizationEntity): GithubRepositoryEntity =
        GithubRepositoryEntity(
            name = RepositoryLanguagesRepositoryTestConstants.REPOSITORY_NAME,
            organization = organization,
        )

    private fun organization(): GithubOrganizationEntity =
        GithubOrganizationEntity(
            name = RepositoryLanguagesRepositoryTestConstants.ORGANIZATION_NAME,
            publicRepos = RepositoryLanguagesRepositoryTestConstants.ORGANIZATION_REPOS,
        )

    private fun languagesEntity(repository: GithubRepositoryEntity): RepositoryLanguagesEntity =
        RepositoryLanguagesEntity(
            languageMap = RepositoryLanguagesRepositoryTestConstants.LANGUAGES,
            repository = repository,
        )
}
