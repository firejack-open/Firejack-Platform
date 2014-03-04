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

(function(){tinymce.create("tinymce.plugins.Preview",{init:function(a,b){var d=this,c=tinymce.explode(a.settings.content_css);d.editor=a;tinymce.each(c,function(f,e){c[e]=a.documentBaseURI.toAbsolute(f)});a.addCommand("mcePreview",function(){a.windowManager.open({file:a.getParam("plugin_preview_pageurl",b+"/preview.html"),width:parseInt(a.getParam("plugin_preview_width","550")),height:parseInt(a.getParam("plugin_preview_height","600")),resizable:"yes",scrollbars:"yes",popup_css:c?c.join(","):a.baseURI.toAbsolute("themes/"+a.settings.theme+"/skins/"+a.settings.skin+"/content.css"),inline:a.getParam("plugin_preview_inline",1)},{base:a.documentBaseURI.getURI()})});a.addButton("preview",{title:"preview.preview_desc",cmd:"mcePreview"})},getInfo:function(){return{longname:"Preview",author:"Moxiecode Systems AB",authorurl:"http://tinymce.moxiecode.com",infourl:"http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/preview",version:tinymce.majorVersion+"."+tinymce.minorVersion}}});tinymce.PluginManager.add("preview",tinymce.plugins.Preview)})();