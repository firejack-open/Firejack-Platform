/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.service.content.utils;

import net.firejack.platform.core.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TTLParser {

    private static final Pattern pattern = Pattern.compile("(\\d)+(h|mn|sec|ms){1}");

    public static Long parseTTLValue(String value) {
        long result = 0;
        if (StringUtils.isNotBlank(value)) {
            Matcher matcher = pattern.matcher(value);
            while (matcher.find()) {
                String fullToken = matcher.group(0);
                if (fullToken.contains("h")) {
                    String timeToken = matcher.group(1);
                    result += parseLong(timeToken) * 3600000;
                } else if (fullToken.contains("mn")) {
                    String timeToken = matcher.group(1);
                    result += parseLong(timeToken) * 60000;
                } else if (fullToken.contains("sec")) {
                    String timeToken = matcher.group(1);
                    result += parseLong(timeToken) * 1000;
                } else if (fullToken.contains("ms")) {
                    String timeToken = matcher.group(1);
                    result += parseLong(timeToken);
                }
            }
        }
        return result;
    }

    private static long parseLong(String val) {
        try {
            return Long.parseLong(val);
        } catch (NumberFormatException e) {
            return 0l;
        }
    }

    public static void main(String[] args) {
        System.out.println(parseTTLValue("2h3mn4sec5ms"));
        System.out.println(parseTTLValue("3mn4sec5ms"));
        System.out.println(parseTTLValue("4sec5ms"));
        System.out.println(parseTTLValue("5ms"));
        System.out.println(parseTTLValue(""));
        System.out.println(parseTTLValue("2h5ms"));
        System.out.println(parseTTLValue("5ms2h"));
        System.out.println(parseTTLValue("5ms2hmnsec"));
    }

}