package com.ticketbooking.util;

import java.util.ArrayList;
import java.util.List;

public final class CsvParser {
    private CsvParser() { }

    public static List<String> parseLine(String line) {
        List<String> fields = new ArrayList<String>();
        if (line == null) {
            fields.add("");
            return fields;
        }

        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (quoted && current == '"' && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                field.append('"');
                i++;
            } else if (current == '"') {
                quoted = !quoted;
            } else if (current == ',' && !quoted) {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(current);
            }
        }
        if (quoted) {
            throw new IllegalArgumentException("Malformed CSV row: unmatched quote.");
        }
        fields.add(field.toString());
        return fields;
    }

    public static List<String> parseRecords(String content) {
        List<String> records = new ArrayList<String>();
        if (content == null || content.isEmpty()) {
            return records;
        }
        StringBuilder record = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < content.length(); i++) {
            char current = content.charAt(i);
            if (current == '"' && quoted && i + 1 < content.length() && content.charAt(i + 1) == '"') {
                record.append(current).append(content.charAt(++i));
            } else if (current == '"') {
                quoted = !quoted;
                record.append(current);
            } else if ((current == '\n' || current == '\r') && !quoted) {
                if (current == '\r' && i + 1 < content.length() && content.charAt(i + 1) == '\n') {
                    i++;
                }
                if (record.length() > 0) {
                    records.add(record.toString());
                    record.setLength(0);
                }
            } else {
                record.append(current);
            }
        }
        if (quoted) {
            throw new IllegalArgumentException("Malformed CSV data: unmatched quote.");
        }
        if (record.length() > 0) {
            records.add(record.toString());
        }
        return records;
    }

    public static String encode(String... fields) {
        StringBuilder row = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                row.append(',');
            }
            String field = fields[i] == null ? "" : fields[i];
            boolean quote = field.indexOf(',') >= 0 || field.indexOf('"') >= 0
                    || field.indexOf('\n') >= 0 || field.indexOf('\r') >= 0;
            if (quote) {
                row.append('"').append(field.replace("\"", "\"\"")).append('"');
            } else {
                row.append(field);
            }
        }
        return row.toString();
    }
}

