package com.pstag.cartest.cli;

import com.pstag.cartest.format.Formatter;
import com.pstag.cartest.format.JsonFormatter;
import com.pstag.cartest.format.OutputFormat;
import com.pstag.cartest.format.TableFormatter;
import com.pstag.cartest.format.XmlFormatter;
import com.pstag.cartest.model.Car;
import com.pstag.cartest.parser.CarParser;
import com.pstag.cartest.parser.CarParserFactory;
import com.pstag.cartest.processing.CarFilter;
import com.pstag.cartest.processing.CarProcessor;
import com.pstag.cartest.util.CurrencyConverter;
import com.pstag.cartest.util.DateUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

@Command(
        name = "car-cli",
        mixinStandardHelpOptions = true,
        version = "1.0.0",
        description = "Parse and process CAR CSV/XML files (or directories containing them) with filtering, sorting and multiple output formats."
)
public class CarCliApp implements Callable<Integer> {

    @Parameters(arity = "1..*", paramLabel = "PATHS",
            description = "Input paths: files (.csv/.xml) or directories containing them (recursively).")
    private List<Path> inputPaths;

    @Option(names = {"--brand"}, description = "Filter by exact brand (used with price or release date filters)")
    private String brand;

    @Option(names = {"--price-min"}, description = "Minimum price for filtering")
    private BigDecimal priceMin;

    @Option(names = {"--price-max"}, description = "Maximum price for filtering")
    private BigDecimal priceMax;

    @Option(names = {"--release-after"}, description = "Release date >= (yyyy-MM-dd or yyyy,dd,MM)")
    private String releaseAfterStr;

    @Option(names = {"--release-before"}, description = "Release date <= (yyyy-MM-dd or yyyy,dd,MM)")
    private String releaseBeforeStr;

    @Option(names = {"--sort"}, description = "Sort by: ${COMPLETION-CANDIDATES}")
    private CarProcessor.SortBy sortBy;

    @Option(names = {"--format"}, description = "Output format: ${COMPLETION-CANDIDATES}", defaultValue = "TABLE")
    private OutputFormat format;

    @Option(names = {"--out"}, description = "Output file (prints to stdout if omitted)")
    private Path out;

    @Option(names = {"--currency-sorting"}, description = "Enable currency-based sorting by car type (SUV->EUR, SEDAN->JPY, TRUCK->USD)")
    private boolean currencySorting;

    @Option(names = {"--limit"}, description = "Limit the number of rows shown in TABLE output")
    private Integer limit;

    @Option(names = {"--fail-fast"}, description = "Stop on first error")
    private boolean failFast;

    public static void main(String[] args) {
        int exit = new CommandLine(new CarCliApp()).execute(args);
        System.exit(exit);
    }

    @Override
    public Integer call() {
        try {
            // Expand input paths (files or directories) to a list of .csv/.xml files
            List<Path> files = expandPaths(inputPaths);
            if (files.isEmpty()) {
                throw new IOException("No .csv or .xml files found in the provided PATHS.");
            }

            // Parse all files
            List<Car> all = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            for (Path p : files) {
                try {
                    CarParser parser = CarParserFactory.forFile(p);
                    all.addAll(parser.parse(p));
                } catch (Exception ex) {
                    String msg = "Failed to parse " + p + ": " + ex.getMessage();
                    if (failFast) {
                        System.err.println("ERROR: " + msg);
                        return 2;
                    } else {
                        errors.add(msg);
                    }
                }
            }
            if (all.isEmpty() && !errors.isEmpty()) {
                System.err.println("No data parsed. Encountered errors:");
                errors.forEach(e -> System.err.println(" - " + e));
                return 1; // procesado parcial / falló todo
            }

            // Build filter combinations
            CarFilter filter = new CarFilter();
            if (brand != null) filter.setBrandEquals(brand);

            if (priceMin != null || priceMax != null) {
                if (brand == null) throw new IllegalArgumentException("Filtering by price requires --brand.");
                filter.setPriceMin(priceMin);
                filter.setPriceMax(priceMax);
            }

            if (releaseAfterStr != null || releaseBeforeStr != null) {
                if (brand == null) throw new IllegalArgumentException("Filtering by release date requires --brand.");
                LocalDate from = releaseAfterStr == null ? null : DateUtils.parseFlexible(releaseAfterStr);
                LocalDate to = releaseBeforeStr == null ? null : DateUtils.parseFlexible(releaseBeforeStr);
                filter.setReleaseFrom(from);
                filter.setReleaseTo(to);
            }

            // Process (filter + sort)
            List<Car> result = CarProcessor.process(all, filter, sortBy);

            // Optional re-sorting by normalized currency-per-type (if requested and sorting by price)
            if (currencySorting && sortBy == CarProcessor.SortBy.PRICE_DESC) {
                CurrencyConverter cc = new CurrencyConverter();
                result.sort((a, b) -> cc.normalizedPriceForType(b).compareTo(cc.normalizedPriceForType(a)));
            }
            
            if (format == OutputFormat.TABLE && limit != null && limit > 0 && limit < result.size()) {
                result = result.subList(0, limit);
            }
            
            if (result.isEmpty()) {
                String info = "No cars matched your filters.";
                if (out != null) {
                    Files.writeString(out, info + System.lineSeparator());
                } else {
                    System.out.println(info);
                }
                return errors.isEmpty() ? 0 : 1;
            }

            // Format output
            Formatter formatter = switch (format) {
                case TABLE -> new TableFormatter();
                case XML -> new XmlFormatter();
                case JSON -> new JsonFormatter();
            };
            
            String output = formatter.format(result);

            if (out != null) {
                Files.writeString(out, output);
            } else {
                System.out.println(output);
            }
            
            if (!errors.isEmpty()) {
                System.err.println("Completed with warnings (" + errors.size() + "):");
                for (String e : errors) System.err.println(" - " + e);
                return 1; // processed with warnings
            }

            return 0;
        } catch (Exception e) {
            if (failFast) {
                System.err.println("ERROR: " + e.getMessage());
                return 2;
            } else {
                e.printStackTrace();
                return 1;
            }
        }
    }

    /** Expands input PATHS into a list of regular files with .csv or .xml extension (recursive for directories). */
    private static List<Path> expandPaths(List<Path> paths) throws IOException {
        List<Path> result = new ArrayList<>();
        for (Path p : paths) {
            if (Files.isDirectory(p)) {
                try (Stream<Path> stream = Files.walk(p)) {
                    stream.filter(Files::isRegularFile)
                          .filter(CarCliApp::isCarDataFile)
                          .forEach(result::add);
                }
            } else if (Files.isRegularFile(p)) {
                if (isCarDataFile(p)) result.add(p);
            } else {
                throw new IOException("Invalid path: " + p);
            }
        }
        return result;
    }

    private static boolean isCarDataFile(Path p) {
        String name = p.getFileName().toString().toLowerCase();
        return name.endsWith(".csv") || name.endsWith(".xml");
    }
}
