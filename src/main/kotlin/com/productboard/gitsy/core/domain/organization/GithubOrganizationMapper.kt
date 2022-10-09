package com.productboard.gitsy.core.domain.organization

/**
 * Maps GitHub organization entity to DTO
 */
fun GithubOrganizationEntity.toDto() =
    GithubOrganizationDto(
        id = id,
        name = name,
        publicRepos = publicRepos,
    )

/**
 * Maps GitHub organization DTO to entity
 */
fun GithubOrganizationDto.toEntity() =
    GithubOrganizationEntity(
        id = id,
        name = name,
        publicRepos = publicRepos,
    )

/**
 * Maps GitHub organization response DTO to internal DTO
 */
fun GithubOrganizationResponseDto.toDto() =
    GithubOrganizationDto(
        name = key, /* persist key at the moment */
        publicRepos = publicRepos,
    )
