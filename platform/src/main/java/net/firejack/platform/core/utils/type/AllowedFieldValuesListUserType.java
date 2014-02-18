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
