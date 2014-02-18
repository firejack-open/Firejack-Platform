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

package net.firejack.platform.core.utils.documentation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum DocumentationEntryType {

    REST_EXAMPLE("REST Example"), SOAP_EXAMPLE("BlazeDS Example"), BLAZE_DS_EXAMPLE("SOAP Example");

    private String docResourceName;

    DocumentationEntryType(String docResourceName) {
        this.docResourceName = docResourceName;
    }

    /**
     * @param resourceName
     * @return
     */
    public static DocumentationEntryType detectByResourceName(String resourceName) {
        DocumentationEntryType[] types = values();
        for (DocumentationEntryType type : types) {
            if (type.docResourceName.equals(resourceName)) {
                return type;
            }
        }
        return null;
    }

    /**
     * @param docEntryTypes
     * @return
     */
    public static Set<DocumentationEntryType> otherDocumentationEntries(Set<DocumentationEntryType> docEntryTypes) {
        Set<DocumentationEntryType> resultSet;
        DocumentationEntryType[] initialTypes = values();
        if (docEntryTypes == null || docEntryTypes.isEmpty()) {
            resultSet = new HashSet<DocumentationEntryType>(Arrays.asList(initialTypes));
        } else {
            resultSet = new HashSet<DocumentationEntryType>();
            for (DocumentationEntryType type : initialTypes) {
                if (!docEntryTypes.contains(type)) {
                    resultSet.add(type);
                }
            }
        }
        return resultSet;
    }
}