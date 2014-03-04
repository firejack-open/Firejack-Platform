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