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

package net.firejack.platform.web.security.action;

import net.firejack.platform.api.registry.domain.Action;
import net.firejack.platform.core.model.registry.HTTPMethod;

import java.util.Comparator;

public class ActionComparator implements Comparator<Action> {

    @Override
    public int compare(Action first, Action second) {
        int result;
        if (first == null) {
            result = second == null ? 0 : -1;
        } else if (second == null) {
            result = 1;
        } else if (second.equals(first)) {
            result = 0;
        } else {
            result = compareUrlPath(first.getUrlPath(), second.getUrlPath());
            if (result == 0) {
                result = compareMethods(first.getMethod(), second.getMethod());
                if (result == 0) {
                    result = compareUrlPath(first.getSoapUrlPath(), second.getSoapUrlPath());
                    if (result == 0) {
                        result = compareMethods(first.getSoapMethod(), second.getSoapMethod());
                    }
                }
            }
        }
        return result;
    }

    private int compareUrlPath(String urlPath1, String urlPath2) {
        int result;
        if (urlPath1 == null && urlPath2 != null) {
            result = 1;
        } else if (urlPath1 != null && urlPath2 == null) {
            result = -1;
        } else if (urlPath1 == null) {
            result = 0;
        } else if (urlPath1.equals(urlPath2)) {
            result = 0;
        } else if (urlPath1.contains(urlPath2)) {
            result = -1;
        } else if (urlPath2.contains(urlPath1)) {
            result = 1;
        } else {
            result = urlPath2.compareTo(urlPath1);
        }
        return result;
    }

    private int compareMethods(HTTPMethod httpMethod1, HTTPMethod httpMethod2) {
        int result;
        if (httpMethod1 == null && httpMethod2 != null) {
            result = 1;
        } else if (httpMethod1 == null) {
            result = 0;
        } else {
            result = httpMethod1.compareTo(httpMethod2);
        }
        return result;
    }

    private int compareMethods(String httpMethod1, String httpMethod2) {
        int result;
        if (httpMethod1 == null && httpMethod2 != null) {
            result = 1;
        } else if (httpMethod1 == null) {
            result = 0;
        } else {
            result = httpMethod1.compareTo(httpMethod2);
        }
        return result;
    }
}