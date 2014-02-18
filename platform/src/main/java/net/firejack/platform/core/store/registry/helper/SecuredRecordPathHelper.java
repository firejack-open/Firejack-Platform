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

package net.firejack.platform.core.store.registry.helper;

import net.firejack.platform.core.model.registry.authority.SecuredRecordModel;
import net.firejack.platform.core.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class SecuredRecordPathHelper {

    public static final String EMPTY_PATH = "['']";

    /**
     * @param securedRecordId
     * @param paths
     * @return
     */
    public String addIdToPaths(Long securedRecordId, String paths) {
        SecuredRecordPaths securedRecordPaths = buildSecuredRecordPaths(paths);
        for (SecuredRecordPath securedRecordPath : securedRecordPaths.getPaths()) {
            securedRecordPath.addId(securedRecordId);
        }
        return generatePath(securedRecordPaths);
    }

    /**
     * @param paths
     * @param newSecuredRecordPath
     * @return
     */
    public String addIdToPaths(String paths, SecuredRecordModel newSecuredRecordPath) {
        String newPath = addIdToPaths(newSecuredRecordPath.getId(), newSecuredRecordPath.getPaths());
        return addPathToPaths(paths, newPath);
    }

    /**
     * @param path
     * @param paths
     * @return
     */
    public String addPathToPaths(String path, String paths) {
        SecuredRecordPaths existsPath = buildSecuredRecordPaths(paths);
        SecuredRecordPaths additionalPath = buildSecuredRecordPaths(path);
        for (SecuredRecordPath securedRecordPath : additionalPath.getPaths()) {
            existsPath.addPath(securedRecordPath);
        }
        return generatePath(existsPath);
    }

    /**
     * @param paths
     * @param oldSecuredRecordPath
     * @return
     */
    public String removeIdFromPaths(String paths, SecuredRecordModel oldSecuredRecordPath) {
        String oldPath = addIdToPaths(oldSecuredRecordPath.getId(), oldSecuredRecordPath.getPaths());
        return removePathFromPaths(paths, oldPath);
    }

    /**
     * @param path
     * @param paths
     * @return
     */
    public String removePathFromPaths(String path, String paths) {
        SecuredRecordPaths existsPath = buildSecuredRecordPaths(path);
        SecuredRecordPaths removalPath = buildSecuredRecordPaths(paths);
        for (SecuredRecordPath securedRecordPath : removalPath.getPaths()) {
            existsPath.removePath(securedRecordPath);
        }
        return generatePath(existsPath);
    }

    /**
     * @param paths
     * @param newSecuredRecordPath
     * @param oldSecuredRecordPath
     * @return
     */
    public String switchPathToPaths(String paths, SecuredRecordModel newSecuredRecordPath, SecuredRecordModel oldSecuredRecordPath) {
        String newPath = addIdToPaths(newSecuredRecordPath.getId(), newSecuredRecordPath.getPaths());
        String oldPath = addIdToPaths(oldSecuredRecordPath.getId(), oldSecuredRecordPath.getPaths());
        paths = removePathFromPaths(paths, oldPath);
        return addPathToPaths(paths, newPath);
    }

    /**
     * @param parentPaths
     * @return
     */
    public SecuredRecordPaths buildSecuredRecordPaths(String parentPaths) {
        SecuredRecordPaths securedRecordPaths = new SecuredRecordPaths();
        parentPaths = parentPaths.substring(1, parentPaths.length() - 1); //remove square brackets
        String[] parentPathArray = parentPaths.split(","); //split by comma
        for (int i = 0, parentPathArrayLength = parentPathArray.length; i < parentPathArrayLength; i++) {
            String parentPath = parentPathArray[i].trim();
            if (parentPath.indexOf("'") == 0 && (parentPath.lastIndexOf("'") + 1) == parentPath.length()) {
                parentPath = parentPath.substring(1, parentPath.length() - 1); //remove single quotes
                SecuredRecordPath securedRecordPath = new SecuredRecordPath();
                if (StringUtils.isNotBlank(parentPath)) {
                    String[] parentIds = parentPath.split(":"); //split by colon
                    for (String parentId : parentIds) {
                        Long id = Long.parseLong(parentId);
                        securedRecordPath.addId(id);
                    }
                }
                securedRecordPaths.addPath(securedRecordPath);
            }
        }
        return securedRecordPaths;
    }

    private String generatePath(SecuredRecordPaths securedRecordPaths) {
        Set<String> paths = new LinkedHashSet<String>();
        for (SecuredRecordPath securedRecordPath : securedRecordPaths.getPaths()) {
            String path = StringUtils.join(securedRecordPath.getIds().iterator(), ":");
            paths.add("'" + path + "'");
        }
        String result = StringUtils.join(paths.iterator(), ","); // join parent paths
        return "[" + result + "]"; // add square brackets
    }

}
