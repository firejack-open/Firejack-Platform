/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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


Ext.define('OPF.core.component.grid.RatingColumn', {
    extend: 'Ext.grid.column.Column',
    alias: 'widget.opf-rating-column',

    ratingPoints: 10,
    ratingImageUrl: null,
    ratingImageHeight: 32,
    ratingImageWidth: 32,
    ratingImagePadding: {
        top: 0,
        left: 0,
        right: 0,
        bottom: 0
    },

    tpl: new Ext.XTemplate(
        '<div class="rating-column" style="position: relative" data-qtip="{tooltip}">',
            '<div class="rating-points" style="height: {ratingHeight}px; width: {ratingWidth}px; background: url({ratingImageUrl}) repeat-x 0 0;"></div>',
            '<div class="rating-active" style="position: absolute; top: 0; left: 0; height: {ratingHeight}px; width: {ratingActiveWidth}px; background: url({ratingImageUrl}) repeat scroll left bottom transparent;"></div>',
        '</div>'
    ),

    initComponent: function() {
        var me = this;

        me.hasCustomRenderer = true;
        me.callParent(arguments);
    },

    defaultRenderer: function(value, meta, record) {
        var intValue = Math.floor(value);
        var decValue = value - Math.floor(value);

        var pointWidth = this.ratingImageWidth - (this.ratingImagePadding.left + this.ratingImagePadding.right);
        var ratingActiveWidth = Math.round(this.ratingImageWidth * intValue + this.ratingImagePadding.left + pointWidth * decValue);

        var data = {
            ratingHeight: this.ratingImageHeight,
            ratingWidth: this.ratingImageWidth * this.ratingPoints,
            ratingImageUrl: this.ratingImageUrl,
            ratingActiveWidth: ratingActiveWidth,
            tooltip: 'Rating: ' + Math.round(value * 100) / 100
        };
        return this.tpl.apply(data);
    }
});