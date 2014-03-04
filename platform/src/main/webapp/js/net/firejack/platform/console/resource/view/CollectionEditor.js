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
Ext.define('OPF.console.resource.view.CollectionEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',

    title: 'COLLECTION: [New]',

    infoResourceLookup: 'net.firejack.platform.content.collection',

    /**
     *
     */
    initComponent: function() {
        this.nodeBasicFields = Ext.create('OPF.core.component.editor.BasicInfoFieldSet', this);

        this.descriptionFields = Ext.create('OPF.core.component.editor.DescriptionInfoFieldSet', this);

        this.collectionMembershipFieldSet = Ext.create('OPF.console.resource.view.CollectionMembershipFieldSet', this);

        this.staticBlocks = [
            this.nodeBasicFields
        ];

        this.additionalBlocks = [
            this.descriptionFields,
            this.collectionMembershipFieldSet
        ];

        this.callParent(arguments);
    },

    onAfterSetValue: function(jsonData) {
        if (OPF.isNotEmpty(jsonData.memberships)) {
            this.collectionMembershipFieldSet.membershipStore.loadData(jsonData.memberships);
        }
        this.collectionMembershipFieldSet.resourceStore.load();
    },

    onBeforeSave: function(formData) {
        formData.memberships = getJsonOfStore(this.collectionMembershipFieldSet.membershipStore);
    },

    onRefreshFields: function(selectedNode) {
        this.collectionMembershipFieldSet.resourceStore.load();
    }

});