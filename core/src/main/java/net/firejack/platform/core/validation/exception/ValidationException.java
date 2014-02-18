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

package net.firejack.platform.core.validation.exception;


import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.validation.ValidationMessage;

import java.util.List;


public class ValidationException extends OpenFlameRuntimeException {
    private static final long serialVersionUID = 8439745178525754732L;

    private List<ValidationMessage> validationMessages;

    /**
     * @param validationMessages
     */
    public ValidationException(List<ValidationMessage> validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * @return
     */
    public List<ValidationMessage> getValidationMessages() {
        return validationMessages;
    }

}