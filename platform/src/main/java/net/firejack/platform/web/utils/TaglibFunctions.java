/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.web.utils;

import net.firejack.platform.core.utils.StringUtils;

import java.util.Random;


public class TaglibFunctions {

    /**
     * @param source
     * @return
     */
    public static String removeHTML(String source) {
        source = source.replaceAll("</?[^<>]*?>", "");
        source = source.replaceAll("\\p{Punct}", " ");
        source = source.replaceAll("\\s{2,}", " ");
        return source.trim();
    }

    /**
     * @param source
     * @param lenght
     * @return
     */
    public static String wrapText(String source, int lenght) {
        return StringUtils.wrapTextInLine(source, lenght);
    }

    /**
     * @param str
     * @return
     */
    public static String htmlEncode(String str) {
        if (str == null) return "";
        return str.replaceAll("&(?!(gt;|lt;|quot;))", "&amp;")
                .replaceAll("\"", "&quot;")
                .replaceAll(">", "&gt;")
                .replaceAll("<", "&lt;");
    }

    /**
     * @param str
     * @return
     */
    public static String anchorEncode(String str) {
        if (str == null) return "";
        return str.replaceAll("\\s", "-")
                .toLowerCase();
    }

    /**
     * @return return random value
     */
    public static String randomValue(String value) {
        String result = String.valueOf(new Random().nextInt(1000000));
        if (StringUtils.isNotBlank(value)) {
            result = value + "_" + result;
        }
        return result;
    }

}