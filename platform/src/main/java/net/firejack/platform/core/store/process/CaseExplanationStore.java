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