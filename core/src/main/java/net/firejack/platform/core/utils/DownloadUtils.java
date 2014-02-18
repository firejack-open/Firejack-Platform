/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.core.utils;

import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

public class DownloadUtils {

    private final static Pattern ESCSYMBOLS = Pattern.compile("\"|/|\\\\|:|\\?|\\*|<|>|\\|");
    private final static Pattern ESCSPACES = Pattern.compile("\\s+");

    /**
     * Normalize file name
     * @param name map name
     * @return file name
     */
    public static String normalizeFileName(String name)
    {
        name = ESCSYMBOLS.matcher(name).replaceAll("");
        return ESCSPACES.matcher(name.trim()).replaceAll("-");
    }

    /**
     * Create appropriate Content Disposition for diffent web-browsers to support UTF-8 file name
     * @param fileName file name
     * @param userAgent  user-agent
     * @return Content Disposition
     * @throws java.io.UnsupportedEncodingException Unsupported Encoding Exception
     */
    public static String getContentDisposition(String fileName, String userAgent)
            throws UnsupportedEncodingException
    {

        String contentDisposition = null;
        String newFileName = MimeUtility.encodeText(fileName, "UTF8", "Q");
        if (newFileName != null)
        {
            String agent = userAgent.toLowerCase(); //.toLowerCase();
            int pos = newFileName.indexOf("=?UTF8?Q?");
            if (pos > -1)
            {
                newFileName = newFileName.replaceAll("=[?]UTF8[?]Q[?]", "");
                newFileName = newFileName.replaceAll("\r\n", "");
                newFileName = newFileName.replaceAll("_[?]=", "");
                newFileName = newFileName.replaceAll("[?]=", "");
                newFileName = newFileName.replaceAll("_", "%20");
                newFileName = newFileName.replaceAll(" ", "");
                newFileName = newFileName.replaceAll("=", "%");
                if (agent.indexOf("opera") == -1 && agent.indexOf("msie") != -1)
                {
                    // IE
                    contentDisposition = "attachment; filename=\"" + newFileName + "\"";
                }
                else
                {
                    // Opera
                    // see RFC [2184] for details (http://www.ietf.org/rfc/rfc2184.txt)
                    contentDisposition = "attachment; filename*=UTF8''" + newFileName;
                }
            }
            else
            {
                // Firefox and others
                contentDisposition = "attachment; filename=\"" +
                        MimeUtility.encodeText(fileName, "UTF8", "B") + "\"";
            }
        }
        return contentDisposition;
    }

}
