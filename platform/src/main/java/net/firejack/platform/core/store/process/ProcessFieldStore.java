/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.core.store.process;

import net.firejack.platform.core.model.registry.ProcessFieldType;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.model.registry.process.*;
import net.firejack.platform.core.store.version.UIDStore;
import net.firejack.platform.core.utils.SortOrder;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;


/**
 * Class provides access to task data
 */
@SuppressWarnings("unused")
@Component("processFieldStore")
public class ProcessFieldStore extends UIDStore<ProcessFieldModel, Long> implements IProcessFieldStore {

    @Autowired
    protected IProcessFieldCaseValueStore processFieldCaseValueStore;

    @Autowired
    protected ICaseStore caseStore;

    /**
     * @see IProcessFieldStore#updateProcessFieldValue(java.lang.String, java.lang.String, java.lang.String, java.lang.Object, java.lang.Long)
     * @param processLookup lookup of the process whose field value is being modified
     * @param entityLookup lookup of the entity which has the field
     * @param fieldLookup lookup of the field whose value is being modified
     * @param value new field value
     * @param caseId ID of the case for which the value is being modified
     */
    @Override
    @Transactional
    public void updateProcessFieldValue(String processLookup, String entityLookup, String fieldLookup, Object value, Long caseId) {
        String query = "SELECT pf" +
                " FROM ProcessFieldModel pf" +
                " LEFT OUTER JOIN pf.process p" +
                " WHERE (p.lookup = :processLookup OR p.id IS NULL)" +
                " AND pf.registryNodeType.lookup = :entityLookup" +
                " AND pf.field.lookup = :fieldLookup";
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("processLookup", processLookup);
        queryParams.put("entityLookup", entityLookup);
        queryParams.put("fieldLookup", fieldLookup);
        List<ProcessFieldModel> fieldList = findByQuery(null, null, query, queryParams, null);

        for (ProcessFieldModel processField : fieldList) {
            Long processFieldId = processField.getId();
            ProcessFieldCaseValue processFieldCaseValue = processFieldCaseValueStore.findByFieldAndCase(processFieldId, caseId);

            if (processFieldCaseValue == null) {
                switch (ProcessFieldType.valueOf(processField.getValueType())) {
                    case BOOLEAN:
                        processFieldCaseValue = new ProcessFieldCaseBooleanValue();
                        break;
                    case DATE:
                        processFieldCaseValue = new ProcessFieldCaseDateValue();
                        break;
                    case DOUBLE:
                        processFieldCaseValue = new ProcessFieldCaseDoubleValue();
                        break;
                    case STRING:
                        processFieldCaseValue = new ProcessFieldCaseStringValue();
                        break;
                    case LONG:
                        processFieldCaseValue = new ProcessFieldCaseLongValue();
                        break;
                    case INTEGER:
                        processFieldCaseValue = new ProcessFieldCaseIntegerValue();
                        break;
                }
                processFieldCaseValue.setProcessField(processField);
                processFieldCaseValue.setCase(caseStore.load(caseId));
            }
            processFieldCaseValue.setValue(value);
            processFieldCaseValueStore.saveOrUpdate(processFieldCaseValue);
        }
    }

    /**
     * Initializes the class
     */
    @PostConstruct
    public void init() {
        setClazz(ProcessFieldModel.class);
    }

    /**
     * @see net.firejack.platform.core.store.process.IProcessFieldStore#findByProcessIdPlusGlobal(java.lang.Long)
     * @param processId - ID of the process for which the fields are being retrieved
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProcessFieldModel> findByProcessIdPlusGlobal(Long processId) {

        Map<String, String> aliases = new HashMap<String, String>();
        aliases.put("process", "p");

        List<Criterion> criterions = new ArrayList<Criterion>();
        Criterion globalFieldCriterion = Restrictions.isNull("p.id");
        if (processId != null) {
            Criterion processFieldCriterion = Restrictions.eq("p.id", processId);
            criterions.add(Restrictions.or(globalFieldCriterion, processFieldCriterion));
        } else {
            criterions.add(globalFieldCriterion);
        }

        Order order = createOrder("orderPosition", SortOrder.ASC);

        List<String> fetchPaths = Arrays.asList("registryNodeType");

        return findAllWithFilter(null, null, criterions, aliases, null, null, null, fetchPaths, order);
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessFieldModel findByUID(String uid) {
        ProcessFieldModel processField = super.findByUID(uid);
        if (processField != null) {
            EntityModel entity = processField.getRegistryNodeType();
            Hibernate.initialize(entity);
            Hibernate.initialize(entity.getUid());
            List<FieldModel> fields = entity.getFields();
            if (fields != null) {
                Hibernate.initialize(fields);
                for (FieldModel field : fields) {
                    Hibernate.initialize(field.getUid());
                }
            }

        }
        return processField;
    }

}