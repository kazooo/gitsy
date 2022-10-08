@file:Suppress("MatchingDeclarationName", "Filename")

package com.productboard.gitsy.core

/**
 * GitHub REST API endpoints with expandable URL template variables.
 *
 * @author Aleksei Ermak
 * @date 07.10.2022
 */
object GithubApiEndpoint {

    /**
     * Organization information endpoint.
     */
    const val GET_ORGANIZATION = "/orgs/{organization}"

    /**
     * Organization repositories information endpoint.
     */
    const val GET_ORG_REPOSITORIES = "/orgs/{organization}/repos"

    /**
     * Repository information endpoint.
     */
    const val GET_REPOSITORY = "/repos/{owner}/{repository}"

    /**
     * Repository languages information endpoint.
     */
    const val GET_LANGUAGES = "/repos/{owner}/{repository}/languages"
}
