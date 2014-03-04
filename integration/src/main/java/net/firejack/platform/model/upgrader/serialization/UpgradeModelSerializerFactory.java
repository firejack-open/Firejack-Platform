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


import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@SuppressWarnings("unused")
public class UpgradeModelSerializerFactory {

    private static final String MSG_SPECIFIED_TYPE_IS_NULL = "Serializer type is null. Using default JAXB serializer.";
    private static final String MSG_UNSUPPORTED_TYPE = "Unsupported type of serializer.";
    private static final String MSG_SERIALIZER_TYPE_ALREADY_REGISTERED_PART1 = "serializer for type = [";
    private static final String MSG_SERIALIZER_TYPE_ALREADY_REGISTERED_PART2 = "] already registered. Replacing currently registered serializer with new one...";
    private static final String MSG_UN_REGISTERING_TYPE_PART1 = "un-registering type = [";
    private static final String MSG_UN_REGISTERING_TYPE_PART2 = "].";

    private static final Logger logger = Logger.getLogger(UpgradeModelSerializerFactory.class);

    private static UpgradeModelSerializerFactory instance;

    private Map<SerializerType, IUpgradeModelSerializer> serializerCache =
            new HashMap<SerializerType, IUpgradeModelSerializer>();

    private UpgradeModelSerializerFactory() {
        registerSerializer(SerializerType.JAXB, new JAXBSerializer());
    }

    /**
     * @param type
     * @return
     */
    public IUpgradeModelSerializer produceSerializer(SerializerType type) {
        if (type == null) {
            logger.warn(MSG_SPECIFIED_TYPE_IS_NULL);
            return serializerCache.get(SerializerType.JAXB);
        } else if (type == SerializerType.JAXB) {
            return serializerCache.get(type);
        } else {
            throw new UnsupportedOperationException(MSG_UNSUPPORTED_TYPE);
        }
    }

    /**
     * @param type
     * @param serializer
     */
    public void registerSerializer(SerializerType type, IUpgradeModelSerializer serializer) {
        if (type != null && serializer != null) {
            if (serializerCache.containsKey(type)) {
                logger.info(MSG_SERIALIZER_TYPE_ALREADY_REGISTERED_PART1 + type.name() + MSG_SERIALIZER_TYPE_ALREADY_REGISTERED_PART2);
            }
            serializerCache.put(type, serializer);
        }
    }

    /**
     * @param type
     */
    public void unRegisterSerializer(SerializerType type) {
        if (type != null && serializerCache.containsKey(type)) {
            logger.info(MSG_UN_REGISTERING_TYPE_PART1 + type.name() + MSG_UN_REGISTERING_TYPE_PART2);
            serializerCache.remove(type);
        }
    }

    /**
     * @return
     */
    public static UpgradeModelSerializerFactory getInstance() {
        if (instance == null) {
            instance = new UpgradeModelSerializerFactory();
        }
        return instance;
    }
}