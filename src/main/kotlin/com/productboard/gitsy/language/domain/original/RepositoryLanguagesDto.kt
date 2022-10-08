package com.productboard.gitsy.language.domain.original

import com.productboard.gitsy.core.domain.repository.GithubRepositoryDto
import org.joda.time.Instant

/**
 * DTO representing repository language statistic for the particular time.
 *
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
data class RepositoryLanguagesDto(
    /**
     * ID of persisted record
     */
    val id: Long? = null,

    /**
     * Map containing information about languages and number of bytes they take in repository.
     */
    val languageMap: Map<String, Long>,

    /**
     * Repository the given language set belongs to.
     */
    var repository: GithubRepositoryDto? = null,

    /**
     * Timestamp representing the time this record has been created.
     */
    var createdOn: Instant,
)
