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
Ext.define('OPF.console.domain.view.RootDomainEditor', {
    extend: 'OPF.core.component.editor.BaseEditor',

    title: 'ROOT DOMAIN: [New]',

//    pageSuffixUrl: 'console/domain',
//    restSuffixUrl: 'registry/root_domain',
//    modelClassName: 'OPF.console.domain.model.RootDomain',
//    constraintName: 'OPF.registry.RootDomain',

    editableWithChild: true,

    infoResourceLookup: 'net.firejack.platform.registry.registry-node.root-domain',

    /**
     *
     */
    initComponent: function() {

        this.nodeBasicFields = Ext.create('OPF.core.component.editor.BasicInfoFieldSet', this, {
            updateLookup: function() {
                var path = ifBlank(this.pathField.getValue(), '');
                var name = ifBlank(this.nameField.getValue(), '');
                var lookupParts = reverse(name.split('.'));
                var lookup = '';
                Ext.each(lookupParts, function(lookupPart, index) {
                    lookupPart = lookupPart.replace(/[\s!"#$%&'()*+,-./:;<=>?@\[\\\]^_`{|}~]+/g, '-');
                    lookup += (index > 0 ? '.' : '') + lookupPart;
                });
                this.lookupField.setValue(lookup.toLowerCase());
                return lookup;
            }
        });

        this.descriptionFields = Ext.create('OPF.core.component.editor.DescriptionInfoFieldSet', this);

        this.staticBlocks = [
            this.nodeBasicFields
        ];

        this.additionalBlocks = [
            this.descriptionFields
        ];

        this.callParent(arguments);
    },

    refreshFields: function(selectedNode) {
        this.nodeBasicFields.pathField.setValue('');
        this.nodeBasicFields.updateLookup();
    }

});