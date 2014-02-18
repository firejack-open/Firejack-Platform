/*
 * Firejack Platform - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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