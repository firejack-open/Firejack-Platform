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



/**
 * OPF.core.utils.MessageUtils
 * @extends Ext.util.Observable
 */
Ext.define('OPF.core.utils.MessageUtils', {
    extend: 'Ext.util.Observable',
    alternateClassName: ['OPF.Msg'],

    /***
     * response status codes.
     */
    STATUS_EXCEPTION :          'exception',
    STATUS_VALIDATION_ERROR :   "validation",
    STATUS_ERROR:               "error",
    STATUS_NOTICE:              "notice",
    STATUS_OK:                  "ok",
    STATUS_HELP:                "help",

    // private, ref to message-box Element.
    msgCt : null,

    constructor: function(cfg) {
        Ext.onReady(this.onReady, this);

        OPF.core.utils.MessageUtils.superclass.constructor.call(this, cfg);
    },

    // @protected, onReady, executes when Ext.onReady fires.
    onReady : function() {
        // create the msgBox container.  used for App.setAlert
        this.msgCt = Ext.DomHelper.insertFirst(document.body, {id:'msg-div'}, true);
        this.msgCt.setStyle('position', 'absolute');
        this.msgCt.setStyle('z-index', 99999);
        this.msgCt.setWidth(300);
    },

    /***
     * setAlert
     * show the message box.  Aliased to addMessage
     * @param {String} msg
     * @param {Boolean} status
     */
    setAlert : function(status, msg) {
        if (OPF.Cfg.DEBUG_MODE && OPF.isNotBlank(msg)) {
            var message = '<ul>';
            if (Ext.isArray(msg)) {
                Ext.each(msg, function(ms) {
                    message += '<li>' + ms + '</li>';
                })
            } else {
                message = '<li>' + msg + '</li>';
            }
            message += '</ul>';

            // add some smarts to msg's duration (div by 13.3 between 3 & 9 seconds)
            var delay = msg.length / 13.3;
            if (delay < 3) {
                delay = 3;
            }
            else if (delay > 9) {
                delay = 9;
            }
            delay = delay * 1000;

            this.msgCt.alignTo(document, 't-t');
            Ext.DomHelper.append(this.msgCt, {html:this.buildMessageBox(status, message)}, true).slideIn('t').ghost("t", {delay: delay, remove:true});
        }
    },

    /***
     * buildMessageBox
     */
    buildMessageBox : function(title, msg) {
        switch (title) {
            case true:
                title = this.STATUS_OK;
                break;
            case false:
                title = this.STATUS_ERROR;
                break;
        }
        return [
            '<div class="app-msg">',
            '<div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>',
            '<div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc"><h3 class="x-icon-text icon-status-' + title + '">', title, '</h3>', msg, '</div></div></div>',
            '<div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>',
            '</div>'
        ].join('');
    },

    /**
     * decodeStatusIcon
     * @param {Object} status
     */
    decodeStatusIcon : function(status) {
        var iconCls = '';
        switch (status) {
            case true:
            case this.STATUS_OK:
                iconCls = this.ICON_OK;
                break;
            case this.STATUS_NOTICE:
                iconCls = this.ICON_NOTICE;
                break;
            case false:
            case this.STATUS_ERROR:
                iconCls = this.ICON_ERROR;
                break;
            case this.STATUS_HELP:
                iconCls = this.ICON_HELP;
                break;
        }
        return iconCls;
    }
});

OPF.Msg = new OPF.core.utils.MessageUtils({});
