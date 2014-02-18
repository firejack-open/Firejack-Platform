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