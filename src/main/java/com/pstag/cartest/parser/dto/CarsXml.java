package com.pstag.cartest.parser.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "cars")
public class CarsXml {
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<CarXml> car = new ArrayList<>();

    public List<CarXml> getCar() { return car; }
    public void setCar(List<CarXml> car) { this.car = car; }
}
