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

(function(){tinymce.PluginManager.requireLangPack("example");tinymce.create("tinymce.plugins.ExamplePlugin",{init:function(a,b){a.addCommand("mceExample",function(){a.windowManager.open({file:b+"/dialog.htm",width:320+parseInt(a.getLang("example.delta_width",0)),height:120+parseInt(a.getLang("example.delta_height",0)),inline:1},{plugin_url:b,some_custom_arg:"custom arg"})});a.addButton("example",{title:"example.desc",cmd:"mceExample",image:b+"/img/example.gif"});a.onNodeChange.add(function(d,c,e){c.setActive("example",e.nodeName=="IMG")})},createControl:function(b,a){return null},getInfo:function(){return{longname:"Example plugin",author:"Some author",authorurl:"http://tinymce.moxiecode.com",infourl:"http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/example",version:"1.0"}}});tinymce.PluginManager.add("example",tinymce.plugins.ExamplePlugin)})();