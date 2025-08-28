package com.pstag.cartest.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pstag.cartest.model.Car;

import java.util.List;

public class JsonFormatter implements Formatter {
    private final ObjectMapper mapper;

    public JsonFormatter() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public String format(List<Car> cars) throws Exception {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cars);
    }
}
