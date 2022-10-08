package com.productboard.gitsy.core.domain.organization

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotBlank

/**
 * DTO representing a GitHub organization.
 *
 * @author Aleksei Ermak
 * @date 07.10.2022
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class GithubOrganizationDto(

    /**
     * Name of GitHub organization. Must not be blank.
     */
    @JsonProperty("name")
    @field:NotBlank
    val name: String,

    /**
     * Number of repositories the organization owns.
     */
    @JsonProperty("public_repos")
    val publicRepos: Int,

    /* you can add additional fields to work with */
)
