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

Ext.define('OPF.prometheus.component.header.HeaderComponent', {
    extend: 'Ext.container.Container',
    alias: 'widget.prometheus.component.header-component',

    autoEl: 'header',
    cls: 'header',

    siteTitleLookup: null,
    siteSubTitleLookup: null,
    siteLogoLookup: null,

    initComponent: function() {

        this.siteLogo = Ext.ComponentMgr.create({
            xtype: 'image-resource-control',
            imgResourceLookup: this.siteLogoLookup,
            componentCls: 'logo'
        });

        /*this.searchSiteField = Ext.ComponentMgr.create({
            xtype: 'opf-textfield',
            name: 'search-site',
            emptyText: 'Search this site'
        });

        this.searchSiteButton = Ext.ComponentMgr.create({
            xtype: 'button',
            iconCls: 'icon-search',
            height: 30,
            scale: 'small',
            scope: this,
            handler: this.onClickSearchButton
        });*/

        this.items = [
            this.siteLogo,
            {
                xtype: 'container',
                cls: 'm-header-search',
                items: [
                    //this.searchSiteField,
                    //this.searchSiteButton
                ]
            }
        ];

        this.callParent(arguments);
    }

});

