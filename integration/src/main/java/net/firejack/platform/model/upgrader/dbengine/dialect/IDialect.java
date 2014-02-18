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

package net.firejack.platform.model.upgrader.dbengine.dialect;

import net.firejack.platform.model.upgrader.dbengine.bean.Column;
import net.firejack.platform.model.upgrader.dbengine.bean.ForeignKey;
import net.firejack.platform.model.upgrader.dbengine.bean.IndexKey;
import net.firejack.platform.model.upgrader.dbengine.bean.Table;

public interface IDialect {

    /**
     * @param table
     * @return
     */
    String createTable(Table table);

    /**
     * @param table
     * @return
     */
    String dropTable(Table table);

    /**
     * @param table
     * @return
     */
    String renameTable(Table table, Table newTable);

    /**
     * @param table
     * @param column
     * @return
     */
    String addColumn(Table table, Column column);

    /**
     * @param table
     * @param oldColumn
     * @param newColumn
     * @return
     */
    String modifyColumn(Table table, Column oldColumn, Column newColumn);

    /**
     * @param table
     * @param column
     * @return
     */
    String dropColumn(Table table, Column column);

    /**
     * @param foreignKey
     * @return
     */
    String addForeignKey(ForeignKey foreignKey);

    /**
     * @param foreignKey
     * @return
     */
    String dropForeignKey(ForeignKey foreignKey);

    /**
     * @param indexKey
     * @return
     */
    String addIndexKey(IndexKey indexKey);

    /**
     * @param indexKey
     * @return
     */
    String dropIndexKey(IndexKey indexKey);

}
