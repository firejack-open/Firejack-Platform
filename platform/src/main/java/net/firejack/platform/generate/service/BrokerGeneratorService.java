package net.firejack.platform.generate.service;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.core.config.meta.element.action.ActionElement;
import net.firejack.platform.core.config.meta.element.action.ActionParameterElement;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.registry.HTTPMethod;
import net.firejack.platform.generate.beans.ImportString;
import net.firejack.platform.generate.beans.web.broker.Broker;
import net.firejack.platform.generate.beans.web.model.Domain;
import net.firejack.platform.generate.beans.web.model.Model;
import net.firejack.platform.generate.beans.web.report.Report;
import net.firejack.platform.generate.beans.web.store.Method;
import net.firejack.platform.generate.beans.web.store.MethodType;
import net.firejack.platform.generate.beans.web.store.Param;
import net.firejack.platform.generate.beans.web.store.Store;
import net.firejack.platform.generate.structure.Structure;
import net.firejack.platform.generate.tools.Utils;
import net.firejack.platform.model.wsdl.Invoker;
import net.firejack.platform.web.mina.annotations.ProgressStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

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

@Component
public class BrokerGeneratorService extends BaseGeneratorService implements IBrokerGeneratorService {

    @ProgressStatus(weight = 1, description = "Generate broker objects")
    public void generateBrokerService(IPackageDescriptor descriptor, List<Model> models, Structure structure) throws IOException {
        ActionElement[] elements = descriptor.getActionElements();

        if (elements == null) {
            return;
        }

        for (ActionElement element : elements) {
            Model model = getCacheItem(element.getPath());
            if (model != null && !model.isAbstracts()) {
                Store store = model.getStore();

                Method method = model.findStoreMethod(Utils.fieldFormatting(element.getName()));
                Broker broker = getBroker(element, model, store, method);
                model.addBroker(broker);

                if (model instanceof Report) {
                    if (((Report) model).isBiReport()) {
                        generate(broker, "templates/code/server/report/biReportBroker.vsl", structure.getSrc());
                    } else {
                        generate(broker, "templates/code/server/report/reportBroker.vsl", structure.getSrc());
                    }
                } else {
                    generate(broker, "templates/code/server/service/broker.vsl", structure.getSrc());
                }
            }
        }
    }

    private Broker getBroker(ActionElement element, Model model, Store store, Method method) {
        Broker broker;
        if (method != null) {
            broker = new Broker(model, method.getType());
        } else {
            broker = new Broker(model, element.getName());
        }

        broker.setLookup(DiffUtils.lookup(element.getPath(), element.getName()));
        broker.setMethod(method);
        broker.setWsdl(model.isWsdl());
        broker.setWsdlMethod(element.getSoapMethod());
        broker.setDescription(element.getDescription());
        broker.setHttpMethod(element.getMethod());
        broker.addImport(model.getDomain());
        broker.addImport(model.getKey());
        if (model.isWsdl())
            broker.addImport(new ImportString(Invoker.class));
        if (store != null) {
            broker.addImport(store.getInterface());
        }
        if (model.isSingle()) {
            broker.removeImport(model);
        }

        List<ActionParameterElement> parameters = element.getParameters();
        if (parameters != null) {
            for (ActionParameterElement parameter : parameters) {
                if (!model.isSingle() && parameter.getName().equals("id")) {
                    broker.addParam(new Param("id", model.getKey()));
                } else {
                    broker.addParam(new Param(parameter.getName(), parameter.getType()));
                }
            }
        } else if (!broker.getHttpMethod().equals(HTTPMethod.POST) && broker.getType() != null && !broker.isType(MethodType.readAll)) {
            broker.addParam(new Param("id", model.getKey()));
        }

        if (broker.getHttpMethod().equals(HTTPMethod.POST) || broker.getHttpMethod().equals(HTTPMethod.PUT)) {
            Model request = getCacheItem(element.getInputVOEntityLookup());
            broker.addParam(new Param("data", FieldType.OBJECT, request.getDomain()));

            broker.setRequest(request);
            broker.addImport(request.getDomain());
        }

        if (model instanceof Report && ((Report) model).isBiReport()) {
            Model m = new Model();

            Domain domain = new Domain();
            domain.setProjectPath("net.firejack.platform.api.registry.domain");
            domain.setName("BIReportData");
            m.setDomain(domain);

            broker.setResponse(m);
        }

        addCacheItem(broker.getLookup(), broker);
        return broker;
    }
}
