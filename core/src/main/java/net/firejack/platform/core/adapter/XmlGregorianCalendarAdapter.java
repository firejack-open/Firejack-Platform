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

package net.firejack.platform.core.adapter;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;
import java.util.GregorianCalendar;

public class XmlGregorianCalendarAdapter extends XmlAdapter<Date, XMLGregorianCalendarImpl> {

    @Override
    public Date marshal(XMLGregorianCalendarImpl value) throws Exception {
        return value.toGregorianCalendar().getTime();
    }

    @Override
    public XMLGregorianCalendarImpl unmarshal(Date value) throws Exception {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(value);
        return new XMLGregorianCalendarImpl(gregorianCalendar);
    }

}