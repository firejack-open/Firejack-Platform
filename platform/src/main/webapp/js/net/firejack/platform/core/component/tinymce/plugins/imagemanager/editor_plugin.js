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

(function(){tinymce.PluginManager.requireLangPack('imagemanager');tinymce.create('tinymce.plugins.ImageManagerPlugin',{init:function(ed,url){ed.addCommand('mceImageManager',function(){var id="imageManagerDialog";var imageManagerDialog=Ext.WindowMgr.get(id);if(OPF.isEmpty(imageManagerDialog)){imageManagerDialog=Ext.create('OPF.core.component.tinymce.plugins.imagemanager.dialog.ImageManagerDialog',ed)}imageManagerDialog.show()});ed.addButton('imagemanager',{title:'imagemanager.photo_desc',cmd:'mceImageManager',image:url+'/img/photo.gif'});ed.onNodeChange.add(function(ed,cm,n){})},createControl:function(n,cm){return null},getInfo:function(){return{longname:'Image Manager Plugin',author:'Oleg Marshalenko',authorurl:'http://firejack.net',infourl:'http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/attachments',version:"1.0"}}});tinymce.PluginManager.add('imagemanager',tinymce.plugins.ImageManagerPlugin)})();
