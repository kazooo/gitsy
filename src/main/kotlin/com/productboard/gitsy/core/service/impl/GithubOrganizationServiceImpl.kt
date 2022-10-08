package com.productboard.gitsy.core.service.impl

import com.productboard.gitsy.core.domain.organization.GithubOrganizationDto
import com.productboard.gitsy.core.domain.organization.toDto
import com.productboard.gitsy.core.domain.organization.toEntity
import com.productboard.gitsy.core.repository.GithubOrganizationRepository
import com.productboard.gitsy.core.service.GithubOrganizationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Service providing interface for working with GitHub organizations.
 *
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
@Service
class GithubOrganizationServiceImpl(
    @Autowired private val organizationRepository: GithubOrganizationRepository,
) : GithubOrganizationService {

    override fun getOrganizationByName(organizationName: String): GithubOrganizationDto? {
        require(organizationName.isNotBlank()) { "|organizationName| can't be blank" }
        return organizationRepository.findByName(organizationName)?.toDto()
    }

    override fun save(organization: GithubOrganizationDto): GithubOrganizationDto =
        organizationRepository.save(organization.toEntity()).toDto()
}
