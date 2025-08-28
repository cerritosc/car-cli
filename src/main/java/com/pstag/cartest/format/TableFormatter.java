package com.pstag.cartest.format;

import com.pstag.cartest.model.Car;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TableFormatter implements Formatter {
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String format(List<Car> cars) {
        String[] headers = {"ID", "Brand", "Model", "Type", "Currency", "Price", "ReleaseDate"};
        List<String[]> rows = new ArrayList<>();
        rows.add(headers);

        for (Car c : cars) {
            rows.add(new String[]{
                    s(c.getId()),
                    s(c.getBrand()),
                    s(c.getModel()),
                    c.getType() == null ? "" : c.getType().name(),
                    s(c.getCurrency()),
                    c.getPrice() == null ? "" : c.getPrice().toPlainString(),
                    c.getReleaseDate() == null ? "" : DF.format(c.getReleaseDate())
            });
        }

        int[] widths = computeWidths(rows);

        StringBuilder sb = new StringBuilder();
        sb.append(border(widths));

        // header
        sb.append(row(rows.get(0), widths));
        sb.append(border(widths));

        // data
        for (int i = 1; i < rows.size(); i++) {
            sb.append(row(rows.get(i), widths));
        }
        sb.append(border(widths));

        return sb.toString();
    }

    private static int[] computeWidths(List<String[]> rows) {
        int cols = rows.get(0).length;
        int[] widths = new int[cols];
        for (String[] row : rows) {
            for (int i = 0; i < cols; i++) {
                String val = row[i] == null ? "" : row[i];
                if (val.length() > widths[i]) widths[i] = val.length();
            }
        }
        return widths;
    }

    private static String border(int[] widths) {
        StringBuilder sb = new StringBuilder();
        sb.append("+");
        for (int w : widths) {
            sb.append("-".repeat(w + 2)).append("+");
        }
        sb.append("\n");
        return sb.toString();
    }

    private static String row(String[] cells, int[] widths) {
        StringBuilder sb = new StringBuilder();
        sb.append("|");
        for (int i = 0; i < cells.length; i++) {
            String v = cells[i] == null ? "" : cells[i];
            sb.append(" ").append(padRight(v, widths[i])).append(" |");
        }
        sb.append("\n");
        return sb.toString();
    }

    private static String padRight(String s, int w) {
        if (s == null) s = "";
        int spaces = w - s.length();
        if (spaces <= 0) return s;
        return s + " ".repeat(spaces);
    }

    private static String s(String v) { return v == null ? "" : v; }
}
