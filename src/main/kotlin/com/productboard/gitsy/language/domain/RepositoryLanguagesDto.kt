package com.productboard.gitsy.language.domain

import org.joda.time.Instant

/**
 * DTO representing repository language statistic for the particular time.
 *
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
data class RepositoryLanguagesDto(
    /**
     * Map containing information about languages and number of bytes they take in repository.
     */
    val languageMap: Map<String, Long>,

    /**
     * Timestamp representing the time this record has been created.
     */
    val createdOn: Instant,
)
