/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.utils.generate;


import net.firejack.platform.core.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class LanguageRules {

    private static final Map<String, String> PLURAL_EXCEPTIONS1 = new HashMap<String, String>();

    static {
        PLURAL_EXCEPTIONS1.put("foot", "feet");
        PLURAL_EXCEPTIONS1.put("goose", "gees");
        PLURAL_EXCEPTIONS1.put("mouse", "mice");
        PLURAL_EXCEPTIONS1.put("louse", "lice");
        PLURAL_EXCEPTIONS1.put("ox", "oxen");
        PLURAL_EXCEPTIONS1.put("child", "children");
        PLURAL_EXCEPTIONS1.put("woman", "women");
        PLURAL_EXCEPTIONS1.put("man", "men");
        PLURAL_EXCEPTIONS1.put("tooth", "teeth");
    }

    private static final String[] PLURAL_EXCEPTIONS2 = {
            "calf", "half", "knife", "leaf", "life", "loaf", "self", "sheaf",
            "shelf", "thief", "wife", "wolf", "hoof", "scarf", "wharf"
    };

    private static final String CONSONANT_LETTERS_LC = "bcdfghjklmnpqrstvwxz";

    public static String pluralNoun(String word) {
        String result = null;
        if (StringUtils.isBlank(word)) {
            result = word;
        } else {
            String wordLC = word.toLowerCase();
            for (Map.Entry<String, String> entry : PLURAL_EXCEPTIONS1.entrySet()) {
                if (wordLC.equals(entry.getKey())) {
                    result = entry.getValue();
                    break;
                }
            }
            for (String pluralException : PLURAL_EXCEPTIONS2) {
                if (word.equalsIgnoreCase(pluralException)) {
                    if (wordLC.endsWith("f")) {
                        result = word.substring(0, word.length() - 1) + "ves";
                        break;
                    } else if (wordLC.endsWith("fe")) {
                        result = word.substring(0, word.length() - 2) + "ves";
                        break;
                    }
                }
            }
            int length = wordLC.length();
            if (result == null && length > 2) {
                if (wordLC.endsWith("ce") || wordLC.endsWith("ge") || wordLC.endsWith("se") || wordLC.endsWith("ze")) {
                    result = word + "s";
                } else if (wordLC.endsWith("o") || wordLC.endsWith("x") || wordLC.endsWith("s") ||
                        wordLC.endsWith("sh") || wordLC.endsWith("ch")) {
                    result = word + "es";
                } else if (wordLC.endsWith("y") && CONSONANT_LETTERS_LC.contains(wordLC.substring(length - 2, length - 1))) {
                    result = word.substring(0, length - 1) + "ies";
                }
            }
            if (result == null) {
                result = word + "s";
            }
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println("foot => " + pluralNoun("foot"));
        System.out.println("half => " + pluralNoun("half"));
        System.out.println("face => " + pluralNoun("face"));
        System.out.println("dish => " + pluralNoun("dish"));
        System.out.println("tomato => " + pluralNoun("tomato"));
        System.out.println("baby => " + pluralNoun("baby"));
        System.out.println("book => " + pluralNoun("book"));
    }

}