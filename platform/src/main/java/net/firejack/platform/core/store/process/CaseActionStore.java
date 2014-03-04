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
