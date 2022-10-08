package com.productboard.gitsy.core.service.impl

import com.productboard.gitsy.core.api.GithubClient
import com.productboard.gitsy.core.domain.organization.toDto
import com.productboard.gitsy.core.domain.repository.GithubRepositoryDto
import com.productboard.gitsy.core.domain.repository.toDto
import com.productboard.gitsy.core.service.GithubOrganizationService
import com.productboard.gitsy.core.service.GithubOrganizationSyncService
import com.productboard.gitsy.core.service.GithubRepositoryService
import com.productboard.gitsy.language.service.RepositoryLanguageSynchronizationService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger { }

/**
 * Service providing interface for organization synchronization operations.
 *
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
@Service
class GithubOrganizationSyncServiceImpl(
    @Autowired private val githubClient: GithubClient,
    @Autowired private val organizationService: GithubOrganizationService,
    @Autowired private val repositoryService: GithubRepositoryService,
    @Autowired private val languageSyncService: RepositoryLanguageSynchronizationService,
) : GithubOrganizationSyncService {

    override fun synchronizeOrganization(organizationName: String) {
        require(organizationName.isNotBlank()) { "|organizationName| can't be blank" }
        logger.trace { "Synchronizing organization $organizationName..." }

        /* get information about the organization via GitHub REST API */
        val organizationResponse = githubClient.getOrganization(organizationName)
        if (organizationResponse == null) {
            logger.info { "Can't get any information about organization $organizationName!" }
            return
        } else {
            logger.trace { "Found information about organization $organizationName." }
        }

        /* persist organization information if it doesn't exist in database */
        val organizationDto = organizationResponse.toDto()
        var persistedOrganization = organizationService.getOrganizationByName(organizationName)
        /* set id of persisted record to properly compare the states of records */
        organizationDto.id = persistedOrganization?.id
        /* check persisted entity state */
        if (persistedOrganization == null || persistedOrganization != organizationDto) {
            logger.debug {
                "There are no records about organization $organizationName in database, creating new one..."
            }
            persistedOrganization = organizationService.save(organizationDto)
        } else {
            logger.trace { "Found a record about the given organization $organizationName in database." }
        }

        /* synchronize repositories */
        logger.trace { "Synchronizing repositories of $organizationName..." }
        val repositoriesResponse = githubClient.getOrganizationRepositories(organizationName)
        repositoriesResponse
            .map { it.toDto() }
            .onEach { it.organization = persistedOrganization }
            .forEach { synchronizeRepository(it) }

        /* you can add any additional synchronization below */
    }

    private fun synchronizeRepository(repository: GithubRepositoryDto) {
        val organizationName = repository.organization!!.name
        val repositoryName = repository.name

        /* store information about repository if there is no record about it */
        var persistedRepository = repositoryService.getRepository(organizationName, repositoryName)
        /* set id of persisted record to properly compare the states of records */
        repository.id = persistedRepository?.id
        /* check persisted entity state */
        if (persistedRepository == null || persistedRepository != repository) {
            logger.trace { "There is no record about repository $repositoryName, creating new one..." }
            persistedRepository = repositoryService.save(repository)
        } else {
            logger.trace { "Found a record about the given repository $repositoryName in database." }
        }

        /* fetch actual information about languages used in repository */
        languageSyncService.synchronizeRepositoryLanguages(persistedRepository)

        /* you can add any additional synchronization below */
    }
}
