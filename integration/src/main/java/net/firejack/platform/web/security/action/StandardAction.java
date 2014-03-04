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

public enum StandardAction {

    READ, READ_ALL, CREATE, UPDATE, DELETE, ADVANCED_SEARCH;

    public static StandardAction detectStandardAction(Action action) {
        return isReadAction(action) ? READ : isReadAllAction(action) ? READ_ALL :
                isCreateAction(action) ? CREATE : isUpdateAction(action) ? UPDATE :
                        isDeleteAction(action) ? DELETE : isAdvancedSearchAction(action) ? ADVANCED_SEARCH : null;
    }

    public static boolean isReadAction(Action action) {
        return HTTPMethod.GET.equals(action.getMethod()) &&
                action.getName().equalsIgnoreCase(ActionDetectorFactory.READ_ACTION);
    }

    public static boolean isCreateAction(Action action) {
        return HTTPMethod.POST.equals(action.getMethod()) &&
                action.getName().equalsIgnoreCase(ActionDetectorFactory.CREATE_ACTION);
    }

    public static boolean isUpdateAction(Action action) {
        return HTTPMethod.PUT.equals(action.getMethod()) &&
                action.getName().equalsIgnoreCase(ActionDetectorFactory.UPDATE_ACTION);
    }

    public static boolean isDeleteAction(Action action) {
        return HTTPMethod.DELETE.equals(action.getMethod()) &&
                action.getName().equalsIgnoreCase(ActionDetectorFactory.DELETE_ACTION);
    }

    public static boolean isReadAllAction(Action action) {
        return HTTPMethod.GET.equals(action.getMethod()) &&
                action.getName().equalsIgnoreCase(ActionDetectorFactory.READ_ALL_ACTION);
    }

    public static boolean isAdvancedSearchAction(Action action) {
        return action.getName().equalsIgnoreCase(ActionDetectorFactory.ADVANCED_SEARCH_ACTION);
    }

}