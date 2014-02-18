/*
 * Firejack Platform - Copyright (c) 2012 Firejack Technologies
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

Ext.namespace('OPF.core.validation');

Ext.define('OPF.core.validation.MessageLevel', {

    constructor: function(level, css, cfg) {
        cfg = cfg || {};
        OPF.core.validation.MessageLevel.superclass.constructor.call(this, cfg);
        this.level = level;
        this.css = css;
    },
    getLevel: function() {
        return this.level;
    },
    getCSS: function() {
        return this.css;
    }
});

OPF.core.validation.MessageLevel.TRACE = new OPF.core.validation.MessageLevel('TRACE', 'msg-trace');
OPF.core.validation.MessageLevel.DEBUG = new OPF.core.validation.MessageLevel('DEBUG', 'msg-debug');
OPF.core.validation.MessageLevel.INFO  = new OPF.core.validation.MessageLevel('INFO',  'msg-info');
OPF.core.validation.MessageLevel.WARN  = new OPF.core.validation.MessageLevel('WARN',  'msg-warn');
OPF.core.validation.MessageLevel.ERROR = new OPF.core.validation.MessageLevel('ERROR', 'msg-error');
OPF.core.validation.MessageLevel.FATAL = new OPF.core.validation.MessageLevel('FATAL', 'msg-fatal');