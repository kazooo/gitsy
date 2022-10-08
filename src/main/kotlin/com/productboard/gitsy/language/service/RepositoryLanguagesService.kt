package com.productboard.gitsy.language.service

import com.productboard.gitsy.language.domain.calculation.CalculatedLanguageSet
import com.productboard.gitsy.language.domain.original.RepositoryLanguagesDto
import org.joda.time.Instant

/**
 * Service providing calculating operations for repository languages.
 *
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
interface RepositoryLanguagesService {

    /**
     * Extract and calculate the latest language set for the given repository.
     *
     * @param organizationName name of organization the repository belongs to
     * @param repositoryName name of repository
     * @return calculated language set with percentage for each language
     */
    fun getLatestLanguageSet(organizationName: String, repositoryName: String): CalculatedLanguageSet?

    /**
     * Extracts and calculates all existing language sets for the given repository.
     *
     * @param organizationName name of organization the repository belongs to
     * @param repositoryName name of repository
     * @return list of calculated language sets with percentage for each language
     */
    fun getLanguageEvolution(organizationName: String, repositoryName: String): List<CalculatedLanguageSet>

    /**
     * Extracts and calculates all existing language sets for the given repository for the given date range.
     *
     * @param organizationName name of organization the repository belongs to
     * @param repositoryName name of repository
     * @param from start of date range
     * @param to end of date range
     * @return list of calculated language sets with percentage for each language
     */
    fun getLanguageEvolution(
        organizationName: String,
        repositoryName: String,
        from: Instant,
        to: Instant
    ): List<CalculatedLanguageSet>

    /**
     * Aggregates all the latest language sets across each repository the given organization owns.
     *
     * @param organizationName name of organization
     * @return aggregated language set for the given organization across each repository
     */
    fun getAggregatedLanguageSetForOrganization(organizationName: String): CalculatedLanguageSet?

    /**
     * Persists language set DTO.
     *
     * @param languagesDto language set DTO to persist
     * @return persisted language DTO with ID
     */
    fun save(languagesDto: RepositoryLanguagesDto): RepositoryLanguagesDto
}
