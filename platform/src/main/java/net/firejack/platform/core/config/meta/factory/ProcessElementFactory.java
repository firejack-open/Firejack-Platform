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

package net.firejack.platform.core.config.meta.factory;

import net.firejack.platform.core.config.meta.element.process.*;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.registry.process.*;
import net.firejack.platform.core.store.process.ICaseExplanationStore;
import net.firejack.platform.core.store.registry.IActorStore;
import net.firejack.platform.core.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProcessElementFactory extends PackageDescriptorConfigElementFactory<ProcessModel, ProcessElement> {

    @Autowired
    @Qualifier("actorStore")
    private IActorStore actorStore;

    @Autowired
    @Qualifier("caseExplanationStore")
    private ICaseExplanationStore caseExplanationStore;

    @Autowired
    @Qualifier("statusElementFactory")
    private StatusElementFactory statusElementFactory;

    @Autowired
    @Qualifier("activityElementFactory")
    private ActivityElementFactory activityElementFactory;

    @Autowired
    @Qualifier("processFieldElementFactory")
    private ProcessFieldElementFactory processFieldElementFactory;

    /***/
    public ProcessElementFactory() {
        setEntityClass(ProcessModel.class);
        setElementClass(ProcessElement.class);
    }

    @Override
    protected void initDescriptorElementSpecific(ProcessElement processElement, ProcessModel process) {
        super.initDescriptorElementSpecific(processElement, process);
        List<ActivityModel> activities = process.getActivities();
        List<StatusModel> statuses = process.getStatuses();
        List<CaseExplanationModel> caseExplanations = process.getCaseExplanations();
        List<ProcessFieldModel> customFields = process.getProcessFields();

        if (activities != null) {
            processElement.setActivities(
                    activityElementFactory.getDescriptorElementList(activities));
        }

        if (statuses != null) {
            List<StatusElement> statusElements =
                    statusElementFactory.getDescriptorElementList(statuses);
            processElement.setStatuses(statusElements);
        }

        if (caseExplanations != null) {
            List<ExplanationElement> elements = new ArrayList<ExplanationElement>();
            for (CaseExplanationModel explanation : caseExplanations) {
                ExplanationElement element = new ExplanationElement();
                element.setShortDescription(explanation.getShortDescription());
                element.setLongDescription(explanation.getLongDescription());
                PackageDescriptorConfigElementFactory.initializeConfigElementUID(
                        element, explanation, caseExplanationStore);
                elements.add(element);
            }
            processElement.setExplanations(elements);
        }

        processElement.setProcessType(process.getProcessType());

        if (CollectionUtils.isNotEmpty(customFields)) {
            processElement.setCustomFields(
                    processFieldElementFactory.getDescriptorElementList(customFields));
        }
    }

    @Override
    protected void initEntitySpecific(ProcessModel process, ProcessElement processElement) {
        super.initEntitySpecific(process, processElement);
        List<ActivityElement> activityElements = processElement.getActivities();
        List<StatusElement> statusElements = processElement.getStatuses();
        List<ExplanationElement> explanationElements = processElement.getExplanations();
        List<ProcessFieldElement> customFields = processElement.getCustomFields();

        Map<String, StatusModel> statuses = new HashMap<String, StatusModel>();
        if (CollectionUtils.isNotEmpty(statusElements)) {
            for (StatusElement statusElement : statusElements) {
                StatusModel status = statusElementFactory.getEntity(statusElement);
                status.setParent(process);
                status.setPath(process.getLookup());
                status.setLookup(DiffUtils.lookup(process.getLookup(), statusElement.getName()));

                statuses.put(statusElement.getName(), status);
            }
            process.setStatuses(new ArrayList<StatusModel>(statuses.values()));
        }

        if (CollectionUtils.isNotEmpty(activityElements)) {
            List<ActivityModel> activities = new ArrayList<ActivityModel>();
            for (ActivityElement activityElement : activityElements) {
                ActorModel actor = actorStore.findByLookup(activityElement.getActor());
                if (actor != null && statuses.containsKey(activityElement.getStatus())) {
                    ActivityModel activity = activityElementFactory.getEntity(activityElement);
                    activity.setParent(process);
                    activity.setPath(process.getLookup());
                    activity.setLookup(DiffUtils.lookup(process.getLookup(), activityElement.getName()));
                    activity.setStatus(statuses.get(activityElement.getStatus()));
                    activity.setActor(actor);

                    activities.add(activity);
                }
                process.setActivities(activities);
            }
        }

        if (CollectionUtils.isNotEmpty(explanationElements)) {
            List<CaseExplanationModel> explanations = new ArrayList<CaseExplanationModel>();
            for (ExplanationElement explanationElement : explanationElements) {
                CaseExplanationModel explanation = new CaseExplanationModel();
                explanation.setProcess(process);
                explanation.setShortDescription(explanationElement.getShortDescription());
                explanation.setLongDescription(explanationElement.getLongDescription());
                PackageDescriptorConfigElementFactory.initializeModelUID(
                        explanation, explanationElement);
                explanations.add(explanation);
            }
            process.setCaseExplanations(explanations);
        }

        process.setProcessType(processElement.getProcessType());

        if (CollectionUtils.isNotEmpty(customFields)) {
            process.setProcessFields(
                    processFieldElementFactory.getEntityList(customFields));
        }
    }

}