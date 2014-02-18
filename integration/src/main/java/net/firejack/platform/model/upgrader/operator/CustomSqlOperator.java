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

import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.model.upgrader.operator.bean.CustomSqlType;

import java.util.ArrayList;
import java.util.List;

public class CustomSqlOperator extends AbstractOperator<CustomSqlType> {

    private static final String SQL_SEPARATOR = ";";

    @Override
    protected String[] sqlCommands(CustomSqlType type) {
        String[] customSqlCommands = type.getValue().split(SQL_SEPARATOR);
        List<String> sqlCommands = new ArrayList<String>();
        for (String customSqlCommand : customSqlCommands) {
            if (!customSqlCommand.startsWith("#")) {
                customSqlCommand = customSqlCommand.trim();
                if (StringUtils.isNotBlank(customSqlCommand)) {
                    sqlCommands.add(customSqlCommand);
                }
            }
        }
        return sqlCommands.toArray(new String[sqlCommands.size()]);
    }

}
