package com.productboard.gitsy.core.service

import com.productboard.gitsy.core.domain.organization.GithubOrganizationDto

/**
 * Service providing interface for working with GitHub organizations.
 *
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
interface GithubOrganizationService {

    /**
     * Gets organization by its name.
     *
     * @param organizationName name of organization
     * @return organization DTO or null if there are no repository with the given identifier
     */
    fun getOrganizationByName(organizationName: String): GithubOrganizationDto?

    /**
     * Persists the given organization DTO.
     *
     * @param organization organization DTO
     * @return persisted organization DTO with ID
     */
    fun save(organization: GithubOrganizationDto): GithubOrganizationDto
}
