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

package net.firejack.platform.generate.tools;

import net.firejack.platform.generate.beans.web.store.Method;
import net.firejack.platform.generate.beans.web.store.Param;
import net.firejack.platform.utils.generate.FormattingUtils;


public class Utils extends FormattingUtils {

    /**
     * @param doc
     * @param length
     * @return
     */
    public static String documentFormatting(String doc, int length) {
        return descriptionFormatting(doc, null, length);
    }

    /**
     * @param doc
     * @param method
     * @param length
     * @return
     */
    public static String descriptionFormatting(String doc, Method method, int length) {
        if (doc == null || doc.isEmpty()) return null;
        int max = length;
        StringBuilder builder = new StringBuilder(doc.length());

        String[] split = doc.replace("/", "").split("[\\p{Space}\\*]");

        builder.append("/**\n\t*");
        for (String str : split) {
            if (str.isEmpty()) continue;
            builder.append(str).append(" ");
            if (builder.length() >= max) {
                builder.append("\n\t*");
                max += length;
            }
        }

        if (method != null && method.isRender()) {
            if (method.getParams() != null) {
                for (Param param : method.getParams()) {
                    builder.append("\n\t*@param ").append(param.getName()).append(" ").append(param.getParamName());
                }
            }

            if (method.getReturnType() != null) {
                builder.append("\n\t*@return ").append(method.getReturnType().getName()).append(" ").append(method.getReturnType().getParamName());
            }
        }

        if (builder.subSequence(builder.length() - 1, builder.length()).equals("*")) {
            builder.append("/\n");
        } else {
            builder.append("\t\n*/\n");
        }

        return builder.toString();
    }

}
