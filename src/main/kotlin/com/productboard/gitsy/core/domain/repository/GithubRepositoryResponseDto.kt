package com.productboard.gitsy.core.domain.repository

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotBlank

/**
 * REST API response DTO representing a GitHub repository.
 *
 * @author Aleksei Ermak
 * @date 07.10.2022
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class GithubRepositoryResponseDto(

    /**
     * Name of GitHub repository. Must not be blank.
     */
    @JsonProperty("name")
    @field:NotBlank
    val name: String,

    /* you can add additional fields to work with */
)
