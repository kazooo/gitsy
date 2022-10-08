package com.productboard.gitsy.core.domain.organization

/**
 * DTO representing a GitHub organization.
 *
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
data class GithubOrganizationDto(
    /**
     * Name of GitHub organization.
     */
    val name: String,

    /**
     * Number of repositories the organization owns.
     */
    val publicRepos: Int,
)
