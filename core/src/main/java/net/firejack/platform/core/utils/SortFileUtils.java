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
