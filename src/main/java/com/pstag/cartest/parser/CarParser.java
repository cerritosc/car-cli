package com.pstag.cartest.parser;

import com.pstag.cartest.model.Car;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface CarParser {
    List<Car> parse(Path path) throws IOException;
}
