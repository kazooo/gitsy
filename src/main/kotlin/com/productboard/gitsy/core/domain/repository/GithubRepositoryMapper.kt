package com.productboard.gitsy.core.domain.repository

import com.productboard.gitsy.core.domain.organization.GithubOrganizationEntity
import com.productboard.gitsy.language.domain.RepositoryLanguagesEntity

/**
 * Maps GitHub repository entity to DTO
 */
fun GithubRepositoryEntity.toDto() =
    GithubRepositoryDto(
        name = name,
    )

/**
 * Maps GitHub repository DTO to entity
 */
fun GithubRepositoryDto.toEntity(
    organization: GithubOrganizationEntity,
    languageHistory: List<RepositoryLanguagesEntity> = emptyList()
) =
    GithubRepositoryEntity(
        name = name,
        organization = organization,
        languageHistory = languageHistory
    )

/**
 * Maps GitHub repository response DTO to internal DTO
 */
fun GithubRepositoryResponseDto.toDto() =
    GithubRepositoryDto(
        name = name,
    )
