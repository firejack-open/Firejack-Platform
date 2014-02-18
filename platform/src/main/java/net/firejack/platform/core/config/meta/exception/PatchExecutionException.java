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

package net.firejack.platform.core.config.meta.exception;

import net.firejack.platform.core.config.translate.TranslationError;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;


public class PatchExecutionException extends OpenFlameRuntimeException {

    private static final long serialVersionUID = -704137062012073100L;
    private TranslationError[] errors;

    /***/
    public PatchExecutionException() {
    }

    /**
     * @param errors
     */
    public PatchExecutionException(TranslationError[] errors) {
        this.errors = errors;
    }

    /**
     * @param message
     * @param errors
     */
    public PatchExecutionException(String message, TranslationError[] errors) {
        super(message);
        this.errors = errors;
    }

    /**
     * @param cause
     */
    public PatchExecutionException(Throwable cause) {
        super(cause);
    }
}