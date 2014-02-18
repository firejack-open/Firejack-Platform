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