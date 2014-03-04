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