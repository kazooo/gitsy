package com.productboard.gitsy.core.service

import com.productboard.gitsy.core.domain.repository.GithubRepositoryDto

/**
 * Service providing interface for working with GitHub repositories.
 *
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
interface GithubRepositoryService {

    /**
     * Gets repository by its name and organization name that owns it.
     *
     * @param organizationName name of organization
     * @param repositoryName name of repository
     * @return repository DTO or null if there are no repository with the given identifiers
     */
    fun getRepository(organizationName: String, repositoryName: String): GithubRepositoryDto?

    /**
     * Gets all repositories that belong to the givent organization.
     *
     * @param organizationName name of organization
     * @return list of repositories that belong to the given organization
     */
    fun getAllRepositories(organizationName: String): List<GithubRepositoryDto>

    /**
     * Persists the given repository DTO.
     *
     * @param repository repository DTO
     * @return persisted repository DTO with ID
     */
    fun save(repository: GithubRepositoryDto): GithubRepositoryDto
}
