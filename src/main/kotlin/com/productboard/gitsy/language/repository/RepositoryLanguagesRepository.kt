package com.productboard.gitsy.language.repository

import com.productboard.gitsy.language.domain.RepositoryLanguagesEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
@Repository
interface RepositoryLanguagesRepository : JpaRepository<RepositoryLanguagesEntity, Long> {
    /**
     * Searches for all language records according to their repository name.
     *
     * @param repositoryName name of repository languages belong to
     */
    fun findByRepositoryName(repositoryName: String): List<RepositoryLanguagesEntity>
}
