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

package net.firejack.platform.model.upgrader.operator;

import net.firejack.platform.model.upgrader.dbengine.bean.Column;
import net.firejack.platform.model.upgrader.dbengine.bean.IndexKey;
import net.firejack.platform.model.upgrader.dbengine.bean.Table;
import net.firejack.platform.model.upgrader.operator.bean.AddIndexType;
import net.firejack.platform.model.upgrader.operator.bean.ColumnType;

import java.util.ArrayList;
import java.util.List;

public class AddIndexOperator extends AbstractOperator<AddIndexType> {

    @Override
    protected String[] sqlCommands(AddIndexType addIndexType) {
        Table table = new Table(addIndexType.getTable());
        IndexKey indexKey = new IndexKey(addIndexType.getName(), table);
        indexKey.setType(addIndexType.getIndexType());
        List<Column> columns = new ArrayList<Column>();
        for (ColumnType columnType : addIndexType.getColumn()) {
            Column column = new Column(columnType.getName());
            columns.add(column);
        }
        indexKey.setColumns(columns);
        return new String[] { dialect.addIndexKey(indexKey) };
    }

}
