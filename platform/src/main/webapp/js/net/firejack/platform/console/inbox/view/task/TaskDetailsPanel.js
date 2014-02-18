/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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

/**
 *
 */
Ext.define('OPF.console.inbox.view.task.TaskDetailsPanel', {
    extend: 'Ext.panel.Panel',

    //title: 'Details',
    layout: 'fit',
    //flex: 1,
    border: false,

    managerLayout: null,

    caseObjectTabs: new Array(),

    constructor: function(managerLayout, cfg) {
        cfg = cfg || {};
        OPF.console.inbox.view.task.TaskDetailsPanel.superclass.constructor.call(this, Ext.apply({
            managerLayout: managerLayout
        }, cfg));
    },

    initComponent: function() {
        this.processInfoTab = Ext.create('OPF.console.inbox.view.process.ProcessInfoPanel', this.managerLayout);
        this.notesTab = Ext.create('OPF.console.inbox.view.NotesPanel', this.managerLayout);
        this.caseAttachmentTab = Ext.create('OPF.console.inbox.view.processcase.CaseAttachmentsPanel', this.managerLayout);
        this.taskHistoryTab = Ext.create('OPF.console.inbox.view.task.TaskHistoryPanel', this.managerLayout);

        this.detailPanels = new Array();
        this.detailPanels.push(this.processInfoTab);
        this.detailPanels.push(this.notesTab);
        this.detailPanels.push(this.caseAttachmentTab);
        this.detailPanels.push(this.taskHistoryTab);

        this.cardsHolder = Ext.create('Ext.panel.Panel', {
            layout: 'card',
            border: false,
            defaults: {
                border: false
            },
            items: Ext.Array.clone(this.detailPanels)
        });

        this.items = this.cardsHolder;

        this.callParent(arguments);
    },

    setActiveItem: function(card) {
        if (OPF.isNotEmpty(card)) {
            this.cardsHolder.getLayout().setActiveItem(card);
        }
    },

    setCaseObjectTabs: function(caseObjects) {
        var instance = this;

        var setNewTabToActive = false;

        var activeTab = this.cardsHolder.getLayout().getActiveItem();
        if (activeTab.name == 'associatedObjectPanel') {
            setNewTabToActive = true;
        }

        var refreshRequired = false;

        Ext.each(this.caseObjectTabs, function (caseObjectTab) {
            //instance.tabPanel.remove(caseObjectTab, true); //autoDestroy
            instance.cardsHolder.items.remove(caseObjectTab);
            instance.detailPanels = Ext.Array.remove(instance.detailPanels, caseObjectTab);
            refreshRequired = true;
            delete caseObjectTab;
        });

        this.caseObjectTabs = new Array();

        if (OPF.isNotEmpty(caseObjects)) {
            Ext.each(caseObjects, function (caseObject) {
                var tabTitle = "CaseObject"; // Need to change it
                var caseObjectTab = Ext.create('OPF.console.inbox.view.AssociatedObjectPanel',
                    instance.managerLayout, tabTitle, caseObject, {
                    name: 'associatedObjectPanel' // need to find this objects tab in tab panel
                });
                instance.caseObjectTabs.push(caseObjectTab);
            });
        }

        //this.tabPanel.add(this.caseObjectTabs);
        this.cardsHolder.items.addAll(this.caseObjectTabs);
        Ext.each(this.caseObjectTabs, function(caseObjectTab) {
            instance.detailPanels.push(caseObjectTab);
        });
        if (this.caseObjectTabs.length > 0) {
            refreshRequired = true;
        }
        if (setNewTabToActive && this.caseObjectTabs.length > 0) {
            this.cardsHolder.getLayout().setActiveItem(this.caseObjectTabs[0]);
        }

        if (refreshRequired) {
            this.managerLayout.navPanel.items.removeAll();
            var navPanel = this.managerLayout.populateNavPanel();
            this.managerLayout.navPanel.items.addAll(navPanel);
        }
        //this.tabPanel.doLayout();
    },

    refreshPanelsForced: function() {
        this.refreshPanels(this.taskId, this.caseId, true);
    },

    /**
     *
     * @param taskId
     * @param caseId
     * @param forceRefresh if true, even if taskId & caseId are not changed, forces caseInfo, notes and history tabs (only them) to refresh
     */
    refreshPanels: function(taskId, caseId, forceRefresh) {

        if (this.cardsHolder.getLayout().getActiveItem() == this.processInfoTab) {
            if (this.caseId != caseId || forceRefresh) {
                this.processInfoTab.updateProcessDetails(caseId);
            }
        }

        if (this.cardsHolder.getLayout().getActiveItem() == this.notesTab) {
            if (this.caseId != caseId || this.taskId != taskId || forceRefresh) {
                this.notesTab.refreshPanel(taskId, caseId);
            }
        }

        if (this.cardsHolder.getLayout().getActiveItem() == this.taskHistoryTab) {
            if (this.caseId != caseId || forceRefresh) {
                this.taskHistoryTab.refreshPanel(caseId);
            }
        }

        if (this.cardsHolder.getLayout().getActiveItem() == this.caseAttachmentTab) {
            if (this.caseId != caseId) {
                this.caseAttachmentTab.refreshPanel(caseId);
            }
        }

        if (this.caseId != caseId) {
            if (OPF.isNotEmpty(caseId)) {
                var instance = this;

                Ext.Ajax.request({        //TODO this is a duplicate request to server if processInfoTab is active (it uses the same service)
                    url: OPF.core.utils.RegistryNodeType.CASE.generateGetUrl(caseId),
                    method: 'GET',
                    success: function(response, action) {
                        var resp = Ext.decode(response.responseText);
                        if (OPF.isNotEmpty(resp.data) && resp.data.length > 0) {
                            var caseObjects = resp.data[0].caseObjects;
                            instance.setCaseObjectTabs(caseObjects);
                        }
                    }
                });
            } else {
                this.setCaseObjectTabs(); // remove them all
            }
        }

        this.caseId = caseId;
        this.taskId = taskId;
    },

    getDetailPanels: function() {
        return Ext.Array.clone(this.detailPanels);
    }

});