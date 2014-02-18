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

/**
 * Utility class for convert string version numbers to integer number and integer number to string version number.
 * For define version number it uses version pattern: major_number.minor_number.build_number
 * <p/>
 * Examples:
 * <p/>
 * '0.0.0' = 0
 * '0.0.1' = 1
 * '0.1.0' = 128
 * '0.1.3' = 131
 * '1.0.0' = 16384
 * '1.2.3' = 16643
 * <p/>
 * 129 = '0.1.1'
 * 16512 = '1.1.0'
 * <p/>
 * '2' = 32768
 * '2.0' = 32768
 * '2.0.0' = 32768
 */

public class VersionUtils {

    /**
     * Maximum number version for converting
     */
    public static final Integer MAX_VERSION_NUMBER = 128;

    /**
     * This method allows to convert string version number to integer number for storing in database.
     * It uses algorithm: integer_number = major * 128 ^ 2 + minor * 128 + build
     *
     * @param version - integer type
     * @return number_version
     */
    public static Integer convertToNumber(String version) {
        Integer number = null;
        if (StringUtils.isNotBlank(version) && version.matches("\\d+(\\.\\d+(\\.\\d+)?)?")) {
            version = version.trim();
            String[] versions = version.split("\\.");
            Integer[] numbers = new Integer[]{0, 0, 0};
            for (int i = 0; i < Math.min(versions.length, 3); i++) {
                numbers[i] = Integer.parseInt(versions[i]);
            }
            number = numbers[0] * MAX_VERSION_NUMBER * MAX_VERSION_NUMBER + numbers[1] * MAX_VERSION_NUMBER + numbers[2];
        }
        return number;
    }

    /**
     * This method allows to convert integer number to string number in format: major.minor.build
     *
     * @param number - string
     * @return number_version
     */
    public static String convertToVersion(Integer number) {
        String version = null;
        if (number != null) {
            Integer majorVersion = number / (MAX_VERSION_NUMBER * MAX_VERSION_NUMBER);
            Integer minorVersion = (number - majorVersion * MAX_VERSION_NUMBER * MAX_VERSION_NUMBER) / MAX_VERSION_NUMBER;
            Integer buildVersion = number - majorVersion * MAX_VERSION_NUMBER * MAX_VERSION_NUMBER - minorVersion * MAX_VERSION_NUMBER;
            version = majorVersion + "." + minorVersion + "." + buildVersion;
        }
        return version;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("0.0.0 = " + VersionUtils.convertToNumber("0.0.0"));
        System.out.println("0.0.1 = " + VersionUtils.convertToNumber("0.0.1"));
        System.out.println("0.0.2 = " + VersionUtils.convertToNumber("0.0.2"));
        System.out.println("0.0.3 = " + VersionUtils.convertToNumber("0.0.3"));
        System.out.println("0.1.0 = " + VersionUtils.convertToNumber("0.1.0"));
        System.out.println("0.1.1 = " + VersionUtils.convertToNumber("0.1.1"));
        System.out.println("0.1.2 = " + VersionUtils.convertToNumber("0.1.2"));
        System.out.println("0.1.3 = " + VersionUtils.convertToNumber("0.1.3"));
        System.out.println("1.0.0 = " + VersionUtils.convertToNumber("1.0.0"));
        System.out.println("1.0.1 = " + VersionUtils.convertToNumber("1.0.1"));
        System.out.println("1.1.0 = " + VersionUtils.convertToNumber("1.1.0"));
        System.out.println("1.2.3 = " + VersionUtils.convertToNumber("1.2.3"));
        System.out.println("1.5.0 = " + VersionUtils.convertToNumber("1.5.0"));
        System.out.println("12.25.33 = " + VersionUtils.convertToNumber("12.25.33"));

        System.out.println("0 = " + VersionUtils.convertToVersion(0));
        System.out.println("1 = " + VersionUtils.convertToVersion(1));
        System.out.println("2 = " + VersionUtils.convertToVersion(2));
        System.out.println("3 = " + VersionUtils.convertToVersion(3));
        System.out.println("128 = " + VersionUtils.convertToVersion(128));
        System.out.println("129 = " + VersionUtils.convertToVersion(129));
        System.out.println("130 = " + VersionUtils.convertToVersion(130));
        System.out.println("131 = " + VersionUtils.convertToVersion(131));
        System.out.println("16384 = " + VersionUtils.convertToVersion(16384));
        System.out.println("16385 = " + VersionUtils.convertToVersion(16385));
        System.out.println("16512 = " + VersionUtils.convertToVersion(16512));
        System.out.println("16643 = " + VersionUtils.convertToVersion(16643));
        System.out.println("199841 = " + VersionUtils.convertToVersion(199841));

        System.out.println("0 = " + VersionUtils.convertToNumber("0"));
        System.out.println("1.0 = " + VersionUtils.convertToNumber("1.0"));
        System.out.println("1.1 = " + VersionUtils.convertToNumber("1.1"));
        System.out.println("1.2 = " + VersionUtils.convertToNumber("1.2"));
        System.out.println("2 = " + VersionUtils.convertToNumber("2"));
        System.out.println("2.0 = " + VersionUtils.convertToNumber("2.0"));
        System.out.println("2.0.0 = " + VersionUtils.convertToNumber("2.0.0"));
        System.out.println("3.2 = " + VersionUtils.convertToNumber("3.2"));
    }

}
