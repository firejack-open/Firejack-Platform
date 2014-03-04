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


Ext.define('OPF.console.installation.view.InstallationView', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.installation-page',

    title: 'Installation Page',
    layout: 'fit',
    height: 500,

    initComponent: function() {
        var instance = this;

        this.info = Ext.ComponentMgr.create({
            xtype: 'panel',
            flex: 1,
            padding: 10,
            border: false,
            html: 'Firejack Platform is not properly installed and configured on the target server. ' +
                  'Please edit the environment.xml file and set the FIREJACK_CONFIG environment ' +
                  'variable and re-run the installation script to initialize the server. ' +
                  'This page will be replaced with the working version of the Firejack Platform Console ' +
                  'when installation is successful.'
        });

        this.items = [
            this.info
        ];

        this.callParent(arguments);
    }

});