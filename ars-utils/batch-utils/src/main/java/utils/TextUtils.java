package utils;

import java.util.*;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

public class TextUtils {
    private static final String DEFAULT_WORDS_DELIMITER = " ";
    private static final String DEFAULT_EMPTY_VALUE = "";

    private TextUtils() {}

    public static String createPersonFullName(String surname, String name) {
        return createPersonFullName(surname, name, null);
    }

    public static String createPersonFullName(String surname, String name, String middleName) {
        return joinNotBlank(DEFAULT_WORDS_DELIMITER, surname, name, middleName);
    }

    public static String joinNotBlank(String joiner, String... values) {
        StringJoiner stringJoiner = new StringJoiner(joiner);
        for (String value : values) {
            if (StringUtils.isNotBlank(value)) {
                stringJoiner.add(value);
            }
        }
        return stringJoiner.toString();
    }

    public static String normalizeInnerDelimiter(String value) {
        if (value == null) {
            return DEFAULT_EMPTY_VALUE;
        }
        return value.replaceAll("([\n\r\t]|[\\s]{2,})", DEFAULT_WORDS_DELIMITER);
    }

    public static String buildComparableValue(String value) {
        if (value == null) {
            return null;
        }

        return value.trim().toUpperCase();
    }

    public static List<String> getLines(String text) {
        if (text == null) {
            return Lists.newArrayList();
        } else {
            return Lists.newArrayList(text.split("([\r\n])|([\n])"));
        }
    }

    public static String squash(String value) {
        return removeAllWhitespaces(removePunctuations(value));
    }

    public static boolean isNotBlankWithoutPunctuations(String value) {
        return !isBlankWithoutPunctuations(value);
    }

    public static boolean isBlankWithoutPunctuations(String value) {
        String cleanOfPunctuation = removePunctuations(value);
        return StringUtils.isBlank(cleanOfPunctuation);
    }

    public static String removePunctuations(String value) {
        if (value != null) {
            return value.replaceAll("[-.\\\\/,_\'!?:;=+|`~@#$№%^&*<>\"\\(\\)»«]+", DEFAULT_EMPTY_VALUE);
        }
        return null;
    }

    public static String removeAllWhitespaces(String value) {
        if (value != null) {
            return value.replaceAll("[\\s\r\n\t]+", DEFAULT_EMPTY_VALUE);
        }
        return null;
    }
}
