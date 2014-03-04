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
