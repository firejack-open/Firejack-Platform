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

Ext.define('OPF.console.inbox.store.Explanations', {
    extend: 'Ext.data.Store',
    restful: false,
    dialog: null,
    constructor: function(dialog, cfg) {
        cfg = cfg || {};
        OPF.console.inbox.store.Explanations.superclass.constructor.call(this, Ext.apply({
            dialog: dialog,
            model: 'OPF.console.domain.model.CaseExplanationModel',
            proxy: {
                type: 'memory',
                reader: {
                    type: 'json',
                    idProperty: 'id',
                    root: 'data'
                },
                writer: {
                    type: 'json'
                }
            }
        }, cfg));
    },

    reloadStore: function(processId) {
        var dlg = this.dialog;
        var url = OPF.core.utils.RegistryNodeType.CASE_EXPLANATION.generateUrl('/search/') +
                (OPF.isBlank(dlg.processId) ? '' : '?processId=' + dlg.processId);
        Ext.Ajax.request({
            url: url,
            method: 'GET',
            success: function(response, action) {
                var resp = Ext.decode(response.responseText);
                if (OPF.isNotEmpty(resp)) {
                    if (!resp.success) {
                        Ext.Msg.alert('Error', resp.message);
                        dlg.explanationField.hide();
                        return;
                    }
                }
                var explanations = resp.data;
                if (OPF.isNotEmpty(explanations) && OPF.isNotEmpty(explanations.length) &&  explanations.length > 0) {
                    var models = [];
                    for (var i = 0; i < explanations.length; i++) {
                        models.push(Ext.create('OPF.console.domain.model.CaseExplanationModel', explanations[i]));
                    }
                    dlg.explanationField.store.loadData(models);
                    dlg.explanationField.setValue(models[0]);
                    dlg.explanationField.show();
                } else {
                    dlg.explanationField.hide();
                }
            }
        });
    }
});