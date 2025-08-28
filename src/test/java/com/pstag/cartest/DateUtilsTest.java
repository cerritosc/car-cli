package com.pstag.cartest;

import com.pstag.cartest.util.DateUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateUtilsTest {
    @Test
    void parsesDashAndCommaFormats() {
        assertEquals(LocalDate.of(2023, 5, 01), DateUtils.parseFlexible("2023-05-01"));
        assertEquals(LocalDate.of(2023, 1, 31), DateUtils.parseFlexible("2023,31,01"));
    }
}