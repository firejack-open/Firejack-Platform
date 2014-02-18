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


Ext.define('OPF.core.component.plugin.TreeViewDragDrop', {
    extend: 'Ext.tree.plugin.TreeViewDragDrop',
    alias: 'plugin.opf-treeviewdragdrop',

    beforeDragEnter: function(target, e, id) {
        return true;
    },

    beforeDragOver: function(target, e, id) {
        return true;
    },

    beforeDragDrop: function(target, e, id){
        return true;
    },

    isValidDropPoint: function(targetNode, sourceNodes) {
        return true;
    },

    onViewRender : function(view) {
        var me = this;

        if (me.enableDrag) {
            me.dragZone = Ext.create('Ext.tree.ViewDragZone', {
                view: view,
                ddGroup: me.dragGroup || me.ddGroup,
                dragText: me.dragText,
                repairHighlightColor: me.nodeHighlightColor,
                repairHighlight: me.nodeHighlightOnRepair,
                beforeDragEnter: me.beforeDragEnter,
                beforeDragOver: me.beforeDragOver,
                beforeDragDrop: me.beforeDragDrop
            });
        }

        if (me.enableDrop) {
            me.dropZone = Ext.create('Ext.tree.ViewDropZone', {
                view: view,
                ddGroup: me.dropGroup || me.ddGroup,
                allowContainerDrops: me.allowContainerDrops,
                appendOnly: me.appendOnly,
                allowParentInserts: me.allowParentInserts,
                expandDelay: me.expandDelay,
                dropHighlightColor: me.nodeHighlightColor,
                dropHighlight: me.nodeHighlightOnDrop,
                isValidDropPoint : function(node, position, dragZone, e, data) {
                    if (!node || !data.item) {
                        return false;
                    }

                    var view = this.view,
                        targetNode = view.getRecord(node),
                        draggedRecords = data.records,
                        dataLength = draggedRecords.length,
                        ln = draggedRecords.length,
                        i, record;

                    if (!(targetNode && position && dataLength)) {
                        return false;
                    }

                    for (i = 0; i < ln; i++) {
                        record = draggedRecords[i];
                        if (record.isNode && record.contains(targetNode)) {
                            return false;
                        }
                    }

                    if (position === 'append' && targetNode.get('allowDrop') === false) {
                        return false;
                    }
                    else if (position != 'append' && targetNode.parentNode.get('allowDrop') === false) {
                        return false;
                    }

                    if (Ext.Array.contains(draggedRecords, targetNode)) {
                        return false;
                    }

                    return me.isValidDropPoint(targetNode, draggedRecords);
                }
            });
        }
    }
});
