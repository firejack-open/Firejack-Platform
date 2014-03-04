/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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