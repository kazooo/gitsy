package com.productboard.gitsy.language.service

import com.productboard.gitsy.core.domain.repository.GithubRepositoryDto

/**
 * Service providing synchronization operations for repository languages.
 *
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
interface RepositoryLanguageSynchronizationService {

    /**
     * Fetch the latest information about repository languages and synchronizes database.
     *
     * @param repository GitHub repository DTO
     */
    fun synchronizeRepositoryLanguages(repository: GithubRepositoryDto)
}
