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

package net.firejack.platform.core.config.meta.construct;

import net.firejack.platform.core.config.meta.utils.DiffUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashSet;
import java.util.Set;


public class CompoundKeyColumnsRule {

    private Set<CompoundKeyParticipantColumn> compoundKeyParticipantColumns;
    private String name;

    /**
     * @param name
     */
    public CompoundKeyColumnsRule(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return
     */
    public CompoundKeyParticipantColumn[] getCompoundKeyParticipantColumns() {
        return DiffUtils.getArray(compoundKeyParticipantColumns, CompoundKeyParticipantColumn.class);
    }

    /**
     * @param compoundKeyParticipantColumns
     */
    public void setCompoundKeyParticipantColumns(Set<CompoundKeyParticipantColumn> compoundKeyParticipantColumns) {
        this.compoundKeyParticipantColumns = compoundKeyParticipantColumns;
    }

    /**
     * @param column
     */
    public void add(CompoundKeyParticipantColumn column) {
        getUniqueColumnsSet().add(column);
    }

    /**
     * @return
     */
    public boolean isDetermined() {
        for (CompoundKeyParticipantColumn column : compoundKeyParticipantColumns) {
            if (column.getRef() != null || column.isRefToParent()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param ruleName
     * @param name
     * @return
     */
    public static CompoundKeyColumnsRule produceRule(String ruleName, String name) {
        CompoundKeyParticipantColumn column = new CompoundKeyParticipantColumn();
        column.setColumnName(name);
        CompoundKeyColumnsRule rule = new CompoundKeyColumnsRule(ruleName);
        rule.add(column);
        return rule;
    }

    /**
     * @param ruleName
     * @param ref
     * @return
     */
    public static CompoundKeyColumnsRule produceRule(String ruleName, Reference ref) {
        CompoundKeyParticipantColumn column = new CompoundKeyParticipantColumn();
        column.setRef(ref);
        CompoundKeyColumnsRule rule = new CompoundKeyColumnsRule(ruleName);
        rule.add(column);
        return rule;
    }

    private Set<CompoundKeyParticipantColumn> getUniqueColumnsSet() {
        if (compoundKeyParticipantColumns == null) {
            compoundKeyParticipantColumns = new HashSet<CompoundKeyParticipantColumn>();
        }
        return compoundKeyParticipantColumns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompoundKeyColumnsRule)) return false;

        CompoundKeyColumnsRule that = (CompoundKeyColumnsRule) o;

        return name.equals(that.name) && CollectionUtils.isEqualCollection(
                compoundKeyParticipantColumns, that.compoundKeyParticipantColumns);
    }

    @Override
    public int hashCode() {
        int result = compoundKeyParticipantColumns.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}