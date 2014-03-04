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

/**
 *
 */
Ext.define('OPF.console.utils.GridUtils', {});

OPF.console.utils.GridUtils.populateColumn = function(dataIndex, header, cfg) {
    return Ext.create('Ext.grid.column.Column', Ext.apply({
        dataIndex: dataIndex,
        header: header,
        sortable: true
    }, cfg));
};

OPF.console.utils.GridUtils.populateNumberColumn = function(dataIndex, header, width, cfg) {
    return Ext.create('Ext.grid.column.Number', Ext.apply({
        dataIndex: dataIndex,
        header: header,
        sortable: true,
        width: width
    }, cfg));
};

OPF.console.utils.GridUtils.populateBooleanColumn = function(dataIndex, header, cfg) {
    return Ext.create('Ext.grid.column.Boolean', Ext.apply({
        dataIndex: dataIndex,
        header: header,
        sortable: true
    }, cfg));
};

OPF.console.utils.GridUtils.populateDateColumn = function(dataIndex, header, width) {
    return Ext.create('Ext.grid.column.Date', {
        dataIndex: dataIndex,
        header: header,
        sortable: true,
        width: width,
        format: 'M j, Y g:i A'
    });
};