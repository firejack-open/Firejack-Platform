package net.firejack.platform.core.utils;

import java.util.ArrayList;

public class StringList extends ArrayList<String> {

    public StringList(String values) {
        super();
        if (values != null) {
            values = removeStartEndSymbol(values.trim(), "[", "]");
            if (!values.isEmpty()) {
                for(String value : values.split(",")) {
                    value = removeStartEndSymbol(value.trim(), "\"", "\"");
                    add(value);
                }
            }
        }
    }

    private String removeStartEndSymbol(String values, String startSymbol, String endSymbol) {
        if(values.startsWith(startSymbol)) {
            values = values.substring(1);
        }

        if(values.endsWith(endSymbol)) {
            values = values.substring(0, values.length() - 1);
        }
        return values;
    }
}