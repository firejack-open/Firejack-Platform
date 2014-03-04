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

import net.firejack.platform.generate.beans.web.model.column.Field;
import net.firejack.platform.generate.tools.Utils;

public class AssociationsField {
    private String type;
    private Field field;
    private ViewModel model;
    private String displayName;

    public AssociationsField(String type, Field field) {
        this.type = type;
        this.field = field;
        this.model = field.getTarget().getView();
        this.displayName = Utils.displayNameFormattingWithPluralEnding(model.getSimpleName());
    }

    public String getType() {
        return type;
    }

    public Field getField() {
        return field;
    }

    public ViewModel getModel() {
        return model;
    }

    public String getDisplayName() {
        return displayName;
    }

}