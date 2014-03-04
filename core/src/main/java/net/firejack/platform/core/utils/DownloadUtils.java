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
