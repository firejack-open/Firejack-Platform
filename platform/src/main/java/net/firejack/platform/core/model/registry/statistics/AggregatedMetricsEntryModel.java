/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.core.model.registry.statistics;

/**
 * Class hold aggregated values of metrics entry properties
 */
public class AggregatedMetricsEntryModel extends MetricsEntryModel {

	private static final long serialVersionUID = 9215189970387444869L;
	private Long startTime;
    private Long endTime;

    /**
     * Gets the start time
     * @return start time
     */
    public Long getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time
     * @param startTime - start time
     */
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the end time
     * @return end time
     */
    public Long getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time
     * @param endTime - end time
     */
    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    /**
     * Sets alias lookup
     * @param alias_lookup - alias lookup
     */
    public void setAlias_lookup(String alias_lookup){
        setLookup(alias_lookup);
    }

    /**
     * Sets alias system account name
     * @param alias_systemAccountName - alias system account name
     */
    public void setAlias_systemAccountName(String alias_systemAccountName){
        setSystemAccountName(alias_systemAccountName);
    }

    /**
     * Sets alias username
     * @param alias_username - alias username
     */
    public void setAlias_username(String alias_username){
        setUsername(alias_username);
    }


}
