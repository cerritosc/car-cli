package com.pstag.cartest.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateUtils {
    private DateUtils() {}

    public static LocalDate parseFlexible(String input) {
        if (input == null || input.isBlank()) return null;
        String s = input.trim();
        
        DateTimeFormatter dash = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter commas = DateTimeFormatter.ofPattern("yyyy,dd,MM");
        DateTimeFormatter us = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        
        DateTimeFormatter slash = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        for (DateTimeFormatter f : new DateTimeFormatter[]{dash, commas, slash, us}) {
            try { return LocalDate.parse(s, f); } catch (DateTimeParseException ignored) {}
        }
        
        try { return LocalDate.of(Integer.parseInt(s), 1, 1); } catch (Exception ignored) {}
        
        return null;
    }
    
}