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

package net.firejack.platform.generate.beans.web.model;

import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.generate.beans.Base;
import net.firejack.platform.generate.beans.Import;
import net.firejack.platform.generate.beans.annotation.Properties;
import net.firejack.platform.generate.beans.web.broker.Broker;
import net.firejack.platform.generate.beans.web.js.ViewModel;
import net.firejack.platform.generate.beans.web.model.column.Field;
import net.firejack.platform.generate.beans.web.model.key.Key;
import net.firejack.platform.generate.beans.web.report.Report;
import net.firejack.platform.generate.beans.web.store.Method;
import net.firejack.platform.generate.beans.web.store.MethodType;
import net.firejack.platform.generate.beans.web.store.Store;

import java.util.ArrayList;
import java.util.List;

@Properties(subpackage = "model", suffix = "Model")
public class Model<T extends Model> extends Base {
    private ModelType type;
    private boolean abstracts;
    private boolean single;
    private boolean wsdl;

    private String table;
    private String unique;

    private String prefix;
    private String url;
    private String referenceHeading;
    private String referenceSubHeading;
    private String referenceDescription;

    private Key key;
    private T parent;
    private T owner;
    private List<T> children;
    private List<T> subclass;
    private List<Field> fields;

    private Model domain;
    private ViewModel view;
    private List<Report> reports;
    private Store store;
    private List<Broker> brokers;

    /***/
    public Model() {
    }

    public Model(String lookup) {
        super(lookup);
    }

    public Model(Base base) {
        super(base);
    }

    public Model(Model model) {
        super(model);
        this.referenceHeading = model.referenceHeading;
        this.referenceSubHeading = model.referenceSubHeading;
        this.referenceDescription = model.referenceDescription;
    }

    /**
     * @param name
     * @param properties
     */
    public Model(String name, Properties properties) {
        super(name, properties);
    }

    /**
     * @return
     */
    public ModelType getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(ModelType type) {
        this.type = type;
    }

    /**
     * @param type
     * @return
     */
    public boolean isType(ModelType type) {
        return this.type.equals(type);
    }

    /**
     * @return
     */
    public boolean isSingle() {
        return single;
    }

    /**
     * @param single
     */
    public void setSingle(boolean single) {
        this.single = single;
    }

    public boolean isWsdl() {
        return wsdl;
    }

    public void setWsdl(boolean wsdl) {
        this.wsdl = wsdl;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }

    public boolean isAbstracts() {
        return abstracts;
    }

    public void setAbstracts(boolean abstracts) {
        this.abstracts = abstracts;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getReferenceHeading() {
        return referenceHeading;
    }

    public void setReferenceHeading(String referenceHeading) {
        this.referenceHeading = referenceHeading;
    }

    public String getReferenceSubHeading() {
        return referenceSubHeading;
    }

    public void setReferenceSubHeading(String referenceSubHeading) {
        this.referenceSubHeading = referenceSubHeading;
    }

    public String getReferenceDescription() {
        return referenceDescription;
    }

    public void setReferenceDescription(String referenceDescription) {
        this.referenceDescription = referenceDescription;
    }

    /**
     * @return
     */
    public boolean isExtended() {
        return parent != null;
    }

    public boolean isNested() {
        return owner != null;
    }

    public boolean isSubclasses() {
        return subclass != null && !subclass.isEmpty();
    }

    /**
     * @return
     */
    public T getParent() {
        return parent;
    }

    /**
     * @param parent
     */
    public void setParent(T parent) {
        this.parent = parent;
        parent.addChild(this);
    }

    public T getOwner() {
        return owner;
    }

    public void setOwner(T owner) {
        this.owner = owner;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    /**
     * @return
     */
    public List<T> getChildren() {
        return children;
    }

    private void addChild(T child) {
        if (this.children == null) {
            this.children = new ArrayList<T>();
        }
        this.children.add(child);
    }

    /**
     * @return
     */
    public boolean isEmptyChild() {
        return this.children == null;
    }

    public List<T> getSubclass() {
        return subclass;
    }

    public void setSubclass(List<T> subclass) {
        this.subclass = subclass;
    }

    public void addSubclasses(T model) {
        if (subclass == null)
            subclass = new ArrayList<T>();
        subclass.add(model);
    }

    /**
     * @return
     */
    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    /**
     * @param field
     */
    public void addField(Field field) {
        if (this.fields == null) {
            this.fields = new ArrayList<Field>();
        }
        this.fields.add(field);
    }

    public Field findField(String column) {
        if (fields != null) {
            for (Field field : fields) {
                if (field.getColumn().equals(column)) {
                    return field;
                }
            }
        }
        return null;
    }

    /**
     * @return
     */
    public Model getDomain() {
        return domain;
    }

    /**
     * @param domain
     */
    public void setDomain(Model domain) {
        this.domain = domain;
    }

    public ViewModel getView() {
        return view;
    }

    public void setView(ViewModel view) {
        this.view = view;
    }

    /**
     * @return
     */
    public Store getStore() {
        return store;
    }

    /**
     * @param store
     */
    public void setStore(Store store) {
        this.store = store;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public void addReport(Report report) {
        if (this.reports == null)
            this.reports = new ArrayList<Report>();
        this.reports.add(report);
    }

    /**
     * @return
     */
    public List<Broker> getBrokers() {
        return brokers;
    }

    /**
     * @param broker
     */
    public void addBroker(Broker broker) {
        if (this.brokers == null) {
            this.brokers = new ArrayList<Broker>();
        }

        this.brokers.add(broker);
    }

    /**
     * @param name
     * @return
     */
    public Method findStoreMethod(String name) {
        MethodType type = MethodType.find(name);
        if (type == null) return null;
        if (store != null) {
            return store.find(type);
        }

        return null;
    }

    public String getDomainLookup() {
        if (StringUtils.isBlank(classPath)) {
            return projectPath;
        } else {
            return projectPath + DOT + classPath;
        }
    }

    public void addImport(Import anImport) {
        if (Model.class.isAssignableFrom(anImport.getClass()) && ((Model) anImport).isNested()) {
            return;
        }
        if (isNested())
            owner.addImport(anImport);
        else
            super.addImport(anImport);
    }
}
