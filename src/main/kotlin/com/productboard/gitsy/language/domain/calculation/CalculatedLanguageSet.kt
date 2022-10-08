package com.productboard.gitsy.language.domain.calculation

import org.joda.time.Instant

/**
 * DTO representing language set with calculated value of percentage for each language used in GitHub repository.
 *
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
data class CalculatedLanguageSet(
    /**
     * Map containing languages and the corresponding percentage of code they take in GitHub repository.
     */
    val languageMap: Map<String, Double>,

    /**
     * Timestamp representing the time this record has been created.
     */
    val timestamp: Instant = Instant.now()
)
