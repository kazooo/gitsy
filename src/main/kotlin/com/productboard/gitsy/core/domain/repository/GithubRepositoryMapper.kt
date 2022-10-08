package com.productboard.gitsy.core.domain.repository

import com.productboard.gitsy.core.domain.organization.GithubOrganizationEntity

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
fun GithubRepositoryDto.toEntity(organization: GithubOrganizationEntity) =
    GithubRepositoryEntity(
        name = name,
        organization = organization,
    )
