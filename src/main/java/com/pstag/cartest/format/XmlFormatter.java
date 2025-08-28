package com.pstag.cartest.format;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pstag.cartest.model.Cars;
import com.pstag.cartest.model.Car;

import java.util.List;

public class XmlFormatter implements Formatter {
    private final XmlMapper mapper;

    public XmlFormatter() {
        mapper = new XmlMapper();
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public String format(List<Car> cars) throws Exception {
        Cars wrapper = new Cars(cars);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(wrapper);
    }
}