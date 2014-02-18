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

package net.firejack.platform.core.utils;

import java.io.Serializable;


public class Tuple<K, V> implements Serializable {
    private static final long serialVersionUID = -391752072923985756L;
    private K key;
    private V value;

    /**
     * @param key
     * @param value
     */
    public Tuple(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @return
     */
    public K getKey() {
        return key;
    }

    /**
     * @return
     */
    public V getValue() {
        return value;
    }
}
