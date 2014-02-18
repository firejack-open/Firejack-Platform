/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.web.security.action;

import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.api.registry.domain.Action;
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.core.model.registry.HTTPMethod;
import net.firejack.platform.core.utils.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;


public class ActionDetectorFactory {

    private static ActionDetectorFactory instance;

    private static final Logger logger = Logger.getLogger(ActionDetectorFactory.class);

    public static final String CREATE_ACTION = "create";
    public static final String READ_ACTION = "read";
    public static final String READ_ALL_ACTION = "read-all";
    public static final String UPDATE_ACTION = "update";
    public static final String DELETE_ACTION = "delete";
    public static final String ADVANCED_SEARCH_ACTION = "advanced-search";

    public static final String MESSAGE_ACTION_LIST_IS_EMPTY = "Action list is empty. No action will be detected.";
    public static final String MESSAGE_URL_PATH_IS_NULL = "Provided urlPath is null. No action will be detected.";
    public static final String MESSAGE_READ_ACTION_EXCLUDED = "Exclude \"" + READ_ACTION + "\" action from consideration. Action does not correspond to the Platform REST convention.";
    public static final String MESSAGE_READ_ALL_ACTION_EXCLUDED = "Exclude \"" + READ_ALL_ACTION + "\" action from consideration. Action does not correspond to the Platform REST convention.";
    public static final String MESSAGE_ADVANCED_SEARCH_ACTION_EXCLUDED = "Exclude \"" + ADVANCED_SEARCH_ACTION + "\" action from consideration. Action does not correspond to the Platform REST convention.";
    public static final String MESSAGE_CREATE_ACTION_EXCLUDED = "Exclude \"" + CREATE_ACTION + "\" action from consideration. Action does not correspond to the Platform REST convention.";
    public static final String MESSAGE_UPDATE_ACTION_EXCLUDED = "Exclude \"" + UPDATE_ACTION + "\" action from consideration. Action does not correspond to the Platform REST convention.";
    public static final String MESSAGE_DELETE_ACTION_EXCLUDED = "Exclude \"" + DELETE_ACTION + "\" action from consideration. Action does not correspond to the Platform REST convention.";

    public static final String READ_ACTION_MARKER = "." + READ_ACTION;
    public static final String READ_ALL_ACTION_MARKER = "." + READ_ALL_ACTION;
    public static final String SLASH_SYMBOL = "/";

    private ActionDetectorFactory() {
    }

    /**
     * @return
     */
    public IActionDetector produceDefaultActionDetector() {
        return new DefaultActionDetector();
    }

    /**
     * @return
     */
    public IWSActionDetector produceWSActionDetector() {
        return new WSActionDetector();
    }

    /**
     * @return
     */
    public static ActionDetectorFactory getInstance() {
        if (instance == null) {
            instance = new ActionDetectorFactory();
        }
        return instance;
    }

