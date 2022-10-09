package com.productboard.gitsy.language.service.impl

import com.productboard.gitsy.core.domain.repository.toEntity
import com.productboard.gitsy.core.service.GithubRepositoryService
import com.productboard.gitsy.language.domain.calculation.CalculatedLanguageSet
import com.productboard.gitsy.language.domain.original.RepositoryLanguagesDto
import com.productboard.gitsy.language.domain.original.RepositoryLanguagesEntity
import com.productboard.gitsy.language.domain.original.toDto
import com.productboard.gitsy.language.domain.original.toEntity
import com.productboard.gitsy.language.repository.RepositoryLanguagesRepository
import com.productboard.gitsy.language.service.RepositoryLanguagesService
import org.joda.time.Instant
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Implementation of service providing calculating operations for repository languages.
 *
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
@Service
class RepositoryLanguagesServiceImpl(
    @Autowired private val repositoryService: GithubRepositoryService,
    @Autowired private val languagesRepository: RepositoryLanguagesRepository,
) : RepositoryLanguagesService {

    override fun getLatestLanguageSet(organizationName: String, repositoryName: String): CalculatedLanguageSet? {
        require(organizationName.isNotBlank()) { "|organizationName| can't be blank" }
        require(repositoryName.isNotBlank()) { "|repositoryName| can't be blank" }

        val languageSet = findLatestLanguageSet(organizationName, repositoryName)
        return if (languageSet == null) {
            null
        } else {
            calculateLanguageSet(languageSet.toDto())
        }
    }

    override fun getLanguageEvolution(
        organizationName: String,
        repositoryName: String,
    ): List<CalculatedLanguageSet> {
        require(organizationName.isNotBlank()) { "|organizationName| can't be blank" }
        require(repositoryName.isNotBlank()) { "|repositoryName| can't be blank" }

        val languageSetList = languagesRepository.findAllByRepositoryOrganizationNameAndRepositoryName(
            organizationName,
            repositoryName
        )
        return languageSetList.map { calculateLanguageSet(it.toDto()) }
    }

    override fun getLanguageEvolution(
        organizationName: String,
        repositoryName: String,
        from: Instant,
        to: Instant
    ): List<CalculatedLanguageSet> {
        require(from.isBefore(to) || from == to) { "|to| can't be before |from|" }
        require(organizationName.isNotBlank()) { "|organizationName| can't be blank" }
        require(repositoryName.isNotBlank()) { "|repositoryName| can't be blank" }

        val languageSetList = findLanguageSetsForDateRange(
            organizationName,
            repositoryName,
            from,
            to,
        )
        return languageSetList.map { calculateLanguageSet(it.toDto()) }
    }

    override fun getAggregatedLanguageSetForOrganization(organizationName: String): CalculatedLanguageSet? {
        require(organizationName.isNotBlank()) { "|organizationName| can't be blank" }

        val repositories = repositoryService.getAllRepositories(organizationName)
        val latestLanguageSets = repositories.mapNotNull { findLatestLanguageSet(organizationName, it.name) }
        if (latestLanguageSets.isEmpty()) {
            return null
        } else {
            return mergeLanguageSets(latestLanguageSets)
        }
    }

    private fun calculateLanguageSet(languageSet: RepositoryLanguagesDto): CalculatedLanguageSet {
        val calculatedLanguageSet: Map<String, Double> = calculatePercentage(languageSet.languageMap)
        return CalculatedLanguageSet(calculatedLanguageSet, timestamp = languageSet.createdOn)
    }

    private fun calculatePercentage(languageMap: Map<String, Long>): Map<String, Double> {
        val totalBytes = languageMap.values.sum()
        return languageMap.mapValues { (it.value.toDouble() / totalBytes).roundTo(2) }
    }

    private fun mergeLanguageSets(languageSetList: List<RepositoryLanguagesEntity>): CalculatedLanguageSet {
        val aggregatedLanguageSet: Map<String, Long> = languageSetList
            .map { it.languageMap }
            .fold(hashMapOf()) { acc, curr ->
                curr.forEach { entry ->
                    acc.merge(entry.key, entry.value) { new, old -> new + old }
                }
                acc
            }

        return CalculatedLanguageSet(
            calculatePercentage(aggregatedLanguageSet),
            languageSetList.maxOf { it.createdOn }
        )
    }

    override fun save(languagesDto: RepositoryLanguagesDto): RepositoryLanguagesDto {
        val repositoryEntity = languagesDto.repository!!.toEntity()
        return languagesRepository.save(languagesDto.toEntity(repositoryEntity)).toDto()
    }

    /**
     * Alias for "findFirstByRepositoryOrganizationNameAndRepositoryNameOrderByCreatedOnDesc method".
     */
    private fun findLatestLanguageSet(organizationName: String, repositoryName: String) =
        languagesRepository.findFirstByRepositoryOrganizationNameAndRepositoryNameOrderByCreatedOnDesc(
            organizationName,
            repositoryName
        )

    /**
     * Alias for "findAllByRepositoryOrganizationNameAndRepositoryNameAndCreatedOnBetween" method.
     */
    private fun findLanguageSetsForDateRange(
        organizationName: String,
        repositoryName: String,
        from: Instant,
        to: Instant,
    ) = languagesRepository.findAllByRepositoryOrganizationNameAndRepositoryNameAndCreatedOnBetween(
        organizationName,
        repositoryName,
        from,
        to,
    )

    private fun Double.roundTo(numFractionDigits: Int): Double {
        val factor = 10.0.pow(numFractionDigits.toDouble())
        return (this * factor).roundToInt() / factor
    }
}
