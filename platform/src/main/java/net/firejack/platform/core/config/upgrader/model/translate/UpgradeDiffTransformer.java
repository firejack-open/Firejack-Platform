/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.core.config.upgrader.model.translate;

import net.firejack.platform.core.config.meta.diff.*;
import net.firejack.platform.core.config.translate.ElementsDiffTransformer;
import net.firejack.platform.model.upgrader.dbengine.DialectType;

import java.util.Map;


public class UpgradeDiffTransformer implements ElementsDiffTransformer {

    private Map<String, DialectType> sqlDialectMapping;

    @Override
    @SuppressWarnings("unchecked")
    public <T extends PackageDescriptorElementDiff> T transformElementsDiff(T elementsDiff) {
        T result = null;
        if (elementsDiff instanceof FieldsDiff) {
            result = (T) transformFieldsDiff((FieldsDiff) elementsDiff);
        } else if (elementsDiff instanceof EntitiesDiff) {
            result = (T) transformEntitiesDiff((EntitiesDiff) elementsDiff);
        } else if (elementsDiff instanceof IndexesDiff) {
            result = (T) transformIndexesDiff((IndexesDiff) elementsDiff);
        } else if (elementsDiff instanceof RelationshipsDiff) {
            result = (T) transformRelationshipsDiff((RelationshipsDiff) elementsDiff);
        }
        return result;
    }

    public Map<String, DialectType> getSqlDialectMapping() {
        return sqlDialectMapping;
    }

    public void setSqlDialectMapping(Map<String, DialectType> sqlDialectMapping) {
        this.sqlDialectMapping = sqlDialectMapping;
    }

    protected FieldsDiff transformFieldsDiff(FieldsDiff diff) {
        return diff;
    }

    protected EntitiesDiff transformEntitiesDiff(EntitiesDiff diff) {
        return diff;
    }

    protected RelationshipsDiff transformRelationshipsDiff(RelationshipsDiff diff) {
        return diff;
    }

    protected IndexesDiff transformIndexesDiff(IndexesDiff diff) {
        return diff;
    }

}