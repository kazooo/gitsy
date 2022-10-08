package com.productboard.gitsy.core.service

/**
 * Service providing interface for organization synchronization operations.
 *
 * @author Aleksei Ermak
 * @date 08.10.2022
 */
interface GithubOrganizationSyncService {

    /**
     * Fetch and synchronize information about the given organization.
     * Processes even related entities like organization repositories.
     *
     * @param organizationName name of organization to synchronize
     */
    fun synchronizeOrganization(organizationName: String)
}
