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