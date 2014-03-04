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

package net.firejack.platform.utils;

import net.firejack.platform.core.utils.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParamUtils {

    private static final Logger logger = Logger.getLogger(ParamUtils.class);

    private static final String DEFAULT_SEPARATOR = ",";
    private static final String DEFAULT_SEPARATOR_REG_EXP = "\\,";

    public static String composeListParam(List<Long> idList) {
        return composeListParam(DEFAULT_SEPARATOR, idList);
    }

    public static String composeArrayParam(Long... idList) {
        return composeArrayParam(DEFAULT_SEPARATOR, idList);
    }

    public static String composeListParam(String separator, List<Long> idList) {
        if (separator == null || separator.isEmpty()) {
            throw new IllegalArgumentException("Separator parameter should not be blank.");
        }
        String result;
        if (idList == null || idList.isEmpty()) {
            result = "";//maybe better to return null
        } else {
            result = StringUtils.join(idList, DEFAULT_SEPARATOR);
        }
        return result;
    }

    public static String composeArrayParam(String separator, Long... idList) {
        if (StringUtils.isBlank(separator)) {
            throw new IllegalArgumentException("Separator parameter should not be blank.");
        }
        String result;
        if (idList.length == 0) {
            result = "";
        } else {
            result = StringUtils.join(idList, DEFAULT_SEPARATOR);
        }
        return result;
    }

    public static List<Long> parseListParam(String param) {
        return parseListParam(param, DEFAULT_SEPARATOR_REG_EXP);
    }

    public static Long[] parseArrayParam(String param) {
        return parseArrayParam(param, DEFAULT_SEPARATOR_REG_EXP);
    }

    public static List<Long> parseListParam(String param, String separatorRegExp) {
        if (separatorRegExp == null || separatorRegExp.isEmpty()) {
            throw new IllegalArgumentException("separatorRegExp parameter should not be null");
        }
        List<Long> idList;
        if (param == null) {
            idList = null;
        } else if (StringUtils.isBlank(param)){
            idList = Collections.emptyList();
        } else {
            String[] idCandidates = param.split(separatorRegExp);
            idList = new ArrayList<Long>(idCandidates.length);
            for (String idCandidate : idCandidates) {
                try {
                    idList.add(Long.parseLong(idCandidate));
                } catch (NumberFormatException e) {
                    logger.error(e.getMessage(), e);
                    throw new IllegalArgumentException(e);
                }
            }
        }
        return idList;
    }

    public static Long[] parseArrayParam(String param, String separatorRegExp) {
        if (separatorRegExp == null || separatorRegExp.isEmpty()) {
            throw new IllegalArgumentException("separatorRegExp parameter should not be null");
        }
        Long[] idList;
        if (param == null) {
            idList = null;
        } else if (StringUtils.isBlank(param)){
            idList = new Long[0];
        } else {
            String[] idCandidates = param.split(separatorRegExp);
            idList = new Long[idCandidates.length];
            int i = 0;
            for (String idCandidate : idCandidates) {
                try {
                    idList[i++] = Long.parseLong(idCandidate);
                } catch (NumberFormatException e) {
                    logger.error(e.getMessage(), e);
                    throw new IllegalArgumentException(e);
                }
            }
        }
        return idList;
    }

}