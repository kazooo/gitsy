package com.productboard.gitsy.language.repository

import com.productboard.gitsy.language.domain.original.RepositoryLanguagesEntity
import org.joda.time.Instant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository providing interface for database manipulation with language sets.
 *
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
@Repository
interface RepositoryLanguagesRepository : JpaRepository<RepositoryLanguagesEntity, Long> {
    /**
     * Searches for all language records according to their repository name.
     *
     * @param organizationName name of organization the given repository belongs to
     * @param repositoryName name of repository languages belong to
     * @return list of all existing language sets
     */
    fun findAllByRepositoryOrganizationNameAndRepositoryName(
        organizationName: String,
        repositoryName: String
    ): List<RepositoryLanguagesEntity>

    /**
     * Searches the latest language set by organization name and repository name.
     * @param organizationName name of organization the given repository belongs to
     * @param repositoryName name of repository languages belong to
     * @return the latest language set
     */
    fun findFirstByRepositoryOrganizationNameAndRepositoryNameOrderByCreatedOnDesc(
        organizationName: String,
        repositoryName: String,
    ): RepositoryLanguagesEntity?

    /**
     * Searches all language sets by organization name and repository name for the given date range.
     * @param organizationName name of organization the given repository belongs to
     * @param repositoryName name of repository languages belong to
     * @param from start of the date range
     * @param to end of the date range
     * @return list of all existing language sets
     */
    fun findAllByRepositoryOrganizationNameAndRepositoryNameAndCreatedOnBetween(
        organizationName: String,
        repositoryName: String,
        from: Instant,
        to: Instant,
    ): List<RepositoryLanguagesEntity>
}
