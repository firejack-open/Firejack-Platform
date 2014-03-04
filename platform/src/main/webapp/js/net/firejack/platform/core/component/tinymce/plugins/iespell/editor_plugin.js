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

(function(){tinymce.create("tinymce.plugins.IESpell",{init:function(a,b){var c=this,d;if(!tinymce.isIE){return}c.editor=a;a.addCommand("mceIESpell",function(){try{d=new ActiveXObject("ieSpell.ieSpellExtension");d.CheckDocumentNode(a.getDoc().documentElement)}catch(f){if(f.number==-2146827859){a.windowManager.confirm(a.getLang("iespell.download"),function(e){if(e){window.open("http://www.iespell.com/download.php","ieSpellDownload","")}})}else{a.windowManager.alert("Error Loading ieSpell: Exception "+f.number)}}});a.addButton("iespell",{title:"iespell.iespell_desc",cmd:"mceIESpell"})},getInfo:function(){return{longname:"IESpell (IE Only)",author:"Moxiecode Systems AB",authorurl:"http://tinymce.moxiecode.com",infourl:"http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/iespell",version:tinymce.majorVersion+"."+tinymce.minorVersion}}});tinymce.PluginManager.add("iespell",tinymce.plugins.IESpell)})();