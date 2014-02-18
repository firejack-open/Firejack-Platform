/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.core.store.statistics;

import net.firejack.platform.core.model.registry.statistics.LogTransactionModel;
import net.firejack.platform.core.model.statistics.MetricGroupLevel;
import net.firejack.platform.core.store.IStore;

import java.util.Date;
import java.util.List;

public interface ILogTransactionStore extends IStore<LogTransactionModel, Long> {

    LogTransactionModel findByLookupAndHourPeriod(String packageLookup, Long hourPeriod);

    long countAllByTermAndDates(String term, String packageLookup, Date startDate, Date endDate);

    List<LogTransactionModel> findAllByTermAndDates(Integer offset, Integer limit, String term, String packageLookup, Date startDate, Date endDate, String sortColumn, String sortDirection);

    List<LogTransactionModel> findAggregatedByTermAndDates(Integer offset, Integer limit, String term, String lookup, Date startDate, Date endDate, String sortColumn, String sortDirection, MetricGroupLevel level);

    long countAggregatedByTermAndDates(String term, String lookup, Date startDate, Date endDate, MetricGroupLevel level);

}
