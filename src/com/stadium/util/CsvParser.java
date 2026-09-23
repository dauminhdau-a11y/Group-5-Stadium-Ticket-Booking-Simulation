package com.stadium.util;

import java.util.Arrays;
import java.util.List;

public final class CsvParser {
    private CsvParser() { }
    public static List<String> parseLine(String line) {
        return Arrays.asList(line == null ? new String[0] : line.split(",", -1));
    }
}
