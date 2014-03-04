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


Ext.define('OPF.console.documentation.manager.DocumentationCollectionElement', {
    extend: 'OPF.console.documentation.manager.DocumentationElement',

    constructor: function(documentationManager, collectionComponent, cfg) {
        cfg = cfg || {};
        OPF.console.documentation.manager.DocumentationCollectionElement.superclass.constructor.call(this, documentationManager, cfg);

        this.contentComponent = collectionComponent;
        this.controlComponent = collectionComponent;
        this.actionComponent = collectionComponent;

        var contentSrcData = this.getSrc(this.contentComponent);
        if (isNotEmpty(contentSrcData)) {
            this.contentSrcData = eval('(' + contentSrcData.nodeValue + ')');
        }
    }

});