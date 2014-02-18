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

import net.firejack.platform.core.model.registry.process.CaseActionModel;
import net.firejack.platform.core.store.BaseStore;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Class provides access to case action data
 */
@Component("caseActionStore")
public class CaseActionStore extends BaseStore<CaseActionModel, Long> implements ICaseActionStore {

    @Autowired
    @Qualifier("caseNoteStore")
    private ICaseNoteStore caseNoteStore;

    /***/
    @PostConstruct
    public void init() {
        setClazz(CaseActionModel.class);
    }

    /**
     * @param caseAction - case action to be saved/updated
     * @see ICaseActionStore#saveOrUpdate(net.firejack.platform.core.model.registry.process.CaseActionModel)
     */
    @Override
    @Transactional
    public void saveOrUpdate(CaseActionModel caseAction) {
        if (caseAction.getCaseNote() != null) {
            caseNoteStore.saveOrUpdate(caseAction.getCaseNote());
        }
        super.saveOrUpdate(caseAction);
    }

    /**
     * @param caseId - case ID to search by
     * @return
     * @see ICaseActionStore#findAllByCase(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public List<CaseActionModel> findAllByCase(Long caseId) {
        Criteria criteria = getSession().createCriteria(CaseActionModel.class);
        criteria = criteria.createAlias("case", "pc");
        criteria = criteria.createAlias("pc.assignee", "assignee");
        criteria.add(Restrictions.eq("pc.id", caseId));
        criteria.setFetchMode("caseExplanation", FetchMode.JOIN);
        criteria.setFetchMode("caseNote", FetchMode.JOIN);
        criteria.setFetchMode("user", FetchMode.JOIN);
        criteria.addOrder(Order.asc("performedOn"));
        return (List<CaseActionModel>) criteria.list();
    }

}
