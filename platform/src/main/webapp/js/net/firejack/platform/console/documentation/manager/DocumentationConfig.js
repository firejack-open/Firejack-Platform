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

Ext.define('OPF.console.documentation.manager.DocumentationConfig', {
    alternateClassName: ['OPF.DCfg'],

    DOC_URL: null,
    REGISTRY_NODE_ID: null,
    REGISTRY_NODE_TYPE: null,
    LOOKUP_URL_SUFFIX: null,
    LOOKUP: null,
    COUNTRY: null,

    HAS_PERMISSION: null,
    HAS_ADD_PERMISSION: null,
    HAS_EDIT_PERMISSION: null,
    HAS_DELETE_PERMISSION: null,

    MAX_IMAGE_WIDTH: null

});

OPF.DCfg = new OPF.console.documentation.manager.DocumentationConfig();