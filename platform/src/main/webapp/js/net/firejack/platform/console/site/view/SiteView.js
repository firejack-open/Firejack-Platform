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

Ext.require([
    'OPF.console.PageLayout',
    'OPF.console.NavigationPageLayout'
]);

Ext.define('OPF.console.site.view.Site', {
    extend: 'OPF.console.NavigationPageLayout',
    alias: 'widget.site-page',

    activeButtonLookup: 'net.firejack.platform.console.site',

    initComponent: function() {
        var instance = this;

        this.rnButtons = [
            {
                xtype: 'opf-rnbutton',
                scale: 'large',
                tooltip: 'add nav element',
                cls: 'navElementIcon',
                iconCls: 'btn-add-element',
                registryNodeType: OPF.core.utils.RegistryNodeType.NAVIGATION_ELEMENT,
                managerLayout: instance
            }
        ];

        this.infoContent = new OPF.core.component.resource.ResourceControl({
            htmlResourceLookup: 'net.firejack.platform.site.info-html'
        });

        this.callParent(arguments);
    }

});