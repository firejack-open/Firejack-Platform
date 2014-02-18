package net.firejack.platform.generate.service;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.core.config.meta.exception.ParseException;
import net.firejack.platform.generate.beans.web.model.Domain;
import net.firejack.platform.generate.beans.web.model.Model;
import net.firejack.platform.generate.beans.web.model.column.Field;
import net.firejack.platform.generate.beans.web.report.Report;
import net.firejack.platform.generate.structure.Structure;
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
public class DomainGeneratorService extends BaseGeneratorService implements IDomainGeneratorService {

    @ProgressStatus(weight = 1, description = "Generate domain objects")
    public void generateDomains(List<Model> models, Structure structure) throws ParseException, IOException {
        Model base = getCacheItem(ABSTRACT);

        createDomain(base, models);

        reindexView(models);

        for (Model model : models) {
            if (!(model instanceof Report)) {
                Model view = model.getDomain();
                generate(view, "templates/code/server/service/domain.vsl", structure.getSrc());
            }
        }
    }

    private void createDomain(Model base, List<Model> models) {
        for (Model<Model> model : models) {
            if (!model.equals(base)) {
                Domain domain = new Domain(model);
                domain.setFields(model.getFields());
                model.setDomain(domain);

                if (model.isSubclasses()) {
                    createDomain(base, model.getSubclass());
                }
            }
        }
    }

    private void reindexView(List<Model> models) {
        Model base = getCacheItem(ABSTRACT);
        Model dto = base.getDomain();

        for (Model model : models) {
            Model<Model> domain = model.getDomain();
            Model parent = model.getParent();

            if (model.isSingle() && base.equals(parent)) {
                domain.setParent(dto);
                if (!domain.getName().equalsIgnoreCase(dto.getName())) {
                    domain.addImport(dto);
                }
            } else if (parent != null) {
                domain.setParent(parent.getDomain());
                domain.addImport(parent.getDomain());
            }

            if (model.isSubclasses()) {
                List<Model> subclass = (List<Model>) model.getSubclass();
                reindexView(subclass);

                for (Model<Model> sub : subclass) {
                    Model subDomain = sub.getDomain();
                    subDomain.setOwner(domain);
                    domain.addSubclasses(subDomain);
                }
            }

            if (domain.getFields() != null) {
                for (Field field : domain.getFields()) {
                    Model target = field.getTarget();

                    if (field.getType().equals(FieldType.LIST) || field.getType().equals(FieldType.OBJECT)) {
                        domain.addImport(field);
                    }
                    if (target != null) {
                        domain.removeImport(target);
                        domain.addImport(target.getDomain());
                    }
                }
            }
        }
    }
}
