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