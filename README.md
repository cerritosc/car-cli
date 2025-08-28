# Car CLI – PST.AG Technical Assessment (Java 17)

Command-line application to **parse and process car data** from **CSV** and **XML** files, apply **filters**, **sorting**, and render in **TABLE**, **XML** or **JSON** formats. It supports **directories as input** (recursive) and an optional **currency-based sorting** mode (SUV→EUR, Sedan→JPY, Truck→USD).

---

## 0) Prerequisites

- **Java 17** installed
  - Check:  
    ```bash
    java -version
    ```
    Should print something like `openjdk version "17.x"` or `java version "17.x"`
- **Maven 3.8+** installed
  - Check:
    ```bash
    mvn -v
    ```

> If you use an IDE (IntelliJ/Eclipse/VS Code), you can still build and run from the **terminal** to avoid IDE-specific issues.

---

## 1) Get the code (two ways)

### A) Clone the repository (recommended)

```bash
git clone https://github.com/cerritosc/car-cli.git
cd car-cli
```

### B) Download ZIP
1. Download the project as ZIP and extract it.  
2. Open a terminal **inside the project folder** (the one that contains `pom.xml`).

> The project structure should include `pom.xml` and `src/...`

---

## 2) Build a standalone (fat) JAR

```bash
mvn clean package
```
This produces an executable JAR at:
```
target/car-cli-1.0.0.jar
```

You can verify the CLI is available:
```bash
java -jar target/car-cli-1.0.0.jar --help
```

Expected (short) output should show usage, options and version.

---

## Quick Start (no Maven required)

