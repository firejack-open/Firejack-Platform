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

package net.firejack.platform.model.upgrader.dbengine;

import net.firejack.platform.model.upgrader.dbengine.dialect.IDialect;
import net.firejack.platform.model.upgrader.dbengine.dialect.MySql5Dialect;

import javax.transaction.NotSupportedException;

public class DialectFactory {

    private static DialectFactory instance = new DialectFactory();

    private DialectFactory() {

    }

    /**
     * @return
     */
    public static DialectFactory getInstance() {
        if (instance == null) {
            instance = new DialectFactory();
        }
        return instance;
    }

    /**
     * @param dialectType
     * @return
     * @throws javax.transaction.NotSupportedException
     *
     */
    public IDialect getDialect(DialectType dialectType) throws NotSupportedException {
        IDialect dialect;
        switch (dialectType) {
            case MySQL5:
                dialect = new MySql5Dialect();
                break;
            default:
                throw new NotSupportedException("Dialect " + dialectType.name() + " doesn't support.");
        }
        return dialect;
    }

}
