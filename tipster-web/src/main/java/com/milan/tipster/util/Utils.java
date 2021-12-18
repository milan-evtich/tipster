package com.milan.tipster.util;

import com.milan.tipster.error.exception.TipsterException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.milan.tipster.util.Constants.DIFFERENCE_LIMIT;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class Utils {

    public static final String DEFAULT_LOCAL_DATE_TIME_PATTERN = "dd-MM-yy-HH-mm-ss";
    public static final String GREEK_TONOS = "'";
    public static final String EMPTY_CHAR = "";
    public static final String DEFAULT_LOCAL_DATE_REGEX_PATTERN = "[0-9]{2}-[0-9]{2}-[0-9]{2}-[0-9]{2}-[0-9]{2}";

    public static String limitTextTo253(String text) {
        if (isNotBlank(text)) {
            if (text.length() > 253) {
                return text.substring(0, 253);
            }
        }
        return text;
    }

    public static String limitTextToSize(String text, int size) {
        if (isNotBlank(text)) {
            if (text.length() > size) {
                return text.substring(0, size);
            }
        }
        return text;
    }

    public static void sleep(int minimumTimeInMiliSecs) {
        try {
            Thread.sleep((long)((Math.random() * 10000 + minimumTimeInMiliSecs)) % 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean wordsAreSimillar(String word1, String word2) {
        Integer wordDistance = LevenshteinDistance.getDefaultInstance().apply(word1, word2);
        if (wordDistance == -1) {
            throw new TipsterException("Can't determine simillarity");
        }
        if (wordDistance  >= 0 && wordDistance < DIFFERENCE_LIMIT) {
            return true;
        }
        return false;
    }
    public static Double parseOddFromText(String strongText) {
        if (isNotBlank(strongText)) {
            String[] parts = strongText.split(" ");
            for (String part : parts) {
                if (isNumeric(part)) {
                    part = part.replace(",", ".");
                    if (part.contains(".")) {
                        return Double.valueOf(part);
                    }
                }
            }
        }
        return null;
    }

    public static boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*[\\,\\.]?\\d+");
    }

    public static String encodeUrlForFile(String url) {
        return url.replace("/", "_").replace(":", "");
    }

    public static String trimLastChar(String s) {
        Objects.requireNonNull(s, "String s mush be not null");
        if (s.length() > 0) {
            return s.substring(0, s.length() - 1);
        } else {
            return "";
        }
    }

    public static Map<String, String> greekSymbolMap;
    static {
        greekSymbolMap = new HashMap<>();
        greekSymbolMap.put("ά", "α");
        greekSymbolMap.put("ή", "η");
        greekSymbolMap.put("έ", "ε");
        greekSymbolMap.put("ώ", "ω");
        greekSymbolMap.put("ύ", "υ");
        greekSymbolMap.put("ό", "ο");
        greekSymbolMap.put("ί", "ι");
    }

    public static LocalDate convertStringToLocalDate(String dateStr) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        LocalDate localDate = LocalDate.parse(dateStr);
        return localDate;
    }

    public static LocalDateTime convertStringToLocalDateTime(String dateStr, String pattern) {
        DateTimeFormatter formatter;
        if (isCanonicalDateFormat(dateStr) && isCanonicalDateFormat(pattern)) {
            formatter = DateTimeFormatter.ofPattern(isNotBlank(pattern)
                    ? pattern : DEFAULT_LOCAL_DATE_TIME_PATTERN);
        } else {
            dateStr = dateStr.replace("/", "-").replace(":", "-");
            String[] dateParts = dateStr.split("-");
            String[] patternParts = (isNotBlank(pattern) ? pattern.replace("/", "-").replace(":", "-") : DEFAULT_LOCAL_DATE_TIME_PATTERN).split("-");
            StringBuilder datePatternSB = new StringBuilder();
            int i = 0;
            for (String part : dateParts) {
                if (part.length() == 1) {
                    datePatternSB.append(patternParts[i].substring(1));
                } else {
                    datePatternSB.append(patternParts[i]);
                }
                datePatternSB.append("-");
                i++;
            }
            String customPattern = trimLastChar(datePatternSB.toString());
            formatter = DateTimeFormatter.ofPattern(customPattern);
        }
        return LocalDateTime.parse(dateStr, formatter);
    }

    public static boolean textHasAnyOfWords(String text, String... words) {
        for (String word : words) {
            if (textHasWord(text, word)) {
                return true;
            }
        }
        return false;
    }

    public static boolean textHasWord(String text, String word) {
        if (greekToUpper(removeDot(text)).matches(".*\\b" + word + "\\b.*")) {
            return true;
        } else {
            return false;
        }
    }

    public static String greekToUpper(String text) {
        return removeTonos(text).toUpperCase();
    }

    public static String removeDot(String text) {
        return text.replaceAll("\\.", "");
    }

    public static boolean isCanonicalDateFormat(String s){
        return s.matches(DEFAULT_LOCAL_DATE_REGEX_PATTERN);
    }

    private static String removeTonos(String text) {
        for (Map.Entry<String,String> entry : greekSymbolMap.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue()).replace(GREEK_TONOS, EMPTY_CHAR);
        }
        return text;
    }
}
