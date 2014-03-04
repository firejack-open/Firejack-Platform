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
    'OPF.prometheus.component.toolbar.ToolbarComponent',
    'OPF.prometheus.component.topmenu.TopMenuComponent',
    'OPF.prometheus.component.breadcrumbs.BreadcrumbsComponent',
    'OPF.prometheus.component.header.HeaderComponent',
    'OPF.prometheus.component.footer.FooterComponent',
    'OPF.prometheus.component.login.LoginComponent',
    'OPF.prometheus.component.forgotpassword.ForgotPasswordComponent',
    'OPF.prometheus.component.content.ContentComponent',
    'OPF.prometheus.component.action.ActionComponent',
    'OPF.prometheus.component.title.TitleComponent'
]);

Ext.define('OPF.prometheus.layout.BaseLayout', {
    extend: 'OPF.prometheus.layout.AbstractLayout',
    alias: 'widget.prometheus.layout.base',

    components: [],

    initComponent: function() {
        var topComponents = [];
        var bodyComponents = [];
        var footerComponents = [];

        Ext.each(this.components, function(component) {
            switch(component.area) {
                case 'top':
                    topComponents.push(component);
                    break;
                case 'body':
                    bodyComponents.push(component);
                    break;
                case 'footer':
                    footerComponents.push(component);
                    break;
            }
        });

        this.items = [
            {
                xtype: 'container',
                cls: 'top-container',
                hidden: topComponents.length == 0,
                items: topComponents
            },
            {
                xtype: 'container',
                cls: 'body-container',
                items: bodyComponents
            },
            {
                xtype: 'container',
                cls: 'footer',
                hidden: footerComponents.length == 0,
                items: footerComponents
            }
        ];

        this.callParent(arguments);
    }

});