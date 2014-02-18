/*
 * Firejack Platform - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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