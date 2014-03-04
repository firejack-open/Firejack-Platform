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

Ext.namespace('OPF.core.validation');

Ext.define('OPF.core.validation.MessageLevel', {

    constructor: function(level, css, cfg) {
        cfg = cfg || {};
        OPF.core.validation.MessageLevel.superclass.constructor.call(this, cfg);
        this.level = level;
        this.css = css;
    },
    getLevel: function() {
        return this.level;
    },
    getCSS: function() {
        return this.css;
    }
});

OPF.core.validation.MessageLevel.TRACE = new OPF.core.validation.MessageLevel('TRACE', 'msg-trace');
OPF.core.validation.MessageLevel.DEBUG = new OPF.core.validation.MessageLevel('DEBUG', 'msg-debug');
OPF.core.validation.MessageLevel.INFO  = new OPF.core.validation.MessageLevel('INFO',  'msg-info');
OPF.core.validation.MessageLevel.WARN  = new OPF.core.validation.MessageLevel('WARN',  'msg-warn');
OPF.core.validation.MessageLevel.ERROR = new OPF.core.validation.MessageLevel('ERROR', 'msg-error');
OPF.core.validation.MessageLevel.FATAL = new OPF.core.validation.MessageLevel('FATAL', 'msg-fatal');