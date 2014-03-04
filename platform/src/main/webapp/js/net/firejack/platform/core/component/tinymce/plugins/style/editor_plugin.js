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

(function(){tinymce.create("tinymce.plugins.StylePlugin",{init:function(a,b){a.addCommand("mceStyleProps",function(){var c=false;var f=a.selection.getSelectedBlocks();var d=[];if(f.length===1){d.push(a.selection.getNode().style.cssText)}else{tinymce.each(f,function(g){d.push(a.dom.getAttrib(g,"style"))});c=true}a.windowManager.open({file:b+"/props.htm",width:480+parseInt(a.getLang("style.delta_width",0)),height:340+parseInt(a.getLang("style.delta_height",0)),inline:1},{applyStyleToBlocks:c,plugin_url:b,styles:d})});a.addCommand("mceSetElementStyle",function(d,c){if(e=a.selection.getNode()){a.dom.setAttrib(e,"style",c);a.execCommand("mceRepaint")}});a.onNodeChange.add(function(d,c,f){c.setDisabled("styleprops",f.nodeName==="BODY")});a.addButton("styleprops",{title:"style.desc",cmd:"mceStyleProps"})},getInfo:function(){return{longname:"Style",author:"Moxiecode Systems AB",authorurl:"http://tinymce.moxiecode.com",infourl:"http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/style",version:tinymce.majorVersion+"."+tinymce.minorVersion}}});tinymce.PluginManager.add("style",tinymce.plugins.StylePlugin)})();