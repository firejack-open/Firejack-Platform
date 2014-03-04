/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.firejack.platform.core.store.statistics;

import net.firejack.platform.api.statistics.domain.LogEntryType;
import net.firejack.platform.core.model.registry.statistics.LogEntryModel;
import net.firejack.platform.core.store.BaseStore;
import net.firejack.platform.core.utils.SortOrder;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component
public class LogEntryStore extends BaseStore<LogEntryModel, Long> implements ILogEntryStore {

    /***/
    @PostConstruct
    public void init() {
        setClazz(LogEntryModel.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LogEntryModel> findAllByTermAndDates(Integer offset, Integer limit,
                                                     String term, String nodeLookup,
                                                     Date startDate, Date endDate,
                                                     String sortColumn, String sortDirection,
                                                     LogEntryType logEntryType) {
        List<Criterion> criterions = createCriterionsForTermAndDates(term, nodeLookup, startDate, endDate, logEntryType);

        Order order;
        if (StringUtils.isBlank(sortColumn)) {
            order = null;
        } else {
            if (SortOrder.DESC.name().equalsIgnoreCase(sortDirection)) {
                order = Order.desc(sortColumn);
            } else {
                order = Order.asc(sortColumn);
            }
        }

        if (order == null) {

        }
        return (order == null) ?
                findAllWithFilter(offset, limit, criterions, null) :
                findAllWithFilter(offset, limit, criterions, null, order);

    }

    @Override
    @Transactional(readOnly = true)
    public long countAllByTermAndDates(String term, String nodeLookup, Date startDate, Date endDate, LogEntryType logEntryType) {
        List<Criterion> criterions = createCriterionsForTermAndDates(term, nodeLookup, startDate, endDate, logEntryType);

        return count(criterions, null);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<Criterion> createCriterionsForTermAndDates(String term, String nodeLookup, Date startDate, Date endDate, LogEntryType logEntryType) {
        List<Criterion> criterions = new ArrayList<Criterion>();

        if (!StringUtils.isEmpty(term)) {
            Criterion lookupCriterion = Restrictions.like("lookup", "%" + term + "%");
            Criterion usernameCriterion = Restrictions.like("username", "%" + term + "%");
            Criterion detailsCriterion = Restrictions.like("details", "%" + term + "%");
            Criterion termCriterion = Restrictions.or(Restrictions.or(lookupCriterion, usernameCriterion), detailsCriterion);
            criterions.add(termCriterion);
        }

        if (!LogEntryType.ALL.equals(logEntryType)) {
            criterions.add(Restrictions.eq("type", logEntryType));
        }

        if (!StringUtils.isEmpty(nodeLookup)) {
            Criterion nodeLookupCriterion = Restrictions.like("lookup", nodeLookup + "%");
            criterions.add(nodeLookupCriterion);
        }

        if (startDate != null) {
            Criterion startDateCriterion = Restrictions.ge("created", startDate);
            criterions.add(startDateCriterion);
        }

        if (endDate != null) {
            Criterion endDateCriterion = Restrictions.lt("created", endDate);
            criterions.add(endDateCriterion);
        }
        return criterions;
    }


}
