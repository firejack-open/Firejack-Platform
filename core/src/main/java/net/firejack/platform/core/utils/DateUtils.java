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

package net.firejack.platform.core.utils;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Utility class for operations upon the dates
 */
public class DateUtils extends org.apache.commons.lang.time.DateUtils {

    private static final String DATE_FORMAT_STR = "MM/dd/yyyy";
    private static final String TIME_FORMAT_STR = "hh:mm a";
    private static final String DATE_TIME_FORMAT_STR = "MM/dd/yyyy hh:mm a";
    private static final SimpleDateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat mysqlDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


    /**
     * Parses a string value into a date vale
     * @param dateStr - string value
     * @return date
     */
    public static Date parseDate(String dateStr){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STR);
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

	/**
     * Parses a string value into a date vale
     * @param dateStr - string value
     * @return date
     */
    public static Date parseMysqlDate(String dateStr){
        try {
            return mysqlDateTimeFormat.parse(dateStr);
        } catch (ParseException e) {
	        try {
		        return mysqlDateFormat.parse(dateStr);
	        } catch (ParseException e1) {
		        throw new RuntimeException(e);
	        }
        }
    }

    /**
     * Parses a string value unto date and time value
     * @param dateStr - string value of date
     * @param timeStr - string value of time
     * @return date with time
     */
    public static Date parseDateTime(String dateStr, String timeStr) {
        if (StringUtils.isEmpty(timeStr)) {
            return parseDate(dateStr);
        }

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT_STR);
        try {
            return dateTimeFormat.parse(dateStr + ' ' + timeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Increments the date by one day
     * @param date - date to be incremented
     */
    public static void incDateByDay(Date date){
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        date.setTime(cal.getTimeInMillis());
    }

    /**
     * Decrements the date by one hour
     * @param date - date to be decremented
     */
    public static void decDateByHour(Date date){
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.HOUR, -1);
        date.setTime(cal.getTimeInMillis());
    }

    /**
     * Decrements the date by one week
     * @param date - date to be decremented
     * @return decremented date
     */
    public static Date decDateByWeek(Date date){
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.WEEK_OF_MONTH, -1);
        date.setTime(cal.getTimeInMillis());
        return date;
    }

    /**
     * Truncates the date to week level
     * @param date - date to be truncated
     * @return truncated date
     */
    public static Date truncateDateToWeek(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(DateUtils.truncate(date, Calendar.DAY_OF_MONTH));
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        return cal.getTime();
    }

    /**
     * Formats the date as MM/dd/yyyy
     * @param date - date to be formatted
     * @return String representation of the date
     */
    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STR);
        return dateFormat.format(date);
    }

    /**
     * Formats the time as hh:mm a
     * @param date - date to be formatted
     * @return String representation of the time
     */
    public static String formatTime(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT_STR);
        return timeFormat.format(date);
    }

    /**
     * Increments the date to round it on day level
     * @param date - date to be incremented
     * @return incremented date
     */
    public static Date ceilDateToHour(Date date) {
        date = addHours(date, 1);
        date = truncate(date, Calendar.HOUR_OF_DAY);
        return addMinutes(date, -1);
    }
    
    /**
     * Increments the date to round it on day level
     * @param date - date to be incremented
     * @return incremented date
     */
    public static Date ceilDateToDay(Date date) {
        date = addDays(date, 1);
        date = truncate(date, Calendar.DAY_OF_MONTH);
        return addMinutes(date, -1);
    }

    /**
     * Increments the date to round it on week level
     * @param date - date to be incremented
     * @return incremented date
     */
    public static Date ceilDateToWeek(Date date) {
        date = addWeeks(date, 1);
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return addMinutes(cal.getTime(), -1);
    }

    /**
     * Increments the date to round it on month level
     * @param date - date to be incremented
     * @return incremented date
     */
    public static Date ceilDateToMonth(Date date) {
        date = addMonths(date, 1);
        date = truncate(date, Calendar.MONTH);
        return addMinutes(date, -1);
    }

    /**
     * Converts the number of days since the epoch to the date
     * @param days - number of days since the epoch
     * @return date
     */
    public static Date epochDays2Date(int days) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(0);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }

    /**
     * Converts the date into the number of days since the epoch
     * @param date - date to be converted
     * @return number of days since the epoch
     */
    public static int date2EpochDays(Date date) {
        long millis = date.getTime();
        return (int)(millis/86400000);
    }

    /**
     * Converts the date to the number of months since the epoch
     * @param date - date to be converted
     * @return number of months since the epoch
     */
    public static int date2EpochMonths(Date date) {
        Calendar epoch = new GregorianCalendar();
        epoch.setTimeInMillis(0);

        Calendar dateCal = new GregorianCalendar();
        dateCal.setTime(date);

        int years = dateCal.get(Calendar.YEAR) - epoch.get(Calendar.YEAR);
        int months = dateCal.get(Calendar.MONTH);

        return years * 12 + months + 1; // months are zero-based, each year has 12 months always
    }
}
