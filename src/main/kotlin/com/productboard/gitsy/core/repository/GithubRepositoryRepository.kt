package com.productboard.gitsy.core.repository

import com.productboard.gitsy.core.domain.repository.GithubRepositoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository service for GitHub repository entities.
 *
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
@Repository
interface GithubRepositoryRepository : JpaRepository<GithubRepositoryEntity, Long> {

    /**
     * Searches for GitHub repository entity with the given name.
     *
     * @param organizationName name of organization the required repository belongs to
     * @param repositoryName name of repository to find
     * @return repository entity with the given name or null if no repository found
     */
    fun findByOrganizationNameAndName(organizationName: String, repositoryName: String): GithubRepositoryEntity?

    /**
     * Searches for all repositories that the given organization owns.
     *
     * @param organizationName name of organization
     * @return list of for all repositories that the given organization owns.
     */
    fun findAllByOrganizationName(organizationName: String): List<GithubRepositoryEntity>
}
