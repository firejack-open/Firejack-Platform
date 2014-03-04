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
