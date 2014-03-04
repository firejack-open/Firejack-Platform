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

(function(){tinymce.create("tinymce.plugins.VisualBlocks",{init:function(a,b){var c;if(!window.NodeList){return}a.addCommand("mceVisualBlocks",function(){var e=a.dom,d;if(!c){c=e.uniqueId();d=e.create("link",{id:c,rel:"stylesheet",href:b+"/css/visualblocks.css"});a.getDoc().getElementsByTagName("head")[0].appendChild(d)}else{d=e.get(c);d.disabled=!d.disabled}a.controlManager.setActive("visualblocks",!d.disabled)});a.addButton("visualblocks",{title:"visualblocks.desc",cmd:"mceVisualBlocks"});a.onInit.add(function(){if(a.settings.visualblocks_default_state){a.execCommand("mceVisualBlocks",false,null,{skip_focus:true})}})},getInfo:function(){return{longname:"Visual blocks",author:"Moxiecode Systems AB",authorurl:"http://tinymce.moxiecode.com",infourl:"http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/visualblocks",version:tinymce.majorVersion+"."+tinymce.minorVersion}}});tinymce.PluginManager.add("visualblocks",tinymce.plugins.VisualBlocks)})();