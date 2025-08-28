package com.pstag.cartest.processing;

import com.pstag.cartest.model.Car;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CarProcessor {
    private CarProcessor() {}

    public enum SortBy { RELEASE_YEAR_DESC, PRICE_DESC }

    public static List<Car> process(List<Car> input, CarFilter filter, SortBy sortBy) {
        Stream<Car> s = input.stream();

        if (filter != null) {
            if (filter.hasBrand()) {
                String b = filter.getBrandEquals().toLowerCase(Locale.ROOT);
                s = s.filter(c -> c.getBrand() != null && c.getBrand().toLowerCase(Locale.ROOT).equals(b));
            }
            if (filter.hasPriceBound()) {
                BigDecimal min = filter.getPriceMin();
                BigDecimal max = filter.getPriceMax();
                s = s.filter(c -> c.getPrice() != null
                        && (min == null || c.getPrice().compareTo(min) >= 0)
                        && (max == null || c.getPrice().compareTo(max) <= 0));
            }
            if (filter.hasReleaseBound()) {
                s = s.filter(c -> c.getReleaseDate() != null
                        && (filter.getReleaseFrom() == null || !c.getReleaseDate().isBefore(filter.getReleaseFrom()))
                        && (filter.getReleaseTo() == null || !c.getReleaseDate().isAfter(filter.getReleaseTo())));
            }
        }

        List<Car> out = s.collect(Collectors.toList());

        if (sortBy != null) {
            Comparator<Car> cmp = Comparator.comparing(Car::getId); // default no-op
            switch (sortBy) {
                case RELEASE_YEAR_DESC -> cmp = Comparator.comparingInt(Car::getReleaseYear).reversed();
                case PRICE_DESC -> cmp = Comparator.comparing(c -> Objects.requireNonNullElse(c.getPrice(), BigDecimal.ZERO));
            }
            if (sortBy == SortBy.PRICE_DESC) cmp = cmp.reversed();
            out.sort(cmp);
        }
        return out;
    }
}
