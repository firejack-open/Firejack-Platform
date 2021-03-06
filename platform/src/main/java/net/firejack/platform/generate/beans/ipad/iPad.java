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

package net.firejack.platform.generate.beans.ipad;


public interface iPad {
    public static final String BASE_ENTITY_CLASS = "GWEntity";
    public static final String ENUM_CLASS = "GWEnum *";

    public static final String GWPROPERTY_TYPE_UNKNOWN = "GWPropertyTypeUnknown";
    public static final String GWPROPERTY_TYPE_ENUM = "GWPropertyTypeEnum";
    public static final String GWPROPERTY_TYPE_TREE = "GWPropertyTypeTree";
    public static final String GWPROPERTY_TYPE_PARENT = "GWPropertyTypeParent";
    public static final String GWPROPERTY_TYPE_IMAGE = "GWPropertyTypeImage";
    public static final String GWPROPERTY_TYPE_TEXT = "GWPropertyTypeText";
    public static final String GWPROPERTY_TYPE_PASSWORD = "GWPropertyTypePassword";
    public static final String GWPROPERTY_TYPE_PHONE_NUMBER = "GWPropertyTypePhoneNumber";
    public static final String GWPROPERTY_TYPE_SSN = "GWPropertyTypeSSN";
    public static final String GWPROPERTY_TYPE_STRING = "GWPropertyTypeString";
    public static final String GWPROPERTY_TYPE_NUMBER = "GWPropertyTypeNumber";
    public static final String GWPROPERTY_TYPE_DATE = "GWPropertyTypeDate";
    public static final String GWPROPERTY_TYPE_TIME = "GWPropertyTypeTime";
    public static final String GWPROPERTY_TYPE_FULL_TIME = "GWPropertyTypeFullTime";
    public static final String GWPROPERTY_TYPE_FLAG = "GWPropertyTypeFlag";
    public static final String GWPROPERTY_TYPE_OBJECT = "GWPropertyTypeObject";
    public static final String GWPROPERTY_TYPE_LIST = "GWPropertyTypeList";

    public static final String GWACTION_TYPE_CREATE = "GWActionTypeCreate";
    public static final String GWACTION_TYPE_UPDATE = "GWActionTypeUpdate";
    public static final String GWACTION_TYPE_DELETE = "GWActionTypeDelete";
    public static final String GWACTION_TYPE_READ = "GWActionTypeRead";
    public static final String GWACTION_TYPE_READ_ALL = "GWActionTypeReadAll";
    public static final String GWACTION_TYPE_SEARCH = "GWActionTypeSearch";
    public static final String GWACTION_TYPE_ADVANCED_SEARCH = "GWActionTypeAdvancedSearch";

    public static final String GWPROPERTY_ATTRIBUTE_AUTOGENERATED = "GWPropertyAttributeAutogenerated";
    public static final String GWPROPERTY_ATTRIBUTE_DEFAULT = "GWPropertyAttributeDefault";


    String getName();

    String getFilePosition();
}