- From this repository (main branch):  
  [dist/car-cli-1.0.0.jar](https://github.com/cerritosc/car-cli/releases/tag/v1.0.0)
  

- From GitHub Releases (recommended):  
  https://github.com/cerritosc/car-cli/releases/latest

### 1. Verify Java
Make sure you have **Java 17** installed:

```bash
java -version
```

Expected output should look like:

```
openjdk version "17.x"
```

### 2. Run the CLI JAR
You can run the application directly with:

#### Linux / macOS
```bash
java -jar target/car-cli-1.0.0.jar --help
```

#### Windows (PowerShell or CMD)
```powershell
java -jar "target\car-cli-1.0.0.jar" --help
```

This will display the usage guide with all available options.

### 3. Try an example
Run the CLI against a CSV or XML file:

```bash
# Show cars in a console-friendly table
java -jar target/car-cli-1.0.0.jar data/cars.csv --format TABLE

# Sort by price (highest to lowest)
java -jar target/car-cli-1.0.0.jar data/cars.csv --sort PRICE_DESC --format TABLE

# Enable currency-based sorting (SUV→EUR, Sedan→JPY, Truck→USD)
java -jar target/car-cli-1.0.0.jar data/cars.xml --sort PRICE_DESC --currency-sorting --format TABLE
```

### 4. Output to file
```bash
# Save JSON result to file
java -jar target/car-cli-1.0.0.jar data/cars.csv --format JSON --out result.json
```

---

## 3) Running the CLI

General form:
```bash
java -jar target/car-cli-1.0.0.jar PATHS... [OPTIONS]
```
- `PATHS` can be **individual files** (`.csv` / `.xml`) **or directories** (recursively scanned).
- If a path contains spaces (Windows), **wrap it in quotes**: `"C:\path with spaces\cars"`.

### Common examples
```bash
# List all cars from a CSV in TABLE view
java -jar target/car-cli-1.0.0.jar data/cars.csv --format TABLE

# Mix CSV + XML
java -jar target/car-cli-1.0.0.jar data/cars.csv data/cars.xml --format TABLE

# Pass a directory (recursive) with mixed files
java -jar target/car-cli-1.0.0.jar "C:\Users\You\cars" --format TABLE
```

---

## 4) CLI options (explained)

```
Usage: car-cli [-hV] [--currency-sorting] [--fail-fast] [--format=<format>]
               [--limit=<limit>] [--out=<out>] [--price-max=<priceMax>]
               [--price-min=<priceMin>] [--release-after=<releaseAfterStr>]
               [--release-before=<releaseBeforeStr>] [--sort=<sortBy>]
               [--brand=<brand>] PATHS...
```
- **PATHS**: one or more paths (files `.csv`/`.xml` or directories). Directories are scanned **recursively**.
- `--brand <brand>`: brand to filter (used with price/date filters).
- `--price-min <n>` / `--price-max <n>`: price range (**requires `--brand`**).
- `--release-after <date>` / `--release-before <date>`: release date bounds (**requires `--brand`**).  
  Accepted formats: `yyyy-MM-dd`, `yyyy,dd,MM`, `yyyy/MM/dd`, `MM/dd/yyyy`.
- `--sort {RELEASE_YEAR_DESC, PRICE_DESC}`: sorting key.
- `--format {TABLE, XML, JSON}`: output format (default: `TABLE`).
- `--out <file>`: write output to file instead of console.
- `--currency-sorting`: **optional feature**. With `--sort PRICE_DESC`, SUVs compare in **EUR**, Sedans in **JPY**, Trucks in **USD** (uses `<prices>` if present; otherwise converts from main price).
- `--limit <N>`: limits **TABLE** rows shown (handy for huge datasets).
- `--fail-fast`: stop on first error. Without this flag, the CLI collects errors and continues when possible.

Help/version:
```
-h, --help       Show usage help.
-V, --version    Show version.
```

---

## 5) Data formats

### CSV
- Flexible headers: supports `Brand/brand`, `ReleaseDate/releaseDate`, and more if present (`model`, `type`, `currency`, `price`).
- Missing columns → the corresponding fields in the domain object `Car` are set to `null` (no crash).
- Quotes/BOM tolerated: the parser auto-detects and adapts.
- Dates accepted: `yyyy-MM-dd`, `yyyy,dd,MM`, `yyyy/MM/dd`, `MM/dd/yyyy`.

**CSV examples**

Minimal (brand + date):
```csv
Brand,ReleaseDate
Toyota,01/15/2023
Honda,11/20/2022
```

Full (all columns):
```csv
id,brand,model,type,currency,price,releaseDate
1,Toyota,Corolla,SEDAN,USD,15000,2020-05-01
2,Ford,F-150,TRUCK,USD,32000,2019-10-15
```

> If you use the comma-style date `yyyy,dd,MM`, wrap it in quotes in CSV to avoid column splitting: `"2022,31,01"`

### XML
Supports a **main price** and **an alternate prices map**:
```xml
<cars>
  <car>
    <type>SUV</type>
    <model>RAV4</model>
    <price currency="USD">25000.00</price>
    <prices>
      <price currency="EUR">23000.00</price>
      <price currency="JPY">2800000.00</price>
    </prices>
  </car>
</cars>
```
- `type` accepts `SUV`, `Sedan`/`SEDAN`, `Truck`/`TRUCK` (case-insensitive, mapped to enum).  
- `brand`, `id`, `releaseDate` are optional in XML; they will be `null` if absent.

---

## 6) Step-by-step: Try the required scenarios

### A) Show data (TABLE/JSON/XML)
```bash
# TABLE (console-friendly)
java -jar target/car-cli-1.0.0.jar data/cars.csv --format TABLE

# JSON
java -jar target/car-cli-1.0.0.jar data/cars.csv --format JSON

# XML
java -jar target/car-cli-1.0.0.jar data/cars.xml --format XML
```

### B) Conditional filters (Brand + Price)
```bash
java -jar target/car-cli-1.0.0.jar data/cars.csv --brand Toyota \
  --price-min 10000 --price-max 30000 --sort PRICE_DESC --format TABLE
```
> If you omit `--brand` while using `--price-*`, the CLI returns a **meaningful error**.

### C) Conditional filters (Brand + Release Date)
```bash
# Using dashed dates
java -jar target/car-cli-1.0.0.jar data/cars.csv --brand Toyota \
  --release-after 2020-01-01 --release-before 2023-12-31 \
  --sort RELEASE_YEAR_DESC --format TABLE

# Using US format dates
java -jar target/car-cli-1.0.0.jar data/cars.csv --brand Toyota \
  --release-after 01/01/2020 --release-before 12/31/2023 \
  --sort RELEASE_YEAR_DESC --format TABLE
```

### D) Sorting
```bash
# Release Year (latest → oldest)
java -jar target/car-cli-1.0.0.jar data/cars.csv --sort RELEASE_YEAR_DESC --format TABLE

# Price (highest → lowest)
java -jar target/car-cli-1.0.0.jar data/cars.csv --sort PRICE_DESC --format TABLE
```

### E) Currency-Based Sorting (optional feature)
```bash
# Activates type-targeted currency comparison (SUV→EUR, Sedan→JPY, Truck→USD)
java -jar target/car-cli-1.0.0.jar data/cars.xml --sort PRICE_DESC --currency-sorting --format TABLE
```
- If the dataset has `<prices>` per currency, it uses those.
- Else, it converts from the main price (`currency` + `price`) using internal rates.

### F) Directories (recursive input)
```bash
# Windows (path with spaces)
java -jar target/car-cli-1.0.0.jar "C:\Users\You\cars" --format TABLE

# macOS/Linux
java -jar target/car-cli-1.0.0.jar /Users/you/cars --format TABLE
```

### G) Limit table rows (console-friendly for huge datasets)
```bash
java -jar target/car-cli-1.0.0.jar data/cars.csv --format TABLE --sort PRICE_DESC --limit 20
```

### H) Write output to file
```bash
# JSON to file
java -jar target/car-cli-1.0.0.jar data/cars.csv --sort PRICE_DESC --format JSON --out result.json

# XML to file
java -jar target/car-cli-1.0.0.jar data/cars.xml --format XML --out result.xml
```

---

## 7) Error handling & exit codes

- **Unsupported file type** (not `.csv` or `.xml`) → clear error:  
  `Unsupported file type for <name>. Use .csv or .xml`
- **Missing data** → fields default to `null`, no crash.
- **Invalid dates** → reported clearly; with `--fail-fast` the CLI stops immediately.
- **Unsupported filter combos** → clear guidance (e.g., price/date filters require `--brand`).
- **Per-file errors** → collected and shown at the end (unless `--fail-fast`).

**Exit codes**
- `0` → success (no errors).
- `1` → completed with warnings and/or no matching results.
- `2` → fatal error when `--fail-fast` is enabled (stops on first error).

---

## 8) Troubleshooting

- **“picocli CommandLine import” in IDE**: this is a compile-time concern. At runtime, the shaded JAR already includes dependencies.
  - If building from IDE, ensure Maven dependencies are updated (Eclipse: *Maven → Update Project… (Alt+F5)*, *Project → Clean…*).
- **Windows paths with spaces**: always quote the path.
- **CSV with full-line quotes or BOM**: the parser auto-detects and adapts; still, prefer clean CSV.

---

## 9) Extensibility notes

- Add new filters: extend `CarFilter` and update the checks in `CarProcessor`.
- Add sorting criteria: extend `SortBy` and comparator logic.
- Add new output formats: implement `Formatter` and update the `switch` in `CarCliApp`.
- Add new file types: implement `CarParser` and register it in `CarParserFactory`.

---

## 10) License / Attribution

This project is authored as part of the PST.AG technical assessment. Please adapt as needed for submission (URLs, author, year).
