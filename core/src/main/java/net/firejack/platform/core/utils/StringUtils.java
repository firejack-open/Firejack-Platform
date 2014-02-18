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

import java.util.*;


public class StringUtils extends org.apache.commons.lang.StringUtils {

    public final static String PATH_REPLACER = "-";
    public final static String PATH_SEPARATOR = ".";
    public final static String PATH_SEPARATOR_REGEXP = "\\.";

    /**
     * @param context
     * @param size
     * @param maxWordLength
     * @return
     */
    public static String cuttingWithWordStrictLength(String context, Integer size, Integer maxWordLength) {
        Tuple<String, Boolean> result = cut(context, size, maxWordLength);
        return result.getKey();
    }

    /**
     * @param context
     * @param size
     * @return
     */
    public static String cutting(String context, Integer size) {
        Tuple<String, Boolean> result = cut(context, size, 0);
        return result.getKey();
    }

    /**
     * @param context
     * @param size
     * @return
     */
    public static boolean textWillCut(String context, Integer size) {
        Tuple<String, Boolean> result = cut(context, size, 0);
        return result.getValue();
    }

    /**
     * @param context
     * @param size
     * @return
     */
    public static String cutOneLineStrictLength(String context, Integer size) {
        if (isNotBlank(context)) {
            return context.length() > size ? context.substring(0, size) + "..." : context;
        }
        return null;
    }

    private static Tuple<String, Boolean> cut(String context, Integer size, Integer maxWordLength) {
        context = context.replaceAll("</?[^<>]*?/?>", " ").replaceAll("\\s+", " ");
        context = context.trim();
        String[] words = context.split("\\s+");
        String result = "";
        boolean isCut = false;
        for (String word : words) {
            if (maxWordLength > 0 && word.length() > maxWordLength) {
                while (word.length() >= maxWordLength) {
                    String s = word.substring(0, maxWordLength);
                    if ((result.length() + s.length() + 1) <= size) {
                        result = result + "\n" + s;
                        word = word.substring(maxWordLength);
                    } else {
                        result += "...";
                        isCut = true;
                        break;
                    }
                }
                continue;
            }
            if ((result.length() + word.length() + 1) <= size) {
                result = result + (StringUtils.isNotBlank(result) ? " " : "") + word;
            } else {
                result += "...";
                isCut = true;
                break;
            }
        }
        return new Tuple<String, Boolean>(result, isCut);
    }

    /**
     * @param context
     * @param size
     * @return
     */
    public static String frontCutting(String context, Integer size) {
        int lenght = context.length();
        if (lenght > size) {
            int start = lenght - size;
            context = "..." + context.substring(start, lenght);
        }
        return context;
    }

    /**
     * @param items
     * @return
     */
    public static String[] getNotBlankStrings(String[] items) {
        if (items == null) {
            return null;
        }
        List<String> itemList = new ArrayList<String>();
        for (String item : items) {
            if (StringUtils.isNotBlank(item)) {
                itemList.add(item);
            }
        }
        return itemList.toArray(new String[itemList.size()]);
    }

    /**
     * @param value
     * @param replacement
     * @return
     */
    public static String changeWhiteSpacesWithSymbol(String value, String replacement) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        if (replacement == null) {
            throw new IllegalArgumentException("Could not process value using replacement which is null.");
        }
        StringBuilder sb = new StringBuilder();
        Scanner sc = new Scanner(value);
        sb.append(sc.next());
        while (sc.hasNext()) {
            sb.append(replacement)
                    .append(sc.next());
        }
        return sb.toString();
    }

    /**
     * @param text
     * @param len
     * @return
     */
    public static String[] wrapText(String text, int len) {
        // return empty array for null text
        if (text == null)
            return new String[]{};

        // return text if len is zero or less
        if (len <= 0)
            return new String[]{text};

        // return text if less than length
        if (text.length() <= len)
            return new String[]{text};

        char[] chars = text.toCharArray();
        Vector lines = new Vector();
	    StringBuilder line = new StringBuilder();
	    StringBuilder word = new StringBuilder();

        for (char aChar : chars) {
            word.append(aChar);

            if (aChar == ' ') {
                if ((line.length() + word.length()) > len) {
                    lines.add(line.toString());
                    line.delete(0, line.length());
                }

                line.append(word);
                word.delete(0, word.length());
            }
        }

        // handle any extra chars in current word
        if (word.length() > 0) {
            if ((line.length() + word.length()) > len) {
                lines.add(line.toString());
                line.delete(0, line.length());
            }
            line.append(word);
        }

        // handle extra line
        if (line.length() > 0) {
            lines.add(line.toString());
        }

        String[] ret = new String[lines.size()];
        int c = 0; // counter
        for (Enumeration e = lines.elements(); e.hasMoreElements(); c++) {
            ret[c] = (String) e.nextElement();
        }

        return ret;
    }

    /**
     * @param text
     * @param len
     * @return
     */
    public static String wrapTextInLine(String text, int len) {
        String result = "";
        String[] wrapTexts = wrapText(text, len);
        for (String wrapText : wrapTexts) {
            result += wrapText + "\n";
        }
        return result;
    }

    /**
     * @param path
     * @return
     */
    public static String normalize(String path) {
        return path.replaceAll("[\\s\\p{Punct}]+", PATH_REPLACER).toLowerCase();
    }

    /**
     * @param lookup
     * @return
     */
    public static String getPackageLookup(String lookup) {
        String packageLookup;
        if (isBlank(lookup)) {
            packageLookup = null;
        } else {
            String[] pathEntries = lookup.split(PATH_SEPARATOR_REGEXP);
            if (pathEntries.length >= 3) {
                boolean hasNoBlankParts = true;
                for (int i = 0; i < 3; i++) {
                    if (isBlank(pathEntries[i])) {
                        hasNoBlankParts = false;
                        break;
                    }
                }
                if (hasNoBlankParts) {
                    StringBuilder sb = new StringBuilder(pathEntries[0]);
                    sb.append(PATH_SEPARATOR).append(pathEntries[1])
                            .append(PATH_SEPARATOR).append(pathEntries[2]);
                    packageLookup = sb.toString();
                } else {
                    packageLookup = null;
                }
            } else {
                packageLookup = null;
            }
        }
        return packageLookup;
    }

	/**
	 * @param pattern
	 * @param values
	 *
	 * @return
	 */
	public static String replace(String pattern, Map<String, String> values) {
		for (Map.Entry<String, String> entry : values.entrySet()) {
			pattern = replace(pattern, entry.getKey(), entry.getValue());
		}
		return pattern;
	}

	/**
	 * @param pattern
	 * @param key
	 * @param value
	 *
	 * @return
	 */
	public static String replace(String pattern, String key, String value) {
		value = isNotBlank(value) ? value : "";
		return pattern.replace("{" + key + "}", value);
	}

    public static String splitCamelCase(String s) {
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }

    public static String camelCaseToDotCase(String s) {
        String result = s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                "."
        );
        return result.toLowerCase();
    }

    public static String wrapWith(String s, String value) {
        return value == null || s == null || "".equals(s) ? value : value.startsWith(s) ? value : s + value + s;
    }

}