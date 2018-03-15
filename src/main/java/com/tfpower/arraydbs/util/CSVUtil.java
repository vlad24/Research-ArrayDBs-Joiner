package com.tfpower.arraydbs.util;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class CSVUtil {

    public static final String CSV_DELIMITER = ";";

    public static String concat(String leftCsv, String rightCsv) {
        return leftCsv + CSV_DELIMITER + rightCsv;
    }

    public static String asCsvRow(List<String> elements) {
        return elements.stream().collect(joining(CSV_DELIMITER));
    }
}