    /**
     * @param protocol
     * @param urlPath
     * @param httpMethod
     * @param actionList
     * @return
     */
    public static Action searchAction(
            EntityProtocol protocol, String urlPath, String httpMethod, List<Action> actionList) {
        Action resultAction = null;
        if (actionList == null || actionList.isEmpty()) {
            logger.info(MESSAGE_ACTION_LIST_IS_EMPTY);
        } else if (urlPath == null) {
            logger.info(MESSAGE_URL_PATH_IS_NULL);
        } else {
            HTTPMethod method = HTTPMethod.findByName(httpMethod);
            if (protocol == EntityProtocol.HTTP && method != null) {
                Map<String, Action> detectedActions = new HashMap<String, Action>();
                for (Action action : actionList) {
                    String actionUrl = action.getUrlPath() == null ? null : action.getUrlPath().toLowerCase();
                    if (method == action.getMethod()) {
                        String actionName = action.getName().toLowerCase();
                        if (urlPath.equals(actionUrl)) {
                            detectedActions.put(actionName, action);
                        } else if (urlPath.startsWith(actionUrl) && !detectedActions.containsKey(actionName)) {
                            detectedActions.put(actionName, action);
                        }
                    }
                }
                int lastPos = urlPath.lastIndexOf(SLASH_SYMBOL);
                String endsWith = (lastPos == -1) ? urlPath : urlPath.substring(lastPos + 1);
                boolean endsWithNumeric = StringUtils.isNumeric(endsWith);
                if (HTTPMethod.GET == method) {
                    //look for "read" and "read-all" actions first; therefore detectedActions list may contain both read and read-all actions at the same time
                    //also look for advanced-search action
                    while (!detectedActions.isEmpty()) {
                        if (detectedActions.containsKey(READ_ACTION)) {
                            Action readAction = detectedActions.get(READ_ACTION);
                            String actionBaseUrl = endsWithNumeric ? urlPath.substring(0, lastPos) : urlPath;
                            if (actionBaseUrl.equalsIgnoreCase(readAction.getUrlPath()) && endsWithNumeric) {
                                resultAction = readAction;
                                break;
                            } else {
                                detectedActions.remove(READ_ACTION);
                                logger.debug(MESSAGE_READ_ACTION_EXCLUDED);
                            }
                        } else if (detectedActions.containsKey(READ_ALL_ACTION)) {
                            Action readAllAction = detectedActions.get(READ_ALL_ACTION);
                            String actionBaseUrl = endsWithNumeric ? urlPath.substring(0, lastPos) : urlPath;
                            if (!endsWithNumeric && actionBaseUrl.equalsIgnoreCase(readAllAction.getUrlPath())) {
                                resultAction = readAllAction;
                                break;
                            } else {
                                detectedActions.remove(READ_ALL_ACTION);
                                logger.debug(MESSAGE_READ_ALL_ACTION_EXCLUDED);
                            }
                        } else if (detectedActions.containsKey(ADVANCED_SEARCH_ACTION) &&
                                (resultAction = processAdvancedSearchAction(urlPath, detectedActions)) != null) {
                            break;
                        } else {
                            break;
                        }
                    }
                } else if (HTTPMethod.POST == method) {
                    //look for create action first
                    while (!detectedActions.isEmpty()) {
                        if (detectedActions.containsKey(CREATE_ACTION)) {
                            Action createAction = detectedActions.get(CREATE_ACTION);
                            if (urlPath.equalsIgnoreCase(createAction.getUrlPath()) || urlPath.equalsIgnoreCase(createAction.getUrlPath() + SLASH_SYMBOL)) {
                                resultAction = createAction;
                                break;
                            } else {
                                detectedActions.remove(CREATE_ACTION);
                                logger.debug(MESSAGE_CREATE_ACTION_EXCLUDED);
                            }
                        }  else if (detectedActions.containsKey(ADVANCED_SEARCH_ACTION) &&
                                (resultAction = processAdvancedSearchAction(urlPath, detectedActions)) != null) {
                            break;
                        } else {
                            if (detectedActions.isEmpty()) {
                                break;
                            }
                            //check if there any actions with exactly the same url path in detected actions
                            for (Action action : detectedActions.values()) {
                                if (urlPath.equalsIgnoreCase(action.getUrlPath())) {
                                    resultAction = action;
                                    break;
                                }
                            }
                            //if failed to find appropriate action, then look into
                            if (resultAction == null) {
                                SortedSet<Action> sortedAcions = new TreeSet<Action>(new ActionComparator());
                                for (Action action : detectedActions.values()) {
                                    sortedAcions.add(action);
                                }
                                Iterator<Action> it = sortedAcions.iterator();
                                resultAction = it.next();
                            }
                            break;
                        }
                    }
                } else if (HTTPMethod.PUT == method) {
                    //look for update action first
                    if (detectedActions.containsKey(UPDATE_ACTION)) {
                        if (endsWithNumeric) {
                            resultAction = detectedActions.get(UPDATE_ACTION);
                        } else {
                            detectedActions.remove(UPDATE_ACTION);
                            logger.debug(MESSAGE_UPDATE_ACTION_EXCLUDED);
                        }
                    } else if (detectedActions.containsKey(ADVANCED_SEARCH_ACTION)) {
                        resultAction = processAdvancedSearchAction(urlPath, detectedActions);
                    }
                } else if (HTTPMethod.DELETE == method) {
                    //look for delete action first
                    if (detectedActions.containsKey(DELETE_ACTION)) {
                        if (endsWithNumeric) {
                            resultAction = detectedActions.get(DELETE_ACTION);
                        } else {
                            detectedActions.remove(DELETE_ACTION);
                            logger.debug(MESSAGE_DELETE_ACTION_EXCLUDED);
                        }
                    }
                }
                if (resultAction == null && !detectedActions.isEmpty()) {
                    //look for actions that maximally fit to provided url
                    Collection<Action> actions = detectedActions.values();
                    String actionBaseUrl = endsWithNumeric ? urlPath.substring(0, lastPos) : urlPath;
                    for (Action action : actions) {
                        if (actionBaseUrl.equalsIgnoreCase(action.getUrlPath())) {
                            resultAction = action;
                            break;
                        }
                    }
                    //choose first suitable action.
                    if (resultAction == null) {
                        TreeMap<String, Action> actionsMap = new TreeMap<String, Action>();
                        for (Action action : actions) {
                            actionsMap.put(action.getUrlPath(), action);
                        }
                        NavigableMap<String, Action> descendingActions = actionsMap.descendingMap();
                        for (Map.Entry<String, Action> actionEntry : descendingActions.entrySet()) {
                            if (urlPath.startsWith(actionEntry.getKey())) {
                                resultAction = actionEntry.getValue();
                                break;
                            }
                        }
                    }
                }
            }
        }
        return resultAction;
    }

