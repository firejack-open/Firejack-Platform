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

(function(){tinymce.create("tinymce.plugins.PageBreakPlugin",{init:function(b,d){var f='<img src="'+b.theme.url+'/img/trans.gif" class="mcePageBreak mceItemNoResize" />',a="mcePageBreak",c=b.getParam("pagebreak_separator","<!-- pagebreak -->"),e;e=new RegExp(c.replace(/[\?\.\*\[\]\(\)\{\}\+\^\$\:]/g,function(g){return"\\"+g}),"g");b.addCommand("mcePageBreak",function(){b.execCommand("mceInsertContent",0,f)});b.addButton("pagebreak",{title:"pagebreak.desc",cmd:a});b.onInit.add(function(){if(b.theme.onResolveName){b.theme.onResolveName.add(function(g,h){if(h.node.nodeName=="IMG"&&b.dom.hasClass(h.node,a)){h.name="pagebreak"}})}});b.onClick.add(function(g,h){h=h.target;if(h.nodeName==="IMG"&&g.dom.hasClass(h,a)){g.selection.select(h)}});b.onNodeChange.add(function(h,g,i){g.setActive("pagebreak",i.nodeName==="IMG"&&h.dom.hasClass(i,a))});b.onBeforeSetContent.add(function(g,h){h.content=h.content.replace(e,f)});b.onPostProcess.add(function(g,h){if(h.get){h.content=h.content.replace(/<img[^>]+>/g,function(i){if(i.indexOf('class="mcePageBreak')!==-1){i=c}return i})}})},getInfo:function(){return{longname:"PageBreak",author:"Moxiecode Systems AB",authorurl:"http://tinymce.moxiecode.com",infourl:"http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/pagebreak",version:tinymce.majorVersion+"."+tinymce.minorVersion}}});tinymce.PluginManager.add("pagebreak",tinymce.plugins.PageBreakPlugin)})();