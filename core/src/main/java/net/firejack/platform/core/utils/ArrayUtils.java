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


import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;


@SuppressWarnings("unchecked")
public class ArrayUtils extends org.apache.commons.lang.ArrayUtils {

    public static <T> boolean containsAll(T[] sourceItems, T... itemsToCheck) {
        boolean result = true;
        if (itemsToCheck.length != 0) {
            if (sourceItems == null || sourceItems.length == 0) {
                result = false;
            } else {
                for (T item : itemsToCheck) {
                    if (!contains(sourceItems, item)) {
                        result = false;
                        break;
                    }
                }
            }
        }
        return result;
    }

    public static <T> T[] getArray(Collection<T> itemList, Class<T> clazz) {
        return itemList == null || itemList.isEmpty() ?
                null : itemList.toArray((T[]) Array.newInstance(clazz, itemList.size()));
    }

    public static <T> T[] append(T[] array, List<T> list, Class<T> clazz) {
        if (list == null || list.isEmpty()) {
            return array;
        }
        T[] additionalItems = getArray(list, clazz);
        return array == null ? additionalItems : (T[]) addAll(array, additionalItems);
    }

}