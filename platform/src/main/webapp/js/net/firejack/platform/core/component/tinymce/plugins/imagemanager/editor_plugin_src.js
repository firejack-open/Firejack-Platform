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

(function() {
    // Load plugin specific language pack
    tinymce.PluginManager.requireLangPack('imagemanager');

    tinymce.create('tinymce.plugins.ImageManagerPlugin', {
        /**
         * Initializes the plugin, this will be executed after the plugin has been created.
         * This call is done before the editor instance has finished it's initialization so use the onInit event
         * of the editor instance to intercept that event.
         *
         * @param {tinymce.Editor} ed Editor instance that the plugin is initialized in.
         * @param {string} url Absolute URL to where the plugin is located.
         */
        init : function(ed, url) {
            // Register the command so that it can be invoked by using tinyMCE.activeEditor.execCommand('mceExample');
            ed.addCommand('mceImageManager', function() {
                var id = "imageManagerDialog";
                var imageManagerDialog = Ext.WindowMgr.get(id);
                if (OPF.isEmpty(imageManagerDialog)) {
                    imageManagerDialog = Ext.create('OPF.core.component.tinymce.plugins.imagemanager.dialog.ImageManagerDialog', ed);
                }
                imageManagerDialog.show();
            });

            // Register buttons
            ed.addButton('imagemanager', {
                title : 'imagemanager.photo_desc',
                cmd : 'mceImageManager',
                image : url + '/img/photo.gif'
            });

            // Add a node change handler, selects the button in the UI when a image is selected
            ed.onNodeChange.add(function(ed, cm, n) {
                cm.setActive('imagemanager', n.nodeName == 'IMG');
            });
        },

        /**
         * Creates control instances based in the incomming name. This method is normally not
         * needed since the addButton method of the tinymce.Editor class is a more easy way of adding buttons
         * but you sometimes need to create more complex controls like listboxes, split buttons etc then this
         * method can be used to create those.
         *
         * @param {String} n Name of the control to create.
         * @param {tinymce.ControlManager} cm Control manager to use inorder to create new control.
         * @return {tinymce.ui.Control} New control instance or null if no control was created.
         */
        createControl : function(n, cm) {
            return null;
        },

        /**
         * Returns information about the plugin as a name/value array.
         * The current keys are longname, author, authorurl, infourl and version.
         *
         * @return {Object} Name/value array containing information about the plugin.
         */
        getInfo : function() {
            return {
                longname : 'Image Manager Plugin',
                author : 'Oleg Marshalenko',
                authorurl : 'http://firejack.net',
                infourl : 'http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/attachments',
                version : "1.0"
            };
        }
    });

    // Register plugin
    tinymce.PluginManager.add('imagemanager', tinymce.plugins.ImageManagerPlugin);
})();
