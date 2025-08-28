package com.pstag.cartest.parser;

import java.nio.file.Path;

public final class CarParserFactory {
    private CarParserFactory() {}

    public static CarParser forFile(Path path) {
        String name = path.getFileName().toString().toLowerCase();
        if (name.endsWith(".csv")) return new CsvCarParser();
        if (name.endsWith(".xml")) return new XmlCarParser();
        throw new IllegalArgumentException("Unsupported file type: " + name + " (expected .csv or .xml)");
    }
}
