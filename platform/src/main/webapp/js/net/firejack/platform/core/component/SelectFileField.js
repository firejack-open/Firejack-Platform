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


Ext.define('OPF.core.component.SelectFileField', {
    extend: 'OPF.core.component.TextField',
    alias : 'widget.opf-selectfilefield',

    cls: 'x-form-field-filepath',

    /**
     * @cfg {Boolean} buttonOnly
     * True to display the file upload field as a button with no visible text field. If true, all
     * inherited Text members will still be available.
     */
    buttonOnly: false,

    /**
     * @cfg {Number} buttonMargin
     * The number of pixels of space reserved between the button and the text field. Note that this only
     * applies if {@link #buttonOnly} = false.
     */
    buttonMargin: 3,

    /**
     * @cfg {Object} buttonConfig
     * A standard {@link Ext.button.Button} config object.
     */

    /**
     * @event change
     * Fires when the underlying file input field's value has changed from the user selecting a new file from the system
     * file selection dialog.
     * @param {Ext.ux.form.FileUploadField} this
     * @param {String} value The file value returned by the underlying file input field
     */

    /**
     * @property {Ext.Element} fileInputEl
     * A reference to the invisible file input element created for this upload field. Only populated after this
     * component is rendered.
     */

    /**
     * @property {Ext.button.Button} button
     * A reference to the trigger Button component created for this upload field. Only populated after this component is
     * rendered.
     */

    /**
     * @cfg {String} [fieldBodyCls='x-form-file-wrap']
     * An extra CSS class to be applied to the body content element in addition to {@link #fieldBodyCls}.
     */
//    fieldBodyCls: Ext.baseCSSPrefix + 'form-file-wrap',

    // private
    componentLayout: 'filefield',

    // private
    onRender: function() {
        var me = this,
            inputEl;

        me.callParent(arguments);

        me.createButton();

        // we don't create the file/button til after onRender, the initial disable() is
        // called in the onRender of the component.
        if (me.disabled) {
            me.disableItems();
        }

        inputEl = me.inputEl;
        if (me.buttonOnly) {
            inputEl.setDisplayed(false);
        }
    },

    /**
     * @private
     * Creates the custom trigger Button component. The fileInput will be inserted into this.
     */
    createButton: function() {
        var me = this;

        me.button = Ext.ComponentMgr.create({
            xtype: 'button',
            cls: 'x-form-select-file',
            style: me.buttonOnly ? '' : 'margin-left:' + me.buttonMargin + 'px',
            renderTo: me.bodyEl,
            buttonConfig: me.buttonConfig,
            handler: function() {
                me.onSelectFileClick();
            }
        });
    },

    /**
     * @private Event handler fired when the user selects a file.
     */
    onFileChange: function() {
        this.lastValue = null; // force change event to get fired even if the user selects a file with the same name
        Ext.form.field.SelectFileField.superclass.setValue.call(this, this.fileInputEl.dom.value);
    },

    reset : function(){
        var me = this;
        if (me.rendered) {
            if (me.fileInputEl) {
                me.fileInputEl.remove();
                me.createFileInput();
            }
            me.inputEl.dom.value = '';
        }
        me.callParent();
    },

    onDisable: function(){
        this.callParent();
        this.disableItems();
    },

    disableItems: function(){
        var file = this.fileInputEl,
            button = this.button;

        if (file) {
            file.dom.disabled = true;
        }
        if (button) {
            button.disable();
        }
    },

    onEnable: function(){
        var me = this;
        me.callParent();
        me.fileInputEl.dom.disabled = false;
        me.button.enable();
    },

    isFileUpload: function() {
        return true;
    },

    extractFileInput: function() {
        var fileInput = this.fileInputEl.dom;
        this.reset();
        return fileInput;
    },

    onDestroy: function(){
        Ext.destroyMembers(this, 'fileInputEl', 'button');
        this.callParent();
    },

    /**
     * The function that should handle the selectFile's click event.  This method does nothing by default
     * until overridden by an implementing function.  See Ext.form.ComboBox and Ext.form.DateField for
     * sample implementations.
     * @method
     */
    onSelectFileClick: function() {
        var fileManagerDialog = OPF.core.component.FileManagerDialog.init(this, true);
        fileManagerDialog.show();
    },

    getErrors: function(value) {
        return getSQErrors(this, value)
    },

    getSubmitData: function() {
        var me = this, data = null, val;
        if (!me.disabled) {
            val = me.getSubmitValue();
            if (val !== null) {
                data = {};
                data[me.getName()] = val;
            }
        }
        return data;
    }

});
