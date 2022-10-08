package com.productboard.gitsy.core.repository

import com.productboard.gitsy.core.domain.organization.GithubOrganizationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository service for GitHub organization entities.
 *
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
@Repository
interface GithubOrganizationRepository : JpaRepository<GithubOrganizationEntity, Long> {

    /**
     * Searches for GitHub organization entity with the given name.
     *
     * @param organizationName name of organization to find
     * @return organization entity with the given name or null if no organization found
     */
    fun findByName(organizationName: String): GithubOrganizationEntity?

    /**
     * Deletes GitHub repository entity with the given name.
     *
     * @param organizationName name of organization to delete
     */
    fun deleteByName(organizationName: String)
}
