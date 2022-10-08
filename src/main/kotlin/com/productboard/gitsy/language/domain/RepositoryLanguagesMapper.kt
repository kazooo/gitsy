package com.productboard.gitsy.language.domain

import com.productboard.gitsy.core.domain.repository.GithubRepositoryEntity
import org.joda.time.Instant

/**
 * Maps common maps to languages DTO.
 */
fun Map<String, Long>.toRepositoryLanguagesDto(createdOn: Instant = Instant.now()) =
    RepositoryLanguagesDto(
        languageMap = this,
        createdOn = createdOn,
    )

/**
 * Maps languages entity to DTO.
 */
fun RepositoryLanguagesEntity.toDto() =
    RepositoryLanguagesDto(
        createdOn = createdOn,
        languageMap = languageMap,
    )

/**
 * Maps languages DTO to entity.
 */
fun RepositoryLanguagesDto.toEntity(repository: GithubRepositoryEntity) =
    RepositoryLanguagesEntity(
        repository = repository,
        languageMap = languageMap,
    )
