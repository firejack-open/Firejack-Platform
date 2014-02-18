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

Ext.define('OPF.core.component.PrintButton', {
    extend: 'Ext.button.Button',
    alias: 'widget.opf-print-button',

    buttonConfig: {},

    printCSS: null,
    printTitle: null,
    printData: null,
    printTpl: null,

    initComponent: function() {
        var me = this;

        this.handler = function() {
            me.print();
        };

        this.callParent(arguments);
    },



    print: function() {
        var me = this;

        if (OPF.isNotEmpty(this.printData) && OPF.isNotEmpty(this.printTpl)) {
            var html = this.printTpl.applyTemplate(this.printData);

            var myWindow = window.open('', '', 'width=600,height=800');
            myWindow.document.write('<html><head>');
            myWindow.document.write('<title>' + this.printTitle + '</title>');
//            myWindow.document.write('<link rel="Stylesheet" type="text/css" href="' + this.printCSS + '" />');
            myWindow.document.write('</head><body>');
            myWindow.document.write(html);
            myWindow.document.write('</body></html>');
            myWindow.print();
        }
    }

});