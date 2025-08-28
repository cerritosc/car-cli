package com.pstag.cartest.parser.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;
import java.util.List;

public class CarXml {
	
	private String brand;
	
    private String type;
    
    private String model;
    
    private PriceEntry price;

    @JacksonXmlElementWrapper(localName = "prices")
    private List<PriceEntry> priceList = new ArrayList<>();

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public PriceEntry getPrice() { return price; }
    public void setPrice(PriceEntry price) { this.price = price; }
    public List<PriceEntry> getPriceList() { return priceList; }
    public void setPriceList(List<PriceEntry> priceList) { this.priceList = priceList; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
}
