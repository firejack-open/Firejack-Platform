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

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;


public class SortFileUtils {

    /**
     * @param files
     * @param desc
     * @return
     */
    public static File[] sortingByName(File[] files, boolean desc) {
        Arrays.sort(files, new Comparator() {
            public int compare(final Object o1, final Object o2) {
                String s1 = ((File) o1).getName().toLowerCase();
                String s2 = ((File) o2).getName().toLowerCase();
                return s1.compareTo(s2);
            }
        });
        if (desc) {
            org.apache.commons.lang.ArrayUtils.reverse(files);
        }
        return files;
    }

    /**
     * @param files
     * @param desc
     * @return
     */
    public static File[] sortingByExt(File[] files, boolean desc) {
        Arrays.sort(files, new Comparator() {
            public int compare(final Object o1, final Object o2) {
                String s1 = ((File) o1).getName().toLowerCase();
                String s2 = ((File) o2).getName().toLowerCase();
                final int s1Dot = s1.lastIndexOf('.');
                final int s2Dot = s2.lastIndexOf('.');
                //
                if ((s1Dot == -1) == (s2Dot == -1)) { // both or neither
                    s1 = s1.substring(s1Dot + 1);
                    s2 = s2.substring(s2Dot + 1);
                    Integer i1 = Integer.parseInt(s1);
                    Integer i2 = Integer.parseInt(s2);
                    return i1.compareTo(i2);
                } else if (s1Dot == -1) { // only s2 has an extension, so s1 goes first
                    return -1;
                } else { // only s1 has an extension, so s1 goes second
                    return 1;
                }
            }
        });
        if (desc) {
            org.apache.commons.lang.ArrayUtils.reverse(files);
        }
        return files;
    }

}
