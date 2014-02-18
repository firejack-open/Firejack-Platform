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

package net.firejack.platform.api.registry.model;


import java.util.Date;
import java.util.List;
import java.util.Map;

public enum FieldType {

	UNIQUE_ID(String.class,"NSString *", "VARCHAR(255)", 255),      //    Unique ID,                                                 *
	NUMERIC_ID(Long.class,"NSNumber *", "BIGINT"),                  //    Numeric ID                                                 *
	CODE(String.class,"NSString *", "VARCHAR(16)", 16),             //    Code
	LABEL(String.class,"NSString *", "VARCHAR(128)", 128),          //    Label
	LOOKUP(String.class,"NSString *", "VARCHAR(1024)", 1024),       //    Lookup
	NAME(String.class,"NSString *", "VARCHAR(64)", 64),             //    Name
	DESCRIPTION(String.class,"NSString *", "VARCHAR(4096)", 4096),  //    Description
	PASSWORD(String.class,"NSString *", "VARCHAR(32)", 32),         //    Password
	SECRET_KEY(String.class,"NSString *", "VARCHAR(1024)", 1024),   //    Secret Key
	TINY_TEXT(String.class,"NSString *", "VARCHAR(64)", 64),        //    Tiny Text
	SHORT_TEXT(String.class,"NSString *", "VARCHAR(255)", 255),     //    Short Text
	MEDIUM_TEXT(String.class,"NSString *", "VARCHAR(1024)", 1024),  //    Medium Text
	LONG_TEXT(String.class,"NSString *", "MEDIUMTEXT"),             //    Long Text
	UNLIMITED_TEXT(String.class,"NSString *", "LONGTEXT"),          //    Unlimited Text
	RICH_TEXT(String.class,"NSString *", "LONGTEXT"),               //    Rich Text
	URL(String.class,"NSString *", "VARCHAR(2048)", 2048),          //    URL
//	FILE(String.class, "VARCHAR(1024)", 1024),         //    File
	IMAGE_FILE(String.class,"NSString *", "VARCHAR(1024)", 1024),   //    Image File
//	AUDIO_FILE(String.class, "VARCHAR(1024)", 1024),   //    Audio File
//	VIDEO_FILE(String.class, "VARCHAR(1024)", 1024),   //    Video File
	DATE(java.sql.Date.class,"NSDate *", "DATE"),                 //    Date                                                       *
	TIME(java.sql.Time.class,"NSDate *", "TIME"),                          //    Time                                                       *
	EVENT_TIME(Date.class,"NSDate *", "DATETIME"),                //    Event Time                                                *
	CREATION_TIME(Date.class,"NSDate *", "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"),    //    Creation Time                            *
	UPDATE_TIME(Date.class,"NSDate *", "TIMESTAMP"),                //    Update Time                                              *
	INTEGER_NUMBER(Integer.class,"NSNumber *", "INTEGER"),           //    Integer Number                                            *
	LARGE_NUMBER(Long.class,"NSNumber *", "BIGINT"),                //    Long Number                                                *
	DECIMAL_NUMBER(Double.class,"NSNumber *", "DOUBLE"),             //    Decimal Number
	CURRENCY(Double.class,"NSNumber *", "DECIMAL(19,4)"),                 //    Currency
	PHONE_NUMBER(String.class,"NSString *", "VARCHAR(16)", 16),     //    Phone Number
	SSN(String.class,"NSString *", "VARCHAR(16)", 16),                     //    SSN
	FLAG(Boolean.class,"BOOL ", "BIT"),                         //    Flag
    BLOB(Byte.class,"NSNumber *", "BLOB"),
	OBJECT(Object.class, "NSObject *"),                              //    Entity
	MAP(Map.class,"NSDictionary *"),                                  //    Map
	LIST(List.class,"NSArray *");                                  //    List


	FieldType(Class clazz) {
		this(clazz,null,null,null);
	}

	FieldType(Class clazz, String objectiveC) {
		this(clazz, objectiveC, null, null);
	}

	private FieldType(Class clazz, String objectiveC, String dbtype) {
		this(clazz,objectiveC,dbtype,null);
	}

	FieldType(Class clazz, String objectiveC, String dbtype, Integer length) {
		this.clazz = clazz;
		this.objectiveC = objectiveC;
		this.dbtype = dbtype;
		this.length = length;
	}

	private Class clazz;
	private String dbtype;
	private String objectiveC;
	private Integer length;

	/**
	 * @return
	 */
	public Class getClazz() {
		return clazz;
	}

	/**
	 * @return
	 */
	public String getClassName() {
        if (this.equals(DATE) || this.equals(TIME)) {
            return clazz.getName();
        } else {
            return clazz.getSimpleName();
        }
	}

	/**
	 * @return
	 */
	public String getDbtype() {
		return dbtype;
	}

	public String getObjectiveC() {
		return objectiveC;
	}

	/**
	 * @return
	 */
	public Integer getLength() {
		return length;
	}

	/**
	 * @return
	 */
	public boolean isString() {
		return this.clazz.equals(String.class);
	}

	/**
	 * @return
	 */
	public boolean isInteger() {
		return this.clazz.equals(Integer.class);
	}

	/**
	 * @return
	 */
	public boolean isLong() {
		return this.clazz.equals(Long.class);
	}

	/**
	 * @return
	 */
	public boolean isReal() {
		return this.clazz.equals(Double.class);
	}

	public boolean isNumber() {
		return isInteger() || isLong() || isReal();
	}

	/**
	 * @return
	 */
	public boolean isTimeRelated() {
		return this.clazz.equals(Date.class) || this.clazz.equals(java.sql.Time.class) || this.clazz.equals(java.sql.Date.class);
	}

	/**
	 * @return
	 */
	public boolean isObject() {
		return this.equals(OBJECT);
	}

	/**
	 * @return
	 */
	public boolean isList() {
		return this.equals(LIST);
	}

	/**
	 * @return
	 */
	public boolean isMap() {
		return this.equals(MAP);
	}

	/**
	 * @return
	 */
	public boolean isBoolean() {
		return this.equals(FLAG);
	}


	/**
	 * @return
	 */
	public String getStringOrdinal() {
		return String.valueOf(ordinal());
	}

	/**
	 * @param name
	 *
	 * @return
	 */
	public static FieldType findByName(String name) {
		FieldType value = null;
		for (FieldType e : values()) {
			if (e.name().equals(name)) {
				value = e;
				break;
			}
		}
		return value;
	}

}
