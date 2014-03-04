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

/**
 * @author Shea Frederick - http://www.vinylfox.com
 * @class Ext.ux.form.HtmlEditor.plugins
 * <p>A convenience function that returns a standard set of HtmlEditor buttons.</p>
 * <p>Sample usage:</p>
 * <pre><code>
    new Ext.FormPanel({
        ...
        items : [{
            ...
            xtype           : "htmleditor",
            plugins         : Ext.ux.form.HtmlEditor.plugins()
        }]
    });
 * </code></pre>
 */
Ext.ns('OPF.core.component.htmleditor.plugins.HtmlEditorPlugins');

OPF.core.component.htmleditor.plugins.HtmlEditorPlugins.plugins = function(){
    return [
        new OPF.core.component.htmleditor.plugins.SpecialHeadingFormats()//,
//        new Ext.ux.form.HtmlEditor.Formatblock()
//        new Ext.ux.form.HtmlEditor.Link(),
//        new Ext.ux.form.HtmlEditor.Divider(),
//        new Ext.ux.form.HtmlEditor.Word(),
//        new Ext.ux.form.HtmlEditor.FindAndReplace(),
//        new Ext.ux.form.HtmlEditor.UndoRedo(),
//        new Ext.ux.form.HtmlEditor.Divider(),
//        new Ext.ux.form.HtmlEditor.Image(),
//        new Ext.ux.form.HtmlEditor.Table(),
//        new Ext.ux.form.HtmlEditor.HR(),
//        new Ext.ux.form.HtmlEditor.SpecialCharacters(),
//        new Ext.ux.form.HtmlEditor.HeadingMenu(),
//        new Ext.ux.form.HtmlEditor.IndentOutdent(),
//        new Ext.ux.form.HtmlEditor.SubSuperScript(),
//        new Ext.ux.form.HtmlEditor.RemoveFormat()
    ];
};