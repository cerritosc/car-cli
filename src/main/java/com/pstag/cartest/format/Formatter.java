package com.pstag.cartest.format;

import com.pstag.cartest.model.Car;

import java.util.List;

public interface Formatter {
    String format(List<Car> cars) throws Exception;
}
