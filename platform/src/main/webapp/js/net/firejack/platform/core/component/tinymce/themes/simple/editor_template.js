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

(function(){var a=tinymce.DOM;tinymce.ThemeManager.requireLangPack("simple");tinymce.create("tinymce.themes.SimpleTheme",{init:function(c,d){var e=this,b=["Bold","Italic","Underline","Strikethrough","InsertUnorderedList","InsertOrderedList"],f=c.settings;e.editor=c;c.contentCSS.push(d+"/skins/"+f.skin+"/content.css");c.onInit.add(function(){c.onNodeChange.add(function(h,g){tinymce.each(b,function(i){g.get(i.toLowerCase()).setActive(h.queryCommandState(i))})})});a.loadCSS((f.editor_css?c.documentBaseURI.toAbsolute(f.editor_css):"")||d+"/skins/"+f.skin+"/ui.css")},renderUI:function(h){var e=this,i=h.targetNode,b,c,d=e.editor,f=d.controlManager,g;i=a.insertAfter(a.create("span",{id:d.id+"_container","class":"mceEditor "+d.settings.skin+"SimpleSkin"}),i);i=g=a.add(i,"table",{cellPadding:0,cellSpacing:0,"class":"mceLayout"});i=c=a.add(i,"tbody");i=a.add(c,"tr");i=b=a.add(a.add(i,"td"),"div",{"class":"mceIframeContainer"});i=a.add(a.add(c,"tr",{"class":"last"}),"td",{"class":"mceToolbar mceLast",align:"center"});c=e.toolbar=f.createToolbar("tools1");c.add(f.createButton("bold",{title:"simple.bold_desc",cmd:"Bold"}));c.add(f.createButton("italic",{title:"simple.italic_desc",cmd:"Italic"}));c.add(f.createButton("underline",{title:"simple.underline_desc",cmd:"Underline"}));c.add(f.createButton("strikethrough",{title:"simple.striketrough_desc",cmd:"Strikethrough"}));c.add(f.createSeparator());c.add(f.createButton("undo",{title:"simple.undo_desc",cmd:"Undo"}));c.add(f.createButton("redo",{title:"simple.redo_desc",cmd:"Redo"}));c.add(f.createSeparator());c.add(f.createButton("cleanup",{title:"simple.cleanup_desc",cmd:"mceCleanup"}));c.add(f.createSeparator());c.add(f.createButton("insertunorderedlist",{title:"simple.bullist_desc",cmd:"InsertUnorderedList"}));c.add(f.createButton("insertorderedlist",{title:"simple.numlist_desc",cmd:"InsertOrderedList"}));c.renderTo(i);return{iframeContainer:b,editorContainer:d.id+"_container",sizeContainer:g,deltaHeight:-20}},getInfo:function(){return{longname:"Simple theme",author:"Moxiecode Systems AB",authorurl:"http://tinymce.moxiecode.com",version:tinymce.majorVersion+"."+tinymce.minorVersion}}});tinymce.ThemeManager.add("simple",tinymce.themes.SimpleTheme)})();