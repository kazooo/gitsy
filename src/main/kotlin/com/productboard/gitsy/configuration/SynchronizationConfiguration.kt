package com.productboard.gitsy.configuration

import com.productboard.gitsy.core.service.GithubOrganizationSyncService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

private val logger = KotlinLogging.logger { }

/**
 * Configuration for scheduled synchronizations of GitHub organizations.
 *
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
@Configuration
@EnableScheduling
class SynchronizationConfiguration(
    @Autowired private val environment: AppEnvironment,
    @Autowired private val organizationSyncService: GithubOrganizationSyncService,
) {

    /**
     * Synchronizes GitHub organization and related entities, like repositories.
     * Scheduled by Quartz cron from environment properties.
     */
    @Scheduled(cron = "\${gitsy.organizationSyncCron}")
    fun scheduledSynchronization() {
        if (environment.synchronizationEnabled) {
            val organizationNames = environment.synchronizeOrganizations
            logger.trace { "Synchronizing ${organizationNames.size} organizations..." }
            organizationNames.forEach { organizationSyncService.synchronizeOrganization(it) }
        } else {
            logger.debug { "Synchronization is disabled." }
        }
    }
}
