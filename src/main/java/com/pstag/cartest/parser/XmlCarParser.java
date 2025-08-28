package com.pstag.cartest.parser;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.pstag.cartest.model.Car;
import com.pstag.cartest.model.CarType;
import com.pstag.cartest.parser.dto.CarXml;
import com.pstag.cartest.parser.dto.CarsXml;
import com.pstag.cartest.parser.dto.PriceEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class XmlCarParser implements CarParser {

    private final XmlMapper mapper;

    public XmlCarParser() {
        mapper = new XmlMapper();
        mapper.findAndRegisterModules(); // enables LocalDate, etc.
    }

    @Override
    public List<Car> parse(Path path) throws IOException {
        String xml = Files.readString(path);
        CarsXml raw = mapper.readValue(xml, CarsXml.class);

        List<Car> out = new ArrayList<>();
        if (raw.getCar() == null) return out;

        for (CarXml cx : raw.getCar()) {
            Car c = new Car();
            
            c.setModel(nullSafe(cx.getModel()));
            c.setBrand(trimOrNull(cx.getBrand())); // no brand for XML
            c.setId(null);    // no id for XML

            // type: "Sedan", "SUV", "Truck"
            String t = cx.getType();
            CarType type = CarType.OTHER;
            if (t != null) {
                String norm = t.trim().toUpperCase(Locale.ROOT);
                if (norm.equals("SUV")) type = CarType.SUV;
                else if (norm.equals("SEDAN")) type = CarType.SEDAN;
                else if (norm.equals("TRUCK")) type = CarType.TRUCK;
            }
            c.setType(type);

            if (cx.getPrice() != null) {
                PriceEntry p = cx.getPrice();
                if (p.getCurrency() != null) c.setCurrency(p.getCurrency().trim().toUpperCase(Locale.ROOT));
                c.setPrice(p.getValue());
            }

            // other prices <prices><price currency="EUR">...</price>...</prices>
            Map<String, java.math.BigDecimal> map = new HashMap<>();
            if (cx.getPriceList() != null) {
                for (PriceEntry pe : cx.getPriceList()) {
                    if (pe.getCurrency() != null && pe.getValue() != null) {
                        map.put(pe.getCurrency().trim().toUpperCase(Locale.ROOT), pe.getValue());
                    }
                }
            }
            c.setPrices(map);

            // no releaseDate for XML → set null
            out.add(c);
        }
        return out;
    }
    
    private static String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String nullSafe(String s) { return s == null ? null : s.trim(); }
}
