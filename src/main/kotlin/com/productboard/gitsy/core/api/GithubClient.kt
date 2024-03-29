package com.productboard.gitsy.core.api

import com.productboard.gitsy.core.domain.organization.GithubOrganizationResponseDto
import com.productboard.gitsy.core.domain.repository.GithubRepositoryResponseDto

/**
 * REST API client for GitHub REST API.
 *
 * @author Aleksei Ermak
 * @date 07.10.2022
 */
interface GithubClient {

    /**
     * Requests GitHub for information about organization with the provided name.
     *
     * @param organizationName name of GitHub organization
     * @return GitHub's organization DTO or null if the organization wasn't found
     */
    fun getOrganization(organizationName: String): GithubOrganizationResponseDto?

    /**
     * Requests GitHub for a list of repositories the given organization owns.
     *
     * @param organizationName name of GitHub organization
     * @return list of GitHub repository DTOs
     */
    fun getOrganizationRepositories(organizationName: String): List<GithubRepositoryResponseDto>

    /**
     * Requests GitHub for information about repository with the provided name.
     *
     * @param repositoryOwner  name of GitHub owner
     * @param repositoryName   name of GitHub repository
     * @return GitHub's repository DTO or null if the repository wasn't found
     */
    fun getRepository(repositoryOwner: String, repositoryName: String): GithubRepositoryResponseDto?

    /**
     * Requests GitHub for information about languages the given repository contains.
     *
     * @param repositoryOwner  name of GitHub owner
     * @param repositoryName   name of GitHub repository
     * @return list of languages with corresponding number of bytes of code written in that language
     */
    fun getRepositoryLanguages(repositoryOwner: String, repositoryName: String): Map<String, Long>
}
