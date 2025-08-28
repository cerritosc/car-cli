package com.pstag.cartest.parser;

import com.pstag.cartest.model.Car;
import com.pstag.cartest.model.CarType;
import com.pstag.cartest.util.DateUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

public class CsvCarParser implements CarParser {

    @Override
    public List<Car> parse(Path path) throws IOException {
        
        try (Reader reader = Files.newBufferedReader(path)) {
            try (CSVParser p = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withTrim()
                    .withIgnoreSurroundingSpaces()
                    .parse(reader)) {

                // Si solo hay 1 encabezado con coma dentro, es el caso de líneas entrecomilladas → reintentar sin comillas
                if (p.getHeaderMap() != null && p.getHeaderMap().size() == 1) {
                    String only = p.getHeaderMap().keySet().iterator().next();
                    if (only != null && only.contains(",")) {
                        return parseNoQuotes(path);
                    }
                }

                Map<String, String> headerMap = buildNormalizedHeaderMap(p.getHeaderMap().keySet());
                return readRecords(p, headerMap);
            }
        }
    }

    private List<Car> parseNoQuotes(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path);
             CSVParser p = CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withTrim()
                     .withIgnoreSurroundingSpaces()
                     .withQuote(null) // desactiva manejo de comillas
                     .parse(reader)) {

            Map<String, String> headerMap = buildNormalizedHeaderMap(p.getHeaderMap().keySet());
            return readRecords(p, headerMap);
        }
    }

    private static Map<String, String> buildNormalizedHeaderMap(Set<String> rawHeaders) {
        Map<String, String> map = new HashMap<>();
        for (String h : rawHeaders) {
            String norm = normalizeHeader(h);
            if (!norm.isEmpty()) {
                map.put(norm, h); // norm -> header real
            }
        }
        return map;
    }

    private static String normalizeHeader(String s) {
        if (s == null) return "";
        String t = s.replace("\uFEFF", "") // removes BOM
                    .trim();
        // removes quotes
        if (t.length() >= 2 && t.startsWith("\"") && t.endsWith("\"")) {
            t = t.substring(1, t.length() - 1);
        }
        return t.trim().toLowerCase(Locale.ROOT);
    }

    private static String getByNorm(CSVRecord r, Map<String, String> headerMap, String... normNames) {
        for (String n : normNames) {
            String key = headerMap.get(n.toLowerCase(Locale.ROOT));
            if (key != null && r.isMapped(key)) {
                try {
                    String val = r.get(key);
                    if (val != null) {
                        val = val.trim();
                        if (val.length() >= 2 && val.startsWith("\"") && val.endsWith("\"")) {
                            val = val.substring(1, val.length() - 1);
                        }
                        return val.trim();
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        }
        return null;
    }

    private List<Car> readRecords(CSVParser p, Map<String, String> headerMap) {
        List<Car> cars = new ArrayList<>();
        for (CSVRecord r : p) {
            Car car = new Car();

            String brand = getByNorm(r, headerMap, "brand");
            car.setBrand(brand);

            String model = getByNorm(r, headerMap, "model");
            car.setModel(model);

            String typeStr = getByNorm(r, headerMap, "type");
            if (typeStr != null && !typeStr.isBlank()) {
                try {
                    car.setType(CarType.valueOf(typeStr.trim().toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException e) {
                    car.setType(CarType.OTHER);
                }
            } else {
                car.setType(CarType.OTHER);
            }

            String currency = getByNorm(r, headerMap, "currency");
            car.setCurrency(currency);

            String priceStr = getByNorm(r, headerMap, "price");
            if (priceStr != null && !priceStr.isBlank()) {
                try { car.setPrice(new BigDecimal(priceStr)); } catch (NumberFormatException ignored) {}
            }

            String dateStr = getByNorm(r, headerMap, "releasedate", "release_date", "release-date", "release date");
            if (dateStr == null) {
                dateStr = getByNorm(r, headerMap, "releasedate");
            }
            car.setReleaseDate(DateUtils.parseFlexible(dateStr));

            cars.add(car);
        }
        return cars;
    }
}