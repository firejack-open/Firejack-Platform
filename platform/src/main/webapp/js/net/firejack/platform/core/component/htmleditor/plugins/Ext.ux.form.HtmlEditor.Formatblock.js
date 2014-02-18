/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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
Ext.define('Ext.ux.form.HtmlEditor.Formatblock', {
    extend: 'Ext.util.Observable',

	// private
	init: function(cmp){
		this.cmp = cmp;
		this.cmd = 'FormatBlock';
		this.store = new Ext.data.SimpleStore({
			fields: ['tag', 'name'],
			data : [
				['p', 'Paragraph'],
				['div', 'Div'],
				['h1', 'Header 1'],
				['h2', 'Header 2'],
				['h3', 'Header 3'],
				['h4', 'Header 4'],
				['address', 'Address'],
				['blockquote', 'Blockquote'],
				['pre', 'Preformated']
			]
		});
		this.cmp.on('render', this.onRender, this);
		this.cmp.on('initialize', this.onInit, this, {delay:100, single: true});
	},
	// private
	onInit: function(){
		Ext.EventManager.on(this.cmp.getDoc(), {
			'mousedown': this.onEditorEvent,
			'dblclick': this.onEditorEvent,
			'click': this.onEditorEvent,
			'keyup': this.onEditorEvent,
			buffer:100,
			scope: this
		});
	},

	// private
	onRender: function() {
		var tb = this.cmp.getToolbar();
		this.formatSelector = this.createFormatSelector();
		tb.add(this.formatSelector);
	},

	//private
	createFormatSelector: function(){
		var combo = new Ext.form.ComboBox({
			store: this.store,
			displayField:'name',
			valueField:'tag',
			typeAhead: true,
			mode: 'local',
			width: 80,
			triggerAction: 'all',
			emptyText:'Select a format ...',
			valueNotFoundText: 'Select a format ...',
			forceSelection: true,
			editable: false,
			listWidth: 120,
			selectOnFocus:true,
			listeners:{
				scope: this,
				'select': function(combo, record, index){
					tag = record.data.tag;
					this.insertFormatblock(tag);
					this.cmp.getToolbar().focus();
					this.cmp.deferFocus();
					this.formatSelector.reset();
				}
			}
		});
		return combo;
	},

	insertFormatblock : function(val) {
		if (val.indexOf('<') == -1)
			val = '<' + val + '>';
		if (Ext.isGecko)
			val = val.replace(/<(div|blockquote|code|dt|dd|dl|samp)>/gi, '$1');
		this.cmp.relayCmd('FormatBlock', val);
	},

	getSelection : function() {
	  win = this.cmp.getWin();
		return win.getSelection ? win.getSelection() : win.document.selection;
	},

	getRange : function() {
		var win = this.cmp.getWin(), s, r;

		try {
			if (s = this.getSelection())
				r = s.rangeCount > 0 ? s.getRangeAt(0) : (s.createRange ? s.createRange() : win.document.createRange());
		} catch (ex) {	}

		// No range found then create an empty one
		// This can occur when the editor is placed in a hidden container element on Gecko
		// Or on IE when there was an exception
		if (!r)
			r = Exi.isIE ? win.document.body.createTextRange() : win.document.createRange();

		return r;
	},

	getNode: function(){
		var elem, e, r = this.getRange(), s = this.getSelection();
		if (!Ext.isIE) {
			// Range maybe lost after the editor is made visible again
			if (!r)
				return this.cmp.getDoc().dom.getRoot();

			e = r.commonAncestorContainer;
			// Handle selection a image or other control like element such as anchors
			if (!r.collapsed) {
				// If the anchor node is a element instead of a text node then return this element
				if (Ext.isWebKit && s.anchorNode && s.anchorNode.nodeType == 1)
					return s.anchorNode.childNodes[s.anchorOffset];

				if (r.startContainer == r.endContainer) {
					if (r.startOffset - r.endOffset < 2) {
						if (r.startContainer.hasChildNodes())
							e = r.startContainer.childNodes[r.startOffset];
					}
				}
			}
			elem = e.parentNode;
		} else {
			if (r.item)
				elem = r.item(0);
			else
				elem = r.parentElement();
		}
		return Ext.get(elem);
	},

	getBlocklevelElement: function(n){
		if(n){
			if (/^(P|DIV|H[1-6]|ADDRESS|BODY|BLOCKQUOTE|PRE)$/.test(n.dom.nodeName)){
				return n;
			} else {
				return this.getBlocklevelElement(n.findParentNode());
			}
		}
		return null;
	},

	// private
	onEditorEvent: function(){
		var fs = this.formatSelector, r, p;
		p = this.getBlocklevelElement(this.getNode());
		if (p && p.dom && p.dom.nodeName != 'BODY'){
			fs.setValue(p.dom.nodeName.toLowerCase());
		} else {
			fs.reset();
		}
	}

});