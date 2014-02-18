package net.firejack.platform.core.utils;

import java.util.ArrayList;

public class LongList extends ArrayList<Long> {

    private static final long serialVersionUID = 3678873194825845898L;

    public LongList(String values) {
        super();
        if (values != null) {
            values = values.replaceAll("\\[", "").replaceAll("]", "");
            if (!values.isEmpty()) {
                for(String value : values.split(",")) {
                    try {
                        add(Long.parseLong(value));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Argument contains invalid long value: " + values);
                    }
                }
            }
        }
    }
}