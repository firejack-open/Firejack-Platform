package net.firejack.platform.utils.generate;

import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.validation.NotMatchProcessor;
import org.springframework.web.util.HtmlUtils;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormattingUtils {

    public static final String PREFIX = "_";
    private static final Pattern REFERENCE_OBJECT_PATTERN = Pattern.compile("<img.*?'fieldname':'(.*?)'.*?>", Pattern.CASE_INSENSITIVE);

    /**
     * @param name
     * @return
     */
    public static String classFormatting(String name) {
        String[] strings = name.split("[\\s\\p{Punct}]+");
        StringBuilder builder = new StringBuilder();

        for (String string : strings) {
            builder.append(StringUtils.capitalize(string));
        }

        return builder.toString();
    }

    public static String classFormattingWithPluralEnding(String name) {
        if (StringUtils.isNotBlank(name)) {
            String[] strings = name.split("[\\s\\p{Punct}]+");
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < strings.length; i++) {
                builder.append(StringUtils.capitalize(i == strings.length - 1 ?
                        LanguageRules.pluralNoun(strings[i]) : strings[i]));
            }
            name = builder.toString();
        }
        return name;
    }

    public static String fieldFormattingWithPluralEnding(String name) {
        if (StringUtils.isNotBlank(name)) {
            String[] strings = name.split("[\\s\\p{Punct}]+");
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < strings.length; i++) {
                String token = i == strings.length - 1 ? LanguageRules.pluralNoun(strings[i]) : strings[i];
                builder.append(i == 0 ? StringUtils.uncapitalize(token) : StringUtils.capitalize(token));
            }
            name = builder.toString();
            if (isConst(name) || name.matches("^\\d.*")) {
                name = PREFIX + name;
            }
        }

        return name;
    }

    public static String displayNameFormattingWithPluralEnding(String name) {
        if (StringUtils.isNotBlank(name)) {
            String[] strings = name.split("[\\s\\p{Punct}]+");
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < strings.length; i++) {
                builder.append(StringUtils.capitalize(
                        i == strings.length - 1 ? LanguageRules.pluralNoun(strings[i]) : strings[i] + ' '));
            }
            name = builder.toString();
        }
        return name;
    }

    public static String methodFormatting(String name) {
        Pattern pattern = Pattern.compile("(?:[a-z0-9]+|[A-Z0-9]+)[a-z0-9]*");
        Matcher matcher = pattern.matcher(name);

        boolean first = true;
        name = "";
        while (matcher.find()) {
            if (first) {
                name += matcher.group().toLowerCase();
                first = false;
            } else {
                name += StringUtils.capitalize(matcher.group());
            }
        }

        if (name.matches("^\\d.*")) {
            name = PREFIX + name;
        }

        char one = name.charAt(0);
        char two = name.charAt(1);
        if (Character.isLowerCase(one) && Character.isUpperCase(two))
            return name;
        return StringUtils.capitalize(name);
    }

    /**
     * @param name
     * @return
     */
    public static String fieldFormatting(String name) {
        String[] strings = name.split("[\\s\\p{Punct}]+");
        StringBuilder builder = new StringBuilder();

        boolean first = true;
        for (String string : strings) {
            if (first) {
                builder.append(StringUtils.uncapitalize(string));
                first = false;
            } else {
                builder.append(StringUtils.capitalize(string));
            }
        }

        name = builder.toString();

        if (isConst(name) || name.matches("^\\d.*")) {
            name = PREFIX + name;
        }

        return name;
    }

    public static String fieldModelFormatting(String name) {
        Pattern pattern = Pattern.compile("(?:[a-z0-9]+|[A-Z0-9]+)[a-z0-9]*");
        Matcher matcher = pattern.matcher(name);

        boolean first = true;
        name = "";
        while (matcher.find()) {
            if (first) {
                name += matcher.group().toLowerCase();
                first = false;
            } else {
                name += StringUtils.capitalize(matcher.group());
            }
        }

        if (isConst(name) || name.matches("^\\d.*")) {
            name = PREFIX + name;
        }

        return name;
    }

    public static String wsdlFieldModelFormatting(String name) {
        if (isConst(name) || name.matches("^\\d.*")) {
            name = PREFIX + name;
        }

        return name;
    }

    private static boolean isConst(String name) {
        for (String aConst : NotMatchProcessor.words) {
            if (aConst.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param name
     * @return
     */
    public static String createDiscriminatorValue(String name) {
        return name.toUpperCase().replaceAll("[A, E, I, O, U]", "");
    }

    public static String createTableName(String packagePrefix, String name) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(packagePrefix)) {
            builder.append(packagePrefix).append("_");
        }

        builder.append(name.replaceAll("[\\s\\p{Punct}]+", "_").toLowerCase());
        return builder.toString();
    }

    /**
     * @param type
     * @param domainName
     * @return
     */
    public static String brokerName(String type, String domainName) {
        return classFormatting(type) + domainName;
    }

    public static String escape(String html) {
        if (StringUtils.isNotBlank(html)) {
            html = HtmlUtils.htmlEscape(html);
            html = html.replaceAll("\\p{Zs}", " ").replace("\\", "\\\\").replaceAll("\"", "\\\"").replace("\'", "\\\'");
        }
        return html;
    }

    public static String conversionReferenceString(String html) {
        if (StringUtils.isNotBlank(html)) {
            html = HtmlUtils.htmlUnescape(html);
            Matcher matcher = REFERENCE_OBJECT_PATTERN.matcher(html);
            StringBuffer output = new StringBuffer(html.length());

            while (matcher.find())
                matcher.appendReplacement(output, "{" + fieldModelFormatting(matcher.group(1)) + "}");
            matcher.appendTail(output);

            return escape(output.toString());
        }
        return null;
    }

    public static String generateId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 24).toUpperCase();
    }

}
