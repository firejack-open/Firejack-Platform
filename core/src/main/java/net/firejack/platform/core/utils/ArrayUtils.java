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