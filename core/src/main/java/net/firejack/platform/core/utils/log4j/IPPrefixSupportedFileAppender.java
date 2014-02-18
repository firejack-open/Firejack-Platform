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

package net.firejack.platform.core.utils.log4j;

import org.apache.log4j.RollingFileAppender;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class IPPrefixSupportedFileAppender extends RollingFileAppender {

    private String datePattern = "'.'yyyy-MM-dd";
    private InetAddress localIPAddress = null;
    private boolean isIPProcessed = false;

    /**
     * @return
     */
    public String getDatePattern() {
        return datePattern;
    }

    /**
     * @param datePattern
     */
    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    @Override
    public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
            throws IOException {
        super.setFile(processFilename(fileName), append, bufferedIO, bufferSize);
    }

    @Override
    public void setFile(String fileName) {
        super.setFile(processFilename(fileName));
    }

    private String processFilename(String fileName) {
        if (!isIPProcessed && (getLocalIPAddress() != null)) {
            File file = new File(fileName);
            String shortName = file.getName();
            fileName = fileName.replace(shortName,
                    getLocalIPAddress().getHostAddress() + "-" + shortName);
            isIPProcessed = true;
        }
        return fileName;
    }

    private InetAddress getLocalIPAddress() {
        if (localIPAddress == null) {
            try {
                localIPAddress = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace(System.err);
            }
        }
        return localIPAddress;
    }
}