package com.productboard.gitsy.core.domain.repository

import com.productboard.gitsy.language.domain.RepositoryLanguagesDto

/**
 * DTO representing a GitHub repository.
 *
 * @author Aleksei Ermak
 * @date 07.10.2022
 */
data class GithubRepositoryDto(
    /**
     * Name of GitHub repository.
     */
    val name: String,

    /**
     * History of evolution of languages that are used in the given repository.
     * Sorted by DTO created timestamp.
     */
    val languageHistory: List<RepositoryLanguagesDto> = emptyList()
)
