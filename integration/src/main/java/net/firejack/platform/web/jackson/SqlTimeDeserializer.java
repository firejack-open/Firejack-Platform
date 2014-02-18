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

package net.firejack.platform.web.jackson;

import net.firejack.platform.core.utils.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class SqlTimeDeserializer extends JsonDeserializer<Time> {

    private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    private static final Logger logger = Logger.getLogger(SqlDateDeserializer.class);

    @Override
    public Time deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext) throws IOException, JsonProcessingException {
        String date = jsonparser.getText();
	    if (StringUtils.isBlank(date)) {
		    return null;
	    }
        try {
	        if (StringUtils.isNumeric(date)) {
		        return new Time(Long.parseLong(date));
	        }

	        java.util.Date dt = format.parse(date);
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            cal.setTime(dt);
            return new Time(cal.getTimeInMillis());
        } catch (ParseException e) {
            logger.warn("Field '" + jsonparser.getCurrentName() + "' has wrong format value: '" + date + "'. " + e.getMessage());
            return null;
        }
    }

}

