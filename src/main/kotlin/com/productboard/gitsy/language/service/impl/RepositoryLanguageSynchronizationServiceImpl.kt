package com.productboard.gitsy.language.service.impl

import com.productboard.gitsy.core.api.GithubClient
import com.productboard.gitsy.core.domain.repository.GithubRepositoryDto
import com.productboard.gitsy.language.domain.original.toRepositoryLanguagesDto
import com.productboard.gitsy.language.service.RepositoryLanguageSynchronizationService
import com.productboard.gitsy.language.service.RepositoryLanguagesService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger { }

/**
 * Implementation of service providing synchronization operations for repository languages.
 *
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
@Service
class RepositoryLanguageSynchronizationServiceImpl(
    @Autowired private val githubClient: GithubClient,
    @Autowired private val languageService: RepositoryLanguagesService,
) : RepositoryLanguageSynchronizationService {

    override fun synchronizeRepositoryLanguages(repository: GithubRepositoryDto) {
        val organizationName = repository.organization!!.name
        val repositoryName = repository.name

        require(organizationName.isNotBlank()) { "|organizationName| can't be blank" }
        require(repositoryName.isNotBlank()) { "|repositoryName| can't be blank" }

        /* fetch information about languages */
        logger.trace { "Getting information about repository languages..." }
        val languagesResponse = githubClient.getRepositoryLanguages(
            repositoryOwner = organizationName,
            repositoryName = repositoryName,
        )

        if (languagesResponse.isEmpty()) {
            logger.info {
                "GitHub returned an empty language set for repository $repositoryName (owned by $organizationName)."
            }
            return
        }

        /* persist language set */
        logger.trace { "Persisting a new language set record..." }
        val languagesDto = languagesResponse.toRepositoryLanguagesDto()
        languagesDto.repository = repository
        languageService.save(languagesDto)
        logger.debug { "Finished processing language set for $repositoryName." }
    }
}
