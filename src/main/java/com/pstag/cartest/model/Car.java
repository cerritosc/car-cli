package com.pstag.cartest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JacksonXmlRootElement(localName = "car")
public class Car {
    private String id;
    private String brand;
    private String model;
    private CarType type = CarType.OTHER;

    private String currency; // ISO 4217
    private BigDecimal price;

    private Map<String, BigDecimal> prices = new HashMap<>();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    public Car() {}

    public Car(String id, String brand, String model, CarType type, String currency, BigDecimal price, LocalDate releaseDate) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.type = type;
        this.currency = currency;
        this.price = price;
        this.releaseDate = releaseDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public CarType getType() { return type; }
    public void setType(CarType type) { this.type = type; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Map<String, BigDecimal> getPrices() { return prices; }
    public void setPrices(Map<String, BigDecimal> prices) { this.prices = prices == null ? new HashMap<>() : prices; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

    public int getReleaseYear() {
        return releaseDate != null ? releaseDate.getYear() : 0;
    }

    @Override
    public String toString() {
        String rd = (releaseDate == null) ? null : releaseDate.toString();
        return "Car{id=\"" + safe(id) + "\", brand=\"" + safe(brand) + "\", model=\"" + safe(model) +
                "\", type=" + (type == null ? "null" : type.name()) +
                ", currency=\"" + safe(currency) + "\", price=" + (price == null ? "null" : price) +
                ", prices=" + prices +
                ", releaseDate=" + rd + "}";
    }

    private static String safe(String s) { return s == null ? "" : s; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Car)) return false;
        Car car = (Car) o;
        return Objects.equals(id, car.id) &&
                Objects.equals(brand, car.brand) &&
                Objects.equals(model, car.model) &&
                type == car.type &&
                Objects.equals(currency, car.currency) &&
                Objects.equals(price, car.price) &&
                Objects.equals(prices, car.prices) &&
                Objects.equals(releaseDate, car.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, brand, model, type, currency, price, prices, releaseDate);
    }
}
