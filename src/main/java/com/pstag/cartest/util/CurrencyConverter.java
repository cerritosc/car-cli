package com.pstag.cartest.util;

import com.pstag.cartest.model.Car;
import com.pstag.cartest.model.CarType;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;
import java.util.Objects;

public final class CurrencyConverter {
    
    private final java.util.Map<String, BigDecimal> toUsdRates = new java.util.HashMap<>();
    private final java.util.Map<String, BigDecimal> fromUsdRates = new java.util.HashMap<>();

    public CurrencyConverter() {
        toUsdRates.put("USD", BigDecimal.ONE);
        toUsdRates.put("EUR", new BigDecimal("1.10"));
        toUsdRates.put("JPY", new BigDecimal("0.0067"));
        toUsdRates.put("GBP", new BigDecimal("1.27"));

        fromUsdRates.put("USD", BigDecimal.ONE);
        fromUsdRates.put("EUR", new BigDecimal("0.91"));
        fromUsdRates.put("JPY", new BigDecimal("150"));
        fromUsdRates.put("GBP", new BigDecimal("0.79"));
    }

    public void overrideRate(String code, BigDecimal toUsd, BigDecimal fromUsd) {
        String k = code.toUpperCase();
        toUsdRates.put(k, toUsd);
        fromUsdRates.put(k, fromUsd);
    }

    public BigDecimal convert(BigDecimal amount, String fromCode, String toCode) {
        if (amount == null || fromCode == null || toCode == null) return amount;
        String from = fromCode.toUpperCase();
        String to = toCode.toUpperCase();
        BigDecimal inUsd = amount.multiply(toUsdRates.getOrDefault(from, BigDecimal.ONE), MathContext.DECIMAL64);
        return inUsd.multiply(fromUsdRates.getOrDefault(to, BigDecimal.ONE), MathContext.DECIMAL64);
    }

    public String targetCurrencyForType(CarType type) {
        return switch (type) {
            case SUV -> "EUR";
            case SEDAN -> "JPY";
            case TRUCK -> "USD";
            default -> "USD";
        };
    }

    public BigDecimal normalizedPriceForType(Car car) {
        if (car == null) return BigDecimal.ZERO;
        String target = targetCurrencyForType(car.getType());

        Map<String, BigDecimal> map = car.getPrices();
        if (map != null) {
            BigDecimal direct = map.get(target);
            if (direct != null) return direct;
        }

        if (car.getPrice() != null && car.getCurrency() != null) {
            return convert(car.getPrice(), car.getCurrency(), target);
        }

        if (map != null && !map.isEmpty()) {
            Map.Entry<String, BigDecimal> any = map.entrySet().iterator().next();
            return convert(any.getValue(), any.getKey(), target);
        }

        return BigDecimal.ZERO;
    }
}
