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

package net.firejack.platform.core.store.process;

import net.firejack.platform.core.model.registry.process.CaseExplanationModel;
import net.firejack.platform.core.store.version.UIDStore;
import net.firejack.platform.core.utils.SortOrder;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Class provides access to case explanation data
 */
@SuppressWarnings("unused")
@Component("caseExplanationStore")
public class CaseExplanationStore extends UIDStore<CaseExplanationModel, Long> implements ICaseExplanationStore {

    /**
     * Initializes the class
     */
    @PostConstruct
    public void init() {
        setClazz(CaseExplanationModel.class);
    }

    /**
     * @see ICaseExplanationStore#findProcessExplanationsBySearchTerm(java.lang.Long, java.lang.String, java.lang.String, java.lang.String)
     * @param processId - ID of the process explanations belong to
     * @param term - the term to search by
     * @param sortColumn - column to sort by
     * @param sortDirection - sorting direction
     * @return list of case explanations
     */
    @Override
    @Transactional(readOnly = true)
    public List<CaseExplanationModel> findProcessExplanationsBySearchTerm(Long processId, String term, String sortColumn, String sortDirection) {
        List<Criterion> criterions = createCriterions(processId, term);

        Order order = null;
        if (sortColumn != null) {
            if (SortOrder.DESC.name().equalsIgnoreCase(sortDirection)) {
                order = Order.desc(sortColumn);
            } else {
                order = Order.asc(sortColumn);
            }
        }

        return findAllWithFilter(criterions, null, order);
    }

    /**
     * Creates criterions for the search
     * @param processId - ID of the process explanations belong to
     * @param term - the term to search by
     * @return list of criterions for the search
     */
     private List<Criterion> createCriterions(Long processId, String term) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        if (processId != null) {
            Criterion processCriterion = Restrictions.eq("process.id", processId);
            criterions.add(processCriterion);
        }
        if (!StringUtils.isEmpty(term)) {
            Criterion descCriterion = Restrictions.like("shortDescription", "%" + term + "%");
            criterions.add(descCriterion);
        }
        return criterions;
    }

    /**
     * @see ICaseExplanationStore#deleteByProcessId(java.lang.Long)
     * @param processId - process ID
     */
    @Override
    @Transactional
    public void deleteByProcessId(Long processId) {
        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion idCriterion = Restrictions.eq("process.id", processId);
        criterions.add(idCriterion);
        List<CaseExplanationModel> caseExplanations = findAllWithFilter(criterions, null);
        for (CaseExplanationModel caseExplanation : caseExplanations) {
            delete(caseExplanation);
        }
    }

}