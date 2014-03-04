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

Ext.define('OPF.console.directory.view.UserGridView', {
    extend: 'OPF.core.component.BaseGridView',


    title: 'USERS',
    iconCls: 'sm-user-icon',
    iconGridUrl: '/images/icons/16/user_16.png',
    entityName: 'User',
    registryNodeType: OPF.core.utils.RegistryNodeType.USER,

    getColumns: function() {
        var instance = this;

        return [
            {
                xtype: 'gridcolumn',
                header: '!',
                align: 'center',
                width: 30,
                renderer: function()
                {
                    return '<img src="' + OPF.Cfg.OPF_CONSOLE_URL + '/images/icons/16/user_16.png">';
                }
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'username',
                header: 'Username',
                flex: 2
            },
            {
                xtype: 'gridcolumn',
                header: 'First Name',
                dataIndex: 'firstName',
                flex: 3
            },
            {
                xtype: 'gridcolumn',
                header: 'Last Name',
                dataIndex: 'lastName',
                flex: 3
            },
            {
                xtype: 'gridcolumn',
                dataIndex: 'email',
                header: 'E-Mail',
                flex: 2
            }
        ]
    }

});