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

Ext.require([
    'OPF.core.component.calendar.CalendarEvent',
    'OPF.core.component.calendar.store.CalendarStore',
    'OPF.core.component.calendar.model.CalendarEventModel'
]);

Ext.define('OPF.core.component.calendar.CalendarPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.calendar-panel',

    layout: {
        type: 'vbox',
        align: 'stretch',
        pack: 'start'
    },

    calendarEvents: [],

    headerHeight: 30,
    hourHeight: 40,

    year: null,
    week: null,

    tpl: new Ext.XTemplate(
        '<h2>{object.name}</h2>',
        '<p>{startTime} - {endTime}</p>'
    ),

    interval: 30, //in minutes

    startTimeRange:'12:00 AM',
    endTimeRange:'11:30 PM',

    startHoursFromTheBeginningOfTheDay: null,
    endHoursFromTheBeginningOfTheDay: null,
    dateOffSet:null,


    store: null,

    initComponent: function() {
        var me = this;
        var d, m;

        this.year = new Date().getFullYear();
        this.week = new Date().getWeek();

        var dateRangeOfWeek = this.getDateRangeOfWeek();
        var days = this.calculateDays(dateRangeOfWeek.startDate);

        var headerColumnDays = [{
            cls: 'cal-cell-time-header',
            html: 'Time',
            width: 100
        }];

        Ext.each(days, function(day, index) {
//            var format = index == 0 ? 'l, F d, Y' : 'l d';
            var dayName = Ext.Date.format(day, 'l');
            var headerColumnDay = {
                cls: 'cal-cell-day-header',
                html: dayName,
                columnWidth: .2
            };
            headerColumnDays.push(headerColumnDay);
        });
        headerColumnDays.push({
            cls: 'cal-last-cell-time-header',
            width: 18
        });

        this.calendarHeader = Ext.create('Ext.container.Container', {
            layout: 'column',
            height: this.headerHeight,
            defaults: {
                xtype: 'container',
                height: this.headerHeight
            },
            items: headerColumnDays
        });
        var timeThresholds = this.calculateStartAndEndThresholds();
        var starTimeThreshold = timeThresholds[0], endTimeThreshold = timeThresholds[1];

        var startDayTime = Ext.Date.clearTime(new Date());
        var schedulerTimeLegendCells = [];
        for (m = starTimeThreshold; m < endTimeThreshold; m++) {
            var schedulerTime = Ext.Date.add(startDayTime, Ext.Date.MINUTE, m * me.interval);
            var timeName = Ext.Date.format(schedulerTime, 'g:i a');
            var schedulerTimeLegendCell = {
                xtype: 'container',
                height: this.hourHeight / 2,
                cls: 'cal-cell-time-legend',
                padding: ((this.hourHeight / 2) - 20) / 2 + ' 0 0 0',
//                padding:'30 0 0 0',
                html: timeName
            };
            schedulerTimeLegendCells.push(schedulerTimeLegendCell);
        }

        this.schedulerDayColumns = [];
        Ext.each(days, function(day, index) {
            var schedulerDayCells = [];
            for (m = starTimeThreshold; m < endTimeThreshold; m++) {
                var schedulerDayCellCls = m % 2 == 1 ? 'cal-cell-day-fullhour' : 'cal-cell-day-halfhour';
                var schedulerDayCell = {
                    xtype: 'container',
                    height: me.hourHeight / 2,
                    cls: schedulerDayCellCls
                };
                schedulerDayCells.push(schedulerDayCell);
            }

            var schedulerDayColumn = Ext.create('Ext.container.Container', {
                columnWidth: .2,
                cls: 'cal-day-column',
                day: day,
                items: schedulerDayCells,
                dayEvents: [],
                starTimeThreshold: starTimeThreshold,
                endTimeThreshold: endTimeThreshold
            });

            me.schedulerDayColumns.push(schedulerDayColumn);
        });

        this.store.on('preload', this.showCalendarSchedulerMask, this);
        this.store.on('load', this.addCalendarEvents, this);

        this.calendarScheduler = Ext.create('Ext.container.Container', {
            xtype: 'container',
            flex: 1,
            autoScroll: true,
            items: [
                {
                    xtype: 'container',
                    layout: 'column',
                    height: this.hourHeight * (this.endHoursFromTheBeginningOfTheDay - this.startHoursFromTheBeginningOfTheDay + 0.5),
                    defaults: {
                        xtype: 'container'
                    },
                    items: [
                        {
                            width: 100,
                            defaults: {
                                xtype: 'container',
                                style: {
                                    position: 'relative'
                                }
                            },
                            items: schedulerTimeLegendCells
                        },
                        this.schedulerDayColumns
                    ]
                }
            ]
        });

        this.items = [
            this.calendarHeader,
            this.calendarScheduler
        ];

        this.weekTitleText = Ext.create('Ext.toolbar.TextItem', {
            cls: 'cal-date-interval'
        });
        this.getWeekTitle(dateRangeOfWeek);

        this.dockedItems = [
            {
                xtype: 'toolbar',
                dock: 'top',
                items: [
                    '->',
                    {
                        xtype: 'button',
                        cls: 'cal-btn cal-prev-btn',
                        text: '<< Prev Week',
                        handler: this.previousWeek,
                        scope: this
                    },
                    this.weekTitleText,
                    {
                        xtype: 'button',
                        cls: 'cal-btn cal-next-btn',
                        text: 'Next Week >>',
                        handler: this.nextWeek,
                        scope: this
                    },
                    '->'
                ]
            }
        ];

        this.callParent(arguments);
    },

    listeners: {
        resize: function(calendar, adjWidth, adjHeight ) {
            Ext.each(this.calendarEvents, function(calendarEvent) {
                calendarEvent.doResize();
            });
        }
    },

    getWeekTitle: function(dateRangeOfWeek) {
        var monday = dateRangeOfWeek.startDate;
        var friday = Ext.Date.clearTime(monday, true);
        friday.setDate(friday.getDate() + 4);
        this.weekTitleText.setText(
            Ext.Date.format(monday, 'F d, Y') +
                ' - ' +
            Ext.Date.format(friday, 'F d, Y'));
    },

    getYear: function() {
        return this.year;
    },

    getWeek: function() {
        return this.week;
    },

    setDate: function(year, week) {
        this.year = year;
        this.week = week;
        this.refreshCalendarEvents();
    },

    getDateRangeOfWeek: function() {
        var date = new Date();
        date.setYear(this.year);
        var numOfDaysPastSinceLastMonday = date.getDay() - 1;
        date.setDate(date.getDate() - numOfDaysPastSinceLastMonday);
        var weekNoToday = date.getWeek();
        var weeksInTheFuture = this.week - weekNoToday;
        var startDate = Ext.Date.clearTime(date, true);
        startDate.setDate(date.getDate() + 7 * weeksInTheFuture);
        var endDate = Ext.Date.clearTime(startDate, true);
        endDate.setDate(startDate.getDate() + 6);
        return {
            startDate: startDate,
            endDate: endDate
        };
    },

    calculateDays: function(startWeekDay) {
        var days = [];
        for (var d = 0; d < 5; d++) {
            var day = Ext.Date.add(startWeekDay, Ext.Date.DAY, d);
            days.push(day);
        }
        return days;
    },

    refreshCalendarEvents: function() {
        var dateRangeOfWeek = this.getDateRangeOfWeek();
        var startWeekDay = Ext.Date.clearTime(dateRangeOfWeek.startDate);
        var days = this.calculateDays(startWeekDay);
        Ext.each(this.schedulerDayColumns, function(schedulerDayColumn, index) {
            schedulerDayColumn.day = days[index];
            Ext.each(schedulerDayColumn.dayEvents, function(dayEvent) {
                schedulerDayColumn.remove(dayEvent);
            });
            schedulerDayColumn.dayEvents = [];
        });
        this.calendarEvents = [];
        this.store.load();
        this.getWeekTitle(dateRangeOfWeek);
    },

    showCalendarSchedulerMask: function() {
        this.calendarSchedulerMask = new Ext.LoadMask(this.calendarScheduler.getEl(), {msg: 'Loading...'});
        this.calendarSchedulerMask.show();
    },

    addCalendarEvents: function() {
        var me = this;
        this.calendarSchedulerMask.hide();
        this.store.sort([
            {
                property : 'startTime',
                direction: 'ASC'
            }
        ]);
        var calendarEventModels = this.store.getRange();
        Ext.each(calendarEventModels, function(calendarEventModel) {
            var startTime =  Ext.Date.parse(calendarEventModel.get('startTime'), 'H:i:s');
            var endTime =  Ext.Date.parse(calendarEventModel.get('endTime'), 'H:i:s');
            if( !me.isValidCalendarEventDate(startTime, endTime) ) return;
            calendarEventModel.set('startTime', startTime);
            calendarEventModel.set('endTime', endTime);
            calendarEventModel.set('date', Ext.Date.parse(calendarEventModel.get('date'), 'Y-m-d'));

            var date = calendarEventModel.get('date');
            var schedulerDayColumn = me.findSchedulerDayColumn(date);
            if (OPF.isNotEmpty(schedulerDayColumn)) {
                var calendarEvent = Ext.create('OPF.core.component.calendar.CalendarEvent', {
                    tpl: me.tpl,
                    hourHeight: me.hourHeight,
                    calendarEventModel: calendarEventModel,
                    dayColumn: schedulerDayColumn,
                    dateOffSet: me.dateOffSet
                });
                schedulerDayColumn.dayEvents.push(calendarEvent);
                me.calendarEvents.push(calendarEvent);
            }
        });
        Ext.each(this.schedulerDayColumns, function(schedulerDayColumn) {
            me.renderCalendarEvents(schedulerDayColumn);
        });
    },

    findSchedulerDayColumn: function(date) {
        var dayColumn = null;
        Ext.each(this.schedulerDayColumns, function(schedulerDayColumn) {
            if (date.getTime() == schedulerDayColumn.day.getTime()) {
                dayColumn = schedulerDayColumn;
            }
        });
        return dayColumn;
    },

    previousWeek: function() {
        var dateRangeOfWeek = this.getDateRangeOfWeek();
        var weekDate = dateRangeOfWeek.startDate;
        weekDate.setDate(weekDate.getDate() - 7);
        this.setDate(weekDate.getFullYear(), weekDate.getWeek());
    },

    nextWeek: function() {
        var dateRangeOfWeek = this.getDateRangeOfWeek();
        var weekDate = dateRangeOfWeek.startDate;
        weekDate.setDate(weekDate.getDate() + 7);
        this.setDate(weekDate.getFullYear(), weekDate.getWeek());
    },

    calculateFullMinutesFromTheBeginningOfTheDay : function(dateObj){
        if( OPF.isNotEmpty(dateObj) ) {
            return (dateObj.getHours() * 60) + dateObj.getMinutes();
        } else {
            return null;
        }
    },

    calculateFullHoursFromTheBeginningOfTheDay : function(dateObj){
        if( OPF.isNotEmpty(dateObj) ) {
            return this.calculateFullMinutesFromTheBeginningOfTheDay(dateObj) / 60;
        } else {
            return null;
        }
    },

    isValidCalendarEventDate: function(startDateObj, endDateObj){
        if( OPF.isEmpty(startDateObj) || OPF.isEmpty(endDateObj) ) return false;
        var startHours =  this.calculateFullHoursFromTheBeginningOfTheDay(startDateObj);
        var endHours = this.calculateFullHoursFromTheBeginningOfTheDay(endDateObj);
        return startHours >= this.startHoursFromTheBeginningOfTheDay && endHours <= this.endHoursFromTheBeginningOfTheDay;
    },

    calculateStartAndEndThresholds:function(){
        var startDate = Ext.Date.parse(this.startTimeRange, "g:i A");
        var endDate = Ext.Date.parse(this.endTimeRange, "g:i A");
        if( OPF.isEmpty(startDate) ) {
            throw new Error("startTime parameter is not valid ");
        } else if( OPF.isEmpty(endDate) ) {
            throw new Error("endTime parameter is not valid ");
        } else if( startDate.getMinutes() != 0 && startDate.getMinutes() != 30 ) {
            throw new Error("startTime minutes should be 00 or 30");
        } else if( endDate.getMinutes() != 0 && endDate.getMinutes() != 30) {
            throw  new Error("endTime minutes should be 00 or 30")
        }
        this.dateOffSet = startDate;
        this.startHoursFromTheBeginningOfTheDay = this.calculateFullHoursFromTheBeginningOfTheDay(startDate);
        this.endHoursFromTheBeginningOfTheDay = this.calculateFullHoursFromTheBeginningOfTheDay(endDate);

        return [(this.startHoursFromTheBeginningOfTheDay * 2), (this.endHoursFromTheBeginningOfTheDay * 2 + 1)];
    },

    renderCalendarEvents: function(schedulerDayColumn) {
        var me = this;

        if (schedulerDayColumn.dayEvents.length > 0) {
            var columnWidth = schedulerDayColumn.getWidth();

            var calendarEventGroups = this.calculateCalendarEventGroup(schedulerDayColumn.dayEvents);
            Ext.each(calendarEventGroups, function(calendarEventGroup){
                var startMinute = me.calculateFullMinutesFromTheBeginningOfTheDay(calendarEventGroup.startTime);
                var endMinute = me.calculateFullMinutesFromTheBeginningOfTheDay(calendarEventGroup.endTime);
                var positionMatrix = [];
                for (var currentMinute = startMinute; currentMinute < endMinute; currentMinute++) {
                    var indexOfMinute = currentMinute - startMinute;
                    positionMatrix[indexOfMinute] = [];
                    Ext.each(calendarEventGroup.calendarEvents, function(calendarEvent, indexOfEvent) {
                        positionMatrix[indexOfMinute][indexOfEvent] = 0;
                        var startEventMinute = me.calculateFullMinutesFromTheBeginningOfTheDay(calendarEvent.getStartTime());
                        var endEventMinute = me.calculateFullMinutesFromTheBeginningOfTheDay(calendarEvent.getEndTime());
                        if (startEventMinute <= currentMinute && currentMinute <= endEventMinute) {
                            if (indexOfEvent > 0) {
                                var maxPrevIndentOfEvent = positionMatrix[indexOfMinute].slice(0, indexOfEvent).max();
                                positionMatrix[indexOfMinute][indexOfEvent] = Math.max(1, calendarEvent.indentOfEvent, maxPrevIndentOfEvent + 1);
                                calendarEvent.indentOfEvent = Math.max(1, positionMatrix[indexOfMinute][indexOfEvent]);
                            } else {
                                positionMatrix[indexOfMinute][indexOfEvent] = 1;
                            }
                        }
                    });
                }

                var maxInterceptEventsPerTime = 1;
                Ext.each(calendarEventGroup.calendarEvents, function(calendarEvent, indexOfEvent) {
                    var startEventMinute = me.calculateFullMinutesFromTheBeginningOfTheDay(calendarEvent.getStartTime());
                    var endEventMinute = me.calculateFullMinutesFromTheBeginningOfTheDay(calendarEvent.getEndTime());
                    var indents = [];
                    for (var currentMinute = startEventMinute; currentMinute < endEventMinute; currentMinute++) {
                        indents[currentMinute - startEventMinute] = positionMatrix[currentMinute - startMinute].slice(indexOfEvent, indexOfEvent + 1);
                    }
                    maxInterceptEventsPerTime = Math.max(maxInterceptEventsPerTime, indents.max());
                    calendarEvent.indentOfEvent = indents.max() - 1;
                });

                schedulerDayColumn.add(calendarEventGroup.calendarEvents);

                Ext.each(calendarEventGroup.calendarEvents, function(calendarEvent, indexOfEvent) {
                    calendarEvent.scaleOfEvent = maxInterceptEventsPerTime;
                    calendarEvent.doResize();
                });
            });
        }
    },

    calculateCalendarEventGroup: function(calendarEvents) {
        var calendarEventGroups = [];
        Ext.each(calendarEvents, function(calendarEvent, m) {
            var foundInterceptEventGroup = null;
            Ext.each(calendarEventGroups, function(calendarEventGroup) {
                var st1 = calendarEventGroup.startTime.getTime();
                var et1 = calendarEventGroup.endTime.getTime();
                var st2 = calendarEvent.getStartTime().getTime();
                var et2 = calendarEvent.getEndTime().getTime();
                if ((st2 < st1 && st1 < et2) || (st1 < st2 && st2 < et1) || st1 == st2) {
                    foundInterceptEventGroup = calendarEventGroup;
                }
            });
            if (foundInterceptEventGroup != null) {
                foundInterceptEventGroup.startTime = new Date(Math.min.apply(null,[foundInterceptEventGroup.startTime, calendarEvent.getStartTime()]));
                foundInterceptEventGroup.endTime = new Date(Math.max.apply(null,[foundInterceptEventGroup.endTime, calendarEvent.getEndTime()]));
                foundInterceptEventGroup.calendarEvents.push(calendarEvent);
            } else {
                var calendarEventGroup = {
                    startTime: calendarEvent.getStartTime(),
                    endTime: calendarEvent.getEndTime(),
                    calendarEvents: [ calendarEvent ]
                };
                calendarEventGroups.push(calendarEventGroup);
            }
        });

        // maybe need to join crossed event groups??? need to check on big count of events

        return calendarEventGroups;
    }

});