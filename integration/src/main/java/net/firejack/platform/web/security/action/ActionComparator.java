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