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
