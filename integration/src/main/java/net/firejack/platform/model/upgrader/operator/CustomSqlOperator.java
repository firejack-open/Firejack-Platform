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
