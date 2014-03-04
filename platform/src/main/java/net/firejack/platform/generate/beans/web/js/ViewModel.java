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

package net.firejack.platform.generate.beans.web.js;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.generate.beans.annotation.Properties;
import net.firejack.platform.generate.beans.web.model.Model;
import net.firejack.platform.generate.beans.web.model.column.Field;
import net.firejack.platform.generate.beans.web.model.key.CompositeKey;
import net.firejack.platform.generate.beans.web.model.key.Key;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Properties(subpackage = "model", suffix = "Model", extension = ".js")
public class ViewModel extends Model<ViewModel> {
    private List<Field> relatedChildren;
    private List<AssociationsField> associations;
    private CompositeKey compositeKey;
    private ViewModel factModel;

    /***/
    public ViewModel() {
    }

    public ViewModel(Model model) {
        super(model);
        model.setView(this);
        Key key = model.getKey();
        if (key instanceof CompositeKey)
            compositeKey = (CompositeKey) key;
    }

    public List<AssociationsField> getAssociations() {
        return associations;
    }

    public void setAssociations(List<AssociationsField> associations) {
        this.associations = associations;
    }

    public void addAssociations(AssociationsField field) {
        if (associations == null) {
            associations = new ArrayList<AssociationsField>();
        }
        this.associations.add(field);
    }

    public List<Field> getRelatedChildren() {
        return relatedChildren;
    }

    public void addRelatedChildren(Field field) {
        if (relatedChildren == null) {
            relatedChildren = new ArrayList<Field>();
        }
        this.relatedChildren.add(field);
    }

    public CompositeKey getCompositeKey() {
        return compositeKey;
    }

    public ViewModel getFactModel() {
        return factModel;
    }

    public void setFactModel(ViewModel factModel) {
        this.factModel = factModel;
    }

    public String getDisplayName() {
        if (getFields() != null) {
            for (Field field : getFields()) {
                if (field.getType().equals(FieldType.NAME))
                    return field.getName();
            }
            for (Field field : getFields()) {
                if (field.getType().isString() && !field.isNullable())
                    return field.getName();
            }
        }
        return "id";
    }

    public String getPath() {
        String path = StringUtils.isNotEmpty(classPath) ? classPath : StringUtils.isNotEmpty(serviceName) ? serviceName.toLowerCase() : "";
        return ("/" + (path + "/" + (StringUtils.isNotBlank(entityPath) ? entityPath : "")).replaceAll("\\.", "/") + "/" + normalize).replaceAll("/+", "/");
    }

    @Override
    public String getFullName() {
        String sub = properties.subpackage().isEmpty() ? "" : DOT + properties.subpackage();
        if (classPath.isEmpty()) {
            return projectPath + DOT + normalize + sub + DOT + getName();
        } else {
            return projectPath + DOT + classPath + DOT + normalize + sub + DOT + getName();
        }
    }

    public String getFilePosition() {
        String path = this.projectPath.replaceAll("\\.", "\\" + File.separator);
        path += File.separator + this.classPath.replaceAll("\\.", "\\" + File.separator);
        path += File.separator + normalize;
        path += File.separator + properties.subpackage().replaceAll("\\.", "\\" + File.separator);
        return path;
    }
}
