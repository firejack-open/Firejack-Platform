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

(function(){tinymce.create("tinymce.plugins.AdvancedHRPlugin",{init:function(a,b){a.addCommand("mceAdvancedHr",function(){a.windowManager.open({file:b+"/rule.htm",width:250+parseInt(a.getLang("advhr.delta_width",0)),height:160+parseInt(a.getLang("advhr.delta_height",0)),inline:1},{plugin_url:b})});a.addButton("advhr",{title:"advhr.advhr_desc",cmd:"mceAdvancedHr"});a.onNodeChange.add(function(d,c,e){c.setActive("advhr",e.nodeName=="HR")});a.onClick.add(function(c,d){d=d.target;if(d.nodeName==="HR"){c.selection.select(d)}})},getInfo:function(){return{longname:"Advanced HR",author:"Moxiecode Systems AB",authorurl:"http://tinymce.moxiecode.com",infourl:"http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/advhr",version:tinymce.majorVersion+"."+tinymce.minorVersion}}});tinymce.PluginManager.add("advhr",tinymce.plugins.AdvancedHRPlugin)})();