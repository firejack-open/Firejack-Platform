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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PackageUIDGenerator {

    private static final Pattern FIND_NAME = Pattern.compile("name=\"(.*?)\"");
    private static final Pattern FIND_PATH = Pattern.compile("path=\"(.*?)\"");
    private static final Pattern TAG_SPLITTER = Pattern.compile("^([^<>]*?<)([^/][^<>]*?)(/?>.*?)$", Pattern.MULTILINE + Pattern.CASE_INSENSITIVE);

    /**
     * @param args
     */
    public static void main(String[] args) {
        String packageDir = "d:";
        String packageFilePath = packageDir + File.separator + "package.xml";
        String newPackageFilePath = packageDir + File.separator + "package-new.xml";
        File packageFile = new File(packageFilePath);
        if (packageFile.exists()) {
            BufferedWriter writer = null;
            try {
                String inLine;
                String outLine;
                String prevInLine = null;
                List<String> depthNames = new ArrayList<String>();
                String lastLookup = "";
                writer = new BufferedWriter(new FileWriter(newPackageFilePath));
                BufferedReader reader = new BufferedReader(new FileReader(packageFile));
                while ((inLine = reader.readLine()) != null) {
                    inLine = inLine.replaceAll("\\s*uid=\"[\\w\\-]+\"\\s*", "");
                    outLine = inLine;
                    String name;
                    if (isStartTag(inLine, "package")) {
                        String path = getValue(FIND_PATH, inLine);
                        String[] paths = path.split("\\.");
                        depthNames.add(StringUtils.normalize(paths[0]));
                        depthNames.add(StringUtils.normalize(paths[1]));

                        name = getName(inLine);
                        depthNames.add(StringUtils.normalize(name));
                        String lookup = StringUtils.join(depthNames, ".");
                        outLine = addAttribute(inLine, lookup);
                    } else if (isEndTag(inLine, "package")) {
                        int lastIndex = depthNames.size() - 1;
                        depthNames.remove(lastIndex);
                        depthNames.remove(lastIndex - 1);
                    } else if (isStartTag(inLine, "domain")
                            || isStartTag(inLine, "entity")
                            ) {
                        name = getName(inLine);
                        depthNames.add(StringUtils.normalize(name));
                        String lookup = StringUtils.join(depthNames, ".");
                        outLine = addAttribute(inLine, lookup);
                    } else if (isEndTag(inLine, "domain")
                            || isEndTag(inLine, "entity")
                            ) {
                        int lastIndex = depthNames.size() - 1;
                        depthNames.remove(lastIndex);
                    } else if (isStartTag(inLine, "field")) {
                        name = getName(inLine);
                        depthNames.add(StringUtils.normalize(name));
                        String lookup = StringUtils.join(depthNames, ".");
                        outLine = addAttribute(inLine, lookup);
                        int lastIndex = depthNames.size() - 1;
                        depthNames.remove(lastIndex);
                    } else if (isStartTag(inLine, "relationship")) {
                        prevInLine = inLine;
                    } else if (isStartTag(inLine, "source")) {
                        Pattern refPathRegexp = Pattern.compile("refPath=\"(.*?)\"");
                        String refPath = getValue(refPathRegexp, inLine);
                        String[] refPaths = refPath.split("\\.");
                        List<String> refPathList = new ArrayList<String>();
                        for (String path : refPaths) {
                            refPathList.add(StringUtils.normalize(path));
                        }
                        String path = StringUtils.join(refPathList, ".");
                        name = getName(prevInLine);
                        String lookup = path + "." + StringUtils.normalize(name);
                        outLine = addAttribute(prevInLine, lookup);
                        outLine += "\n" + inLine;
                        prevInLine = null;
                    } else if (isStartTag(inLine, "action")
                            || isStartTag(inLine, "navigation")
                            || isStartTag(inLine, "resource-location")
                            || isStartTag(inLine, "role")
                            || isStartTag(inLine, "directory")
                            || isStartTag(inLine, "resource")
                            || isStartTag(inLine, "folder")
                            || isStartTag(inLine, "config")
                            || isStartTag(inLine, "process")
                            || isStartTag(inLine, "actor")
                            ) {
                        String path = getValue(FIND_PATH, inLine);
                        name = getName(inLine);
                        lastLookup = path + "." + StringUtils.normalize(name);
                        outLine = addAttribute(inLine, lastLookup);
                    } else if (isStartTag(inLine, "permission")) {
                        String path = getValue(FIND_PATH, inLine);
                        name = getName(inLine);
                        lastLookup = path + "." + StringUtils.normalize(name) + ".permission";
                        outLine = addAttribute(inLine, lastLookup);
                    } else if (isStartTag(inLine, "parameter")
                            || isStartTag(inLine, "activity")
                            || isStartTag(inLine, "status")
                            ) {
                        name = getName(inLine);
                        String lookup = lastLookup + "." + StringUtils.normalize(name);
                        outLine = addAttribute(inLine, lookup);
                    }

                    if (prevInLine == null) {
                        writer.write(outLine + "\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static String getName(String line) {
        String name = null;
        Matcher m = FIND_NAME.matcher(line);
        if (m.find()) {
            name = m.group(1);
        }
        return name;
    }

    private static String getValue(Pattern regexp, String line) {
        String name = null;
        Matcher m = regexp.matcher(line);
        if (m.find()) {
            name = m.group(1);
        }
        return name;
    }

    private static boolean isStartTag(String line, String tagName) {
        return line.trim().startsWith("<" + tagName + " ");
    }

    private static boolean isEndTag(String line, String tagName) {
        return line.trim().startsWith("</" + tagName + ">");
    }

    private static String addAttribute(String line, String lookup) {
        String newLine = line;
        Matcher m = TAG_SPLITTER.matcher(line);
        if (m.find()) {
            String hash = SecurityHelper.hash(lookup);
            String uid = hash.substring(0, 8) + "-" + hash.substring(8, 12) + "-" + hash.substring(12, 16) + "-" + hash.substring(16, 20) + "-" + hash.substring(20);
            newLine = m.group(1) + m.group(2)
//                    + " lookup=\"" + lookup + "\""
                    + " uid=\"" + uid + "\""
                    + " " + m.group(3);
        }
        return newLine;
    }

}
