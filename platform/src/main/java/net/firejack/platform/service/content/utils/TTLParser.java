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