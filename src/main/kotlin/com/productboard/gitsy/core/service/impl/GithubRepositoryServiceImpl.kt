package com.productboard.gitsy.core.service.impl

import com.productboard.gitsy.core.domain.repository.GithubRepositoryDto
import com.productboard.gitsy.core.domain.repository.toDto
import com.productboard.gitsy.core.domain.repository.toEntity
import com.productboard.gitsy.core.repository.GithubRepositoryRepository
import com.productboard.gitsy.core.service.GithubRepositoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Implementation of service providing interface for working with GitHub organizations.
 *
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
@Service
class GithubRepositoryServiceImpl(
    @Autowired private val repositoryRepository: GithubRepositoryRepository,
) : GithubRepositoryService {

    override fun getRepository(organizationName: String, repositoryName: String): GithubRepositoryDto? =
        repositoryRepository.findByOrganizationNameAndName(organizationName, repositoryName)?.toDto()

    override fun getAllRepositories(organizationName: String): List<GithubRepositoryDto> =
        repositoryRepository.findAllByOrganizationName(organizationName).map { it.toDto() }

    override fun save(repository: GithubRepositoryDto): GithubRepositoryDto =
        repositoryRepository.save(repository.toEntity()).toDto()
}
