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