package com.productboard.gitsy.language.domain.original

import com.productboard.gitsy.core.domain.repository.GithubRepositoryEntity
import com.vladmihalcea.hibernate.type.json.JsonType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.joda.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

/**
 * Database entity representing language statistic of GitHub repository.
 *
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
@Entity(name = "repository_language_history")
@TypeDef(name = "json", typeClass = JsonType::class)
data class RepositoryLanguagesEntity(

    /**
     * Entity id, can be null if it has not yet been persisted.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long? = null,

    /**
     * Map of languages with corresponding number of bytes, stored as jsonb.
     */
    @Type(type = "json")
    @Column(columnDefinition = "json")
    val languageMap: Map<String, Long>,

    /**
     * Repository that contains the given languages, can't be null.
     */
    @ManyToOne
    @JoinColumn(name = "repository")
    val repository: GithubRepositoryEntity,

    /**
     * Timestamp representing the time this record has been created.
     */
    @Column(name = "created_on", nullable = false, updatable = false)
    var createdOn: Instant = Instant.now(),
)
