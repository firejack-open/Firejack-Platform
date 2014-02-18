package net.firejack.platform.generate.beans.web.model.key;
/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */


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
