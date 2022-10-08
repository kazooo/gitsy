package com.productboard.gitsy.core.domain.organization

import com.productboard.gitsy.core.domain.repository.GithubRepositoryEntity
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

/**
 * Database entity representing GitHub repository.
 *
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
@Entity(name = "organization")
data class GithubOrganizationEntity(

    /**
     * Entity id, can be null if it has not yet been persisted.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long? = null,

    /**
     * Name of organization, can't be null.
     */
    @Column(name = "name", nullable = false)
    val name: String,

    /**
     * Number of public repositories the given organization owns.
     */
    @Column(name = "public_repositories", nullable = false)
    val publicRepos: Int = 0,

    /**
     * List of repositories the given organization owns.
     */
    @Column(nullable = true)
    @OneToMany(mappedBy = "organization", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    var repositories: List<GithubRepositoryEntity> = emptyList()

    /* you can add additional fields to work with */
)
