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
import net.firejack.platform.generate.beans.Interface;
import net.firejack.platform.generate.beans.annotation.EmptyProperties;
import net.firejack.platform.generate.beans.web.model.Model;
import net.firejack.platform.generate.beans.web.model.ModelType;
import net.firejack.platform.generate.beans.web.report.Report;
import net.firejack.platform.generate.beans.web.store.Method;
import net.firejack.platform.generate.beans.web.store.MethodType;
import net.firejack.platform.generate.beans.web.store.Param;
import net.firejack.platform.generate.beans.web.store.Store;
import net.firejack.platform.generate.structure.Structure;
import net.firejack.platform.generate.tools.Utils;
import net.firejack.platform.web.mina.annotations.ProgressStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class StoreGeneratorService extends BaseGeneratorService implements IStoreGeneratorService {

    @ProgressStatus(weight = 1, description = "Generate store service objects")
    public void generateStoreService(List<Model> models, Structure structure) throws IOException {

        for (Model model : models) {
            if (model instanceof Report) {
                Report report = (Report) model;
                Store store = new Store(report);
                store.addImport(report.getModel());

                Method method = getMethod(model, MethodType.advancedSearch);
                store.addMethods(method);

                store.createInterface();
                Interface storeInterface = store.getInterface();
                storeInterface.addImport(report);
                storeInterface.addImport(report.getModel());
                report.setStore(store);
            } else if (!model.isSingle() && (model.isType(ModelType.Table) || model.isType(ModelType.DiscriminatorValue) ||
                    (model.isType(ModelType.DiscriminatorColumn) && !model.isAbstracts()))) {
                Store store = new Store(model);

                for (MethodType type : MethodType.values()) {
                    Method method = getMethod(model, type);
                    store.addMethods(method);
                }

                searchImport(model, store);

                store.createInterface();
                model.setStore(store);
            }
        }

        for (Model model : models) {
            Store store = model.getStore();
            if (model instanceof Report) {
                if (!((Report) model).isBiReport()) {
                    generate(store, "templates/code/server/report/reportSore.vsl", structure.getSrc());
                    generate(store.getInterface(), "templates/code/server/report/iReportStore.vsl", structure.getSrc());
                }
            } else if (!model.isSingle() && (model.isType(ModelType.Table) || model.isType(ModelType.DiscriminatorValue)
                    || (model.isType(ModelType.DiscriminatorColumn) && !model.isAbstracts()))) {
                generate(store, "templates/code/server/store/store.vsl", structure.getSrc());
                generate(store.getInterface(), "templates/code/server/store/istore.vsl", structure.getSrc());
            }
        }
    }

    private Method getMethod(Model model, MethodType type) {
        Method method = new Method(type);
        if (type.equals(MethodType.create)) {
            paramModel(model, method);
        } else if (type.equals(MethodType.update)) {
            paramModel(model, method);
        } else if (type.equals(MethodType.delete)) {
            paramId(model, method);
        } else if (type.equals(MethodType.read)) {
            paramId(model, method);
            returnModel(model, method);
        } else if (type.equals(MethodType.readAll)) {
            paramPaging(method);
            returnModelList(model, method);
        } else if (type.equals(MethodType.readAllWithFilter)) {
            paramPaging(method);
            paramIdsFilter(method);
            returnModelList(model, method);
        } else if (type.equals(MethodType.search)) {
            paramSearch(method);
            paramPaging(method);
            returnModelList(model, method);
        } else if (type.equals(MethodType.searchCount)) {
            paramSearch(method);
            returnInt(model, method);
        } else if (type.equals(MethodType.searchWithFilter)) {
            paramSearch(method);
            paramPaging(method);
            paramIdsFilter(method);
            returnModelList(model, method);
        } else if (type.equals(MethodType.searchCountWithFilter)) {
            paramSearch(method);
            paramIdsFilter(method);
            returnInt(model, method);
        } else if (type.equals(MethodType.advancedSearch)) {
            paramSearchQueries(method);
            paramPaging(method);
            returnModelList(model, method);
        } else if (type.equals(MethodType.advancedSearchCount)) {
            paramSearchQueries(method);
            returnInt(model, method);
        } else if (type.equals(MethodType.advancedSearchWithIdsFilter)) {
            paramSearchQueries(method);
            paramPaging(method);
            paramIdsFilter(method);
            returnModelList(model, method);
        } else if (type.equals(MethodType.advancedSearchCountWithIdsFilter)) {
            paramSearchQueries(method);
            paramIdsFilter(method);
            returnInt(model, method);
        }
        return method;
    }

    private void searchImport(Model<Model> model, Store store) {
        if (model != null && model.getFields() != null) {
            searchImport(model.getParent(), store);
        }
    }

    private void paramId(Model model, Method method) {
        method.addParam(new Param("id", model.getKey()));
    }

    private void paramModel(Model model, Method method) {
        method.addParam(new Param(Utils.fieldFormatting(model.getName()), FieldType.OBJECT, model));
    }

    private void paramSearch(Method method) {
        method.addParam(new Param("term", FieldType.SHORT_TEXT));
    }

    private void paramSearchQueries(Method method) {
        method.addParam(new Param("queryParameters", FieldType.LIST, new Model("List<SearchQuery>", new EmptyProperties())));
    }

    private void paramPaging(Method method) {
        method.addParam(new Param("paging", FieldType.OBJECT, new Model("Paging", new EmptyProperties())));
    }

    private void paramIdsFilter(Method method) {
        method.addParam(new Param("idsFilter", FieldType.OBJECT, new Model("SpecifiedIdsFilter", new EmptyProperties())));
    }

    private void returnModel(Model model, Method method) {
        method.setReturnType(new Param(Utils.fieldFormatting(model.getName()), FieldType.OBJECT, model));
    }

    private void returnModelList(Model model, Method method) {
        method.setReturnType(new Param(Utils.fieldFormatting(model.getName()), FieldType.LIST, model));
    }

    private void returnInt(Model model, Method method) {
        method.setReturnType(new Param("int", FieldType.INTEGER_NUMBER, model));
    }
}
