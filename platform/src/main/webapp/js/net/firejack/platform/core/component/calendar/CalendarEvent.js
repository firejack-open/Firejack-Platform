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