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

package net.firejack.platform.generate.beans.web.model.key;


import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.generate.beans.Base;
import net.firejack.platform.generate.beans.annotation.Properties;
import net.firejack.platform.generate.beans.web.model.Model;
import net.firejack.platform.generate.beans.web.model.column.Field;

import java.util.List;

@Properties(subpackage = "model", suffix = "PK")
public class CompositeKey extends Base implements Key {
    private List<Field> fields;

    public CompositeKey(Model model, List<Field> fields) {
        super(model);
        this.fields = fields;
        for (Field field : fields) {
            addImport(field);
        }
    }

    @Override
    public boolean isComposite() {
        return true;
    }

    @Override
    public FieldType getType() {
        return FieldType.OBJECT;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
