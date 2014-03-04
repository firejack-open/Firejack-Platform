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

(function(a){a.create("tinymce.plugins.EmotionsPlugin",{init:function(b,c){b.addCommand("mceEmotion",function(){b.windowManager.open({file:c+"/emotions.htm",width:250+parseInt(b.getLang("emotions.delta_width",0)),height:160+parseInt(b.getLang("emotions.delta_height",0)),inline:1},{plugin_url:c})});b.addButton("emotions",{title:"emotions.emotions_desc",cmd:"mceEmotion"})},getInfo:function(){return{longname:"Emotions",author:"Moxiecode Systems AB",authorurl:"http://tinymce.moxiecode.com",infourl:"http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/emotions",version:a.majorVersion+"."+a.minorVersion}}});a.PluginManager.add("emotions",a.plugins.EmotionsPlugin)})(tinymce);