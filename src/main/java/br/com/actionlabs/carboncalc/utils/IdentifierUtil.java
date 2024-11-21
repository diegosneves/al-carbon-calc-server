package br.com.actionlabs.carboncalc.utils;

import java.util.UUID;

public final class IdentifierUtil {

    private static final String UUID_HYPHEN = "-";
    private static final String EMPTY_STRING = "";

    private IdentifierUtil() {}

    public static String unique() {
        return UUID.randomUUID().toString().toLowerCase().replace(UUID_HYPHEN, EMPTY_STRING);
    }

    public static String from(final String value) {
        return value == null ? null : value.toLowerCase();
    }

}
