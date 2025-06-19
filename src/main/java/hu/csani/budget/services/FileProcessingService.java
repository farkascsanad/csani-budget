package hu.csani.budget.services;

import com.github.pjfanning.xlsx.StreamingReader;
import hu.csani.budget.data.Budget;
import hu.csani.budget.data.UploadRule;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileProcessingService {

    public List<List<String>> readCSV(InputStream input) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            return reader.lines()
                    .map(this::parseCSVLine)
                    .collect(Collectors.toList());
        }
    }

    private List<String> parseCSVLine(String line) {
        return Arrays.asList(line.split("\\s*,\\s*", -1));
    }

    public List<List<String>> readXLSX(InputStream input, String sheetName) throws IOException {
        try (Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(input)) {

            Sheet sheet = (sheetName != null)
                    ? workbook.getSheet(sheetName)
                    : workbook.getSheetAt(0);

            List<List<String>> data = new ArrayList<>();
            int headerCount = 0;

            DecimalFormat df = new DecimalFormat("0.##########");
            df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
            DataFormatter formatter = new DataFormatter();
            formatter.setUseCachedValuesForFormulaCells(true);
            formatter.setUse4DigitYearsInAllDateFormats(true);

            for (Row row : sheet) {
                if (headerCount == 0) {
                    headerCount = row.getPhysicalNumberOfCells();
                }
                List<String> rowData = new ArrayList<>();
                for (int i = 0; i < headerCount; i++) {
                    Cell cell = row.getCell(i);
                    String value = "";
                    if (cell != null) {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            value = formatter.formatRawCellContents(
                                    cell.getNumericCellValue(), 59, "yyyy-MM-dd");
                        } else if (cell.getCellType() == CellType.NUMERIC) {
                            value = df.format(cell.getNumericCellValue());
                        } else {
                            value = formatter.formatCellValue(cell);
                        }
                    }
                    rowData.add(value);
                }
                data.add(rowData);
            }
            return data;
        }
    }

    public List<Map<String, String>> convertToMap(List<String> headers, List<List<String>> rows) {
        return rows.stream()
                .skip(1)
                .map(row -> {
                    Map<String, String> map = new LinkedHashMap<>();
                    for (int i = 0; i < headers.size(); i++) {
                        map.put(headers.get(i), i < row.size() ? row.get(i) : "");
                    }
                    return map;
                })
                .collect(Collectors.toList());
    }

    public List<Budget> mapToBudgetList(UploadRule rule, List<Map<String, String>> records) {
        List<Budget> budgets = new ArrayList<>();
        boolean first = true;
        for (Map<String, String> record : records) {
            if (first) { first = false; continue; } // skip header row
            Budget b = new Budget();
            if (rule.getDefaultAccount() != null)
                b.setAccountId(rule.getDefaultAccount().getAccountId());

            // Dates
            applyDateField(record, rule.getSourceTransactiongDateColumn(), rule.getTransactionDateFormat(), b::setTransactionDate);
            applyDateField(record, rule.getSourceBookingDateColumn(), rule.getBookingDateFormat(), b::setBookingDate);

            // Amounts
            parseAmounts(record, rule, b);

            // Currency
            if (rule.getSourceCurrencyColumn() != null) {
                String cur = record.getOrDefault(rule.getSourceCurrencyColumn(), "HUF");
                b.setCurrency(cur);
            }

            // Other party, type, note
            b.setOtherPartyName(concatColumns(record, rule.getSourceOtherPartyNameColumns()));
            b.setOtherPartyAccountNumber(concatColumns(record, rule.getSourceOtherPartyAccountNumberColumns()));
            b.setTransactionType(record.getOrDefault(rule.getSourceTransactionTypeColumn(), "").trim());
            b.setNote(concatColumns(record, rule.getSourceNoteColumns()));

            budgets.add(b);
        }
        return budgets;
    }

    private void applyDateField(Map<String, String> record, String col, String fmt, java.util.function.Consumer<LocalDate> setter) {
        if (col != null && fmt != null) {
            String raw = record.get(col);
            LocalDate d = LocalDate.parse(raw, DateTimeFormatter.ofPattern(fmt));
            setter.accept(d);
        }
    }

    private void parseAmounts(Map<String, String> rec, UploadRule rule, Budget b) {
        String sep = rule.getDecimalSeparator();
        String regex = "[^\\-0-9" + sep + "]";
        if (rule.getAmountSplitted()) {
            String inRaw = rec.getOrDefault(rule.getSourceAmountInColumn(), "").replaceAll(regex, "");
            String outRaw = rec.getOrDefault(rule.getSourceAmountOutColumn(), "").replaceAll(regex, "");
            BigDecimal in = toDecimal(inRaw, sep);
            BigDecimal out = toDecimal(outRaw, sep);
            b.setAmountIn(in);
            b.setAmountOut(out);
            b.setAmount(in.add(out));
            b.setDirection(in.add(out).signum() >= 0 ? "+" : "-");
        } else {
            String raw = rec.getOrDefault(rule.getSourceAmountColumn(), "").replaceAll(regex, "");
            BigDecimal amt = toDecimal(raw, sep);
            if (amt.signum() >= 0) {
                b.setAmountIn(amt);
                b.setAmountOut(BigDecimal.ZERO);
                b.setDirection("+");
            } else {
                b.setAmountIn(BigDecimal.ZERO);
                b.setAmountOut(amt);
                b.setDirection("-");
            }
            b.setAmount(amt);
        }
    }

    private BigDecimal toDecimal(String raw, String sep) {
        if (!".".equals(sep)) raw = raw.replace(sep, ".");
        return (raw == null || raw.isEmpty()) ? BigDecimal.ZERO : new BigDecimal(raw);
    }

    private String concatColumns(Map<String, String> rec, String cols) {
        if (cols == null) return null;
        StringBuilder sb = new StringBuilder();
        for (String c : cols.split(";")) {
            sb.append(rec.getOrDefault(c, "").trim());
        }
        return sb.toString();
    }
}

