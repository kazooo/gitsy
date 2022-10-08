package com.productboard.gitsy.core.domain.repository

import com.productboard.gitsy.core.domain.organization.GithubOrganizationEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

/**
 * Database entity representing GitHub repository.
 *
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
@Entity(name = "repository")
data class GithubRepositoryEntity(

    /**
     * Entity id, can be null if it has not yet been persisted.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long? = null,

    /**
     * Name of repository, can't be null.
     */
    @Column(name = "name", nullable = false)
    val name: String,

    /**
     * Organiztion that owns the given repository, can't be null.
     */
    @ManyToOne
    @JoinColumn(name = "organization")
    val organization: GithubOrganizationEntity,

    /* you can add additional fields to work with */
)