    /**
     * @param userPermission
     * @return
     */
    public static boolean isReadPermissionForType(UserPermission userPermission) {
        return userPermission.getPermission().equals(
                userPermission.getEntityType() + READ_ACTION_MARKER);
    }

    /**
     * @param userPermission
     * @return
     */
    public static boolean isReadAllPermission(UserPermission userPermission) {
        return isReadAllPermission(userPermission.getPermission());
    }

    /**
     * @param userPermission
     * @return
     */
    public static boolean isReadAllPermission(String userPermission) {
        return userPermission.endsWith(READ_ALL_ACTION_MARKER);
    }

    /**
     * @param permission
     * @return
     */
    public static String getReadAllPermissionType(String permission) {
        return isReadAllPermission(permission) ?
                permission.substring(0, permission.length() - ActionDetectorFactory.READ_ALL_ACTION.length() - 1) : null;
    }

    /**
     * @param userPermission
     * @return
     */
    public static boolean isReadPermission(UserPermission userPermission) {
        return userPermission.getPermission().endsWith(READ_ACTION_MARKER);
    }

    /**
     * @param permission
     * @return
     */
    public static boolean isReadPermission(String permission) {
        return permission.endsWith(READ_ACTION_MARKER);
    }

    /**
     * @param userPermission
     * @return
     */
    public static String getReadPermissionType(UserPermission userPermission) {
        return getReadPermissionType(userPermission.getPermission());
    }

    /**
     * @param permission
     * @return
     */
    public static String getReadPermissionType(String permission) {
        return isReadPermission(permission) ?
                permission.substring(0, permission.length() - ActionDetectorFactory.READ_ACTION.length() - 1) : null;
    }

    private static Action processAdvancedSearchAction(String urlPath, Map<String, Action> detectedActions) {
        Action resultAction = null;
        Action advancedSearchAction = detectedActions.get(ADVANCED_SEARCH_ACTION);
        if (urlPath.equalsIgnoreCase(advancedSearchAction.getUrlPath())) {
            resultAction = advancedSearchAction;
        } else {
            detectedActions.remove(ADVANCED_SEARCH_ACTION);
            logger.debug(MESSAGE_ADVANCED_SEARCH_ACTION_EXCLUDED);
        }
        return resultAction;
    }

}