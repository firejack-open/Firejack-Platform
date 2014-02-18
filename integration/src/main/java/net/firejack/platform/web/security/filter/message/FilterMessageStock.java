/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.web.security.filter.message;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

public class FilterMessageStock {

    private static final String FILTER_MESSAGE_ATTRIBUTE = "FILTER_MESSAGE_ATTRIBUTE";

    private static FilterMessageStock filterMessageStock;
    private HttpSession httpSession;

    private FilterMessageStock() {
    }

    public static FilterMessageStock getInstance() {
        if (filterMessageStock == null) {
            filterMessageStock = new FilterMessageStock();
        }
        return filterMessageStock;
    }

    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public List<FilterMessage> getFilterMessages() {
        List<FilterMessage> filterMessages = (List<FilterMessage>) httpSession.getAttribute(FILTER_MESSAGE_ATTRIBUTE);
        httpSession.removeAttribute(FILTER_MESSAGE_ATTRIBUTE);
        if (filterMessages == null) {
            filterMessages = new ArrayList<FilterMessage>();
        }
        return filterMessages;
    }

    public void addFilterMessage(FilterMessageType type, String message) {
        FilterMessage filterMessage = new FilterMessage(type, message);
        addFilterMessage(filterMessage);
    }

    public void addFilterMessage(FilterMessage filterMessage) {
        List<FilterMessage> filterMessages = (List<FilterMessage>) httpSession.getAttribute(FILTER_MESSAGE_ATTRIBUTE);
        if (filterMessages == null) {
            filterMessages = new ArrayList<FilterMessage>();
            httpSession.setAttribute(FILTER_MESSAGE_ATTRIBUTE, filterMessages);
        }
        filterMessages.add(filterMessage);
    }

}
