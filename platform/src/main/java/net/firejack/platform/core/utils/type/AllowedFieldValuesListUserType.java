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

package net.firejack.platform.core.utils.type;

import net.firejack.platform.core.model.registry.field.AllowedFieldValueList;
import org.hibernate.HibernateException;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;


public class AllowedFieldValuesListUserType extends BaseUserType<String> {

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Object deepCopy(Object value) throws HibernateException {
        if (value == null) {
            return null;
        }
        AllowedFieldValueList index = (AllowedFieldValueList) value;
        return index.isEmpty() ? new AllowedFieldValueList() : new AllowedFieldValueList(new ArrayList<String>(index.getEntries()));
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public boolean equals(Object object, Object object1) throws HibernateException {
        return object != null && object.equals(object1);
    }

    public int hashCode(Object object) throws HibernateException {
        return (object == null) ? 0 : object.hashCode();
    }

    public boolean isMutable() {
        return true;
    }

    public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner) throws HibernateException, SQLException {
        String value = resultSet.getString(names[0]);
        if (!resultSet.wasNull()) {
            return deserialize(value);
        }
        return new AllowedFieldValueList();
    }

    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index) throws HibernateException, SQLException {
        if (value == null) {
            preparedStatement.setNull(index, Types.VARCHAR);
        } else {
            String nodeIndex = serialize((AllowedFieldValueList) value);
            preparedStatement.setString(index, nodeIndex);
        }
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }

    public Class returnedClass() {
        return AllowedFieldValueList.class;
    }

    public int[] sqlTypes() {
        return new int[]{Types.VARCHAR};
    }

    /**
     * @param source
     * @return
     */
    public AllowedFieldValueList deserialize(String source) {
        if ((source != null) && (!"".equals(source))) {
            List<String> shownMembershipPacks = convert(source);
            return new AllowedFieldValueList(shownMembershipPacks);
        }
        return new AllowedFieldValueList();
    }

    /**
     * @param shownMembershipPacks
     * @return
     */
    public String serialize(AllowedFieldValueList shownMembershipPacks) {
        if ((shownMembershipPacks != null) && (!shownMembershipPacks.isEmpty())) {
            List<String> entries = shownMembershipPacks.getEntries();
            return convert(entries);
        }
        return null;
    }

    @Override
    protected String valueOf(String element) {
        return element;
    }

    @Override
    protected String valueOf0(String element) {
        return element;
    }

}
