package com.pstag.cartest.processing;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CarFilter {
    private String brandEquals;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private LocalDate releaseFrom;
    private LocalDate releaseTo;

    public String getBrandEquals() { return brandEquals; }
    public void setBrandEquals(String brandEquals) { this.brandEquals = brandEquals; }

    public BigDecimal getPriceMin() { return priceMin; }
    public void setPriceMin(BigDecimal priceMin) { this.priceMin = priceMin; }

    public BigDecimal getPriceMax() { return priceMax; }
    public void setPriceMax(BigDecimal priceMax) { this.priceMax = priceMax; }

    public LocalDate getReleaseFrom() { return releaseFrom; }
    public void setReleaseFrom(LocalDate releaseFrom) { this.releaseFrom = releaseFrom; }

    public LocalDate getReleaseTo() { return releaseTo; }
    public void setReleaseTo(LocalDate releaseTo) { this.releaseTo = releaseTo; }

    public boolean hasBrand() { return brandEquals != null && !brandEquals.isBlank(); }
    public boolean hasPriceBound() { return priceMin != null || priceMax != null; }
    public boolean hasReleaseBound() { return releaseFrom != null || releaseTo != null; }
}
