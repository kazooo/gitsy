package com.productboard.gitsy.language.domain.original

import com.productboard.gitsy.core.domain.repository.GithubRepositoryEntity
import com.productboard.gitsy.core.domain.repository.toDto
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
        id = id,
        languageMap = languageMap,
        repository = repository.toDto(),
        createdOn = createdOn,
    )

/**
 * Maps languages DTO to entity.
 */
fun RepositoryLanguagesDto.toEntity(repositoryEntity: GithubRepositoryEntity) =
    RepositoryLanguagesEntity(
        id = id,
        repository = repositoryEntity,
        languageMap = languageMap,
        createdOn = createdOn,
    )
