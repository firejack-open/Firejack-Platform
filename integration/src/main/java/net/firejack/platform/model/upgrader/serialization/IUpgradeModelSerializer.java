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

package net.firejack.platform.model.upgrader.serialization;

import net.firejack.platform.model.upgrader.bean.IUpgradeModel;
import net.firejack.platform.model.upgrader.serialization.exception.UpgradeModelDeSerializationException;
import net.firejack.platform.model.upgrader.serialization.exception.UpgradeModelSerializationException;

import java.io.Reader;
import java.io.Writer;

/**
 *
 */
@SuppressWarnings("unused")
public interface IUpgradeModelSerializer {

    /**
     * @param clazz
     * @param model
     * @return
     * @throws net.firejack.platform.model.upgrader.serialization.exception.UpgradeModelSerializationException
     *
     */
    <T extends IUpgradeModel> String serialize(Class<T> clazz, T model) throws UpgradeModelSerializationException;

    /**
     * @param clazz
     * @param xml
     * @return
     * @throws net.firejack.platform.model.upgrader.serialization.exception.UpgradeModelDeSerializationException
     *
     */
    <T extends IUpgradeModel> T deSerialize(Class<T> clazz, String xml) throws UpgradeModelDeSerializationException;

    /**
     * @param clazz
     * @param model
     * @return
     * @throws net.firejack.platform.model.upgrader.serialization.exception.UpgradeModelSerializationException
     *
     */
    <T extends IUpgradeModel> Writer serializeToWriter(Class<T> clazz, T model) throws UpgradeModelSerializationException;

    /**
     * @param clazz
     * @param xmlReader
     * @return
     * @throws net.firejack.platform.model.upgrader.serialization.exception.UpgradeModelDeSerializationException
     *
     */
    <T extends IUpgradeModel> T deSerializeFromReader(Class<T> clazz, Reader xmlReader) throws UpgradeModelDeSerializationException;

}