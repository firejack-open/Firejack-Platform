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

Ext.define('OPF.core.component.calendar.CalendarEvent', {
    extend: 'Ext.container.Container',
    alias: 'widget.calendar-event',

    cls: 'cal-event',

    hourHeight: null,
    dateOffSet:null,
    dayColumn: null,
    calendarEventModel: null,
    scaleOfEvent: 1,
    indentOfEvent: 0,

    initComponent: function() {
        var me = this;

        this.startTime = this.calendarEventModel.get('startTime');
        this.endTime = this.calendarEventModel.get('endTime');
        this.height = ((this.endTime.getTime() - this.startTime.getTime()) / (60 * 60 * 1000)) * this.hourHeight;

        this.data = this.calendarEventModel.data;

        this.callParent(arguments);
    },

    doResize: function() {
        var startDayTime = this.dateOffSet;
        var top = ((this.startTime.getTime() - startDayTime.getTime()) / (60 * 60 * 1000)) * this.hourHeight;
        this.el.setTop(top);
        var width = this.dayColumn.getWidth() / this.scaleOfEvent;
        this.el.setLeft(width * this.indentOfEvent);
        this.setWidth(width);
    },

    getStartTime: function() {
        return this.calendarEventModel.get('startTime');
    },

    getEndTime: function() {
        return this.calendarEventModel.get('endTime');
    }

});