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
     * @param repositoryName name of repository to find
     * @return repository entity with the given name or null if no repository found
     */
    fun findByName(repositoryName: String): GithubRepositoryEntity?
}
