package com.productboard.gitsy.core.domain.repository

import com.productboard.gitsy.core.domain.organization.toDto
import com.productboard.gitsy.core.domain.organization.toEntity
import com.productboard.gitsy.language.domain.original.toEntity

/**
 * Maps GitHub repository entity to DTO
 */
fun GithubRepositoryEntity.toDto() =
    GithubRepositoryDto(
        id = id,
        name = name,
        organization = organization.toDto()
    )

/**
 * Maps GitHub repository DTO to entity
 */
fun GithubRepositoryDto.toEntity(): GithubRepositoryEntity {
    val entity = GithubRepositoryEntity(
        id = id,
        name = name,
        organization = organization!!.toEntity(),
    )
    entity.languageHistory = languageHistory.map { it.toEntity(entity) }
    return entity
}

/**
 * Maps GitHub repository response DTO to internal DTO
 */
fun GithubRepositoryResponseDto.toDto() =
    GithubRepositoryDto(
        name = name,
    )
