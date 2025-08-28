package com.pstag.cartest.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "cars")
public class Cars {
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Car> car = new ArrayList<>();

    public Cars() {}

    public Cars(List<Car> car) { this.car = car; }

    public List<Car> getCar() { return car; }
    public void setCar(List<Car> car) { this.car = car; }
}
