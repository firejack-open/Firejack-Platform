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


public class CompoundKeyParticipantColumn {

    private Reference ref;
    private String columnName;
    private Boolean refToParent;

    /**
     * @return
     */
    public Reference getRef() {
        return ref;
    }

    /**
     * @param ref
     */
    public void setRef(Reference ref) {
        this.ref = ref;
    }

    /**
     * @return
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @param columnName
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @param refToParent
     */
    public void setRefToParent(boolean refToParent) {
        this.refToParent = refToParent ? Boolean.TRUE : null;
    }

    /**
     * @return
     */
    public boolean isRefToParent() {
        return refToParent != null && refToParent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompoundKeyParticipantColumn)) return false;

        CompoundKeyParticipantColumn that = (CompoundKeyParticipantColumn) o;

        return isRefToParent() && that.isRefToParent() ||
                !(columnName != null ? !columnName.equals(that.columnName) : that.columnName != null) &&
                        !(ref != null ? !ref.equals(that.ref) : that.ref != null);
    }

    @Override
    public int hashCode() {
        int result = ref != null ? ref.hashCode() : 0;
        result = 31 * result + (refToParent == Boolean.TRUE ? refToParent.hashCode() : 0);
        result = 31 * result + (columnName != null ? columnName.hashCode() : 0);
        return result;
    }
}