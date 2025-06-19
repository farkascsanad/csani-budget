package hu.csani.budget.views.upload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.github.pjfanning.xlsx.StreamingReader;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import hu.csani.budget.data.Budget;
import hu.csani.budget.data.UploadRule;
import hu.csani.budget.services.BudgetService;
import hu.csani.budget.services.UploadRuleService;

@PageTitle("Upload")
@Route("upload")
@Menu(order = 3, icon = LineAwesomeIconUrl.PENCIL_RULER_SOLID)
public class UploadView extends VerticalLayout {

	private List<List<String>> allRows = new ArrayList<>();
	private VerticalLayout centerLayout;
	private Grid<Map<String, String>> resultGrid;

	private List<Budget> processedBudget;

	@Autowired
	UploadRuleService uploadRuleService;

	@Autowired
	BudgetService budgetService;

	private final ComboBox<UploadRule> uploadRuleComboBox = new ComboBox<>("Select Upload Rule");
	private final Button testButton = new Button("Test");
	private final Button saveButton = new Button("Save");

	public UploadView() {
		this.centerLayout = new VerticalLayout();

		saveButton.setEnabled(false);
		setupUpload();
	}

	private void setupUpload() {
		MemoryBuffer buffer = new MemoryBuffer();
		Upload upload = new Upload(buffer);
		upload.setAcceptedFileTypes(".csv", ".xlsx");
		upload.setMaxFiles(1);
		upload.setDropAllowed(true);
		upload.setWidthFull();
		upload.setAutoUpload(true);

		Div uploadInstructions = new Div();
		uploadInstructions.setText("Upload a CSV or XLSX file.");

		upload.addSucceededListener(event -> {
			String fileName = event.getFileName().toLowerCase();
			try {
				allRows.clear();
				if (fileName.endsWith(".csv")) {
					readCSV(buffer.getInputStream());
				} else if (fileName.endsWith(".xlsx")) {
					readXLSX(buffer.getInputStream(), null);
				} else {
					Notification.show("Unsupported file type!", 3000, Notification.Position.MIDDLE);
					return;
				}
				List<Map<String, String>> exampleTable = showTable();

				showUploadRuleChooser(exampleTable);
			} catch (Exception ex) {
				ex.printStackTrace();
				Notification.show("Failed to read file: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
			}
		});

		add(uploadInstructions, upload);
	}

	private List<Map<String, String>> showTable() {
		if (allRows.isEmpty()) {
			Notification.show("File is empty or failed to parse.", 3000, Notification.Position.MIDDLE);
			return null;
		}
		List<String> headers = allRows.get(0);
//		List<List<String>> rows = allRows.subList(1, Math.min(11, allRows.size())); // up to 10 rows
		List<List<String>> rows = allRows;
		// Remove previous grids
		if (resultGrid != null) {
			centerLayout.remove(resultGrid);
		}

		resultGrid = new Grid<>();
		resultGrid.setWidthFull();

		// Set columns dynamically
		for (String header : headers) {
			resultGrid.addColumn(map -> map.getOrDefault(header, "")).setHeader(header).setResizable(true);
		}

		List<Map<String, String>> items = rows.stream().map(row -> {
			Map<String, String> map = new LinkedHashMap<>();
			for (int i = 0; i < headers.size(); i++) {
				map.put(headers.get(i), i < row.size() ? row.get(i) : "");
			}
			return map;
		}).collect(Collectors.toList());

		resultGrid.setItems(items);

		add(resultGrid);

		return items;
	}

	private void showUploadRuleChooser(List<Map<String, String>> exampleTable) {

		// Betöltjük az összes UploadRule-t és beállítjuk a combobox elemeit
		List<UploadRule> all = uploadRuleService.findAll();
		uploadRuleComboBox.setItems(all);

		// Hogyan jelenjen meg az elem (pl. az uploadRuleName)
		uploadRuleComboBox.setItemLabelGenerator(UploadRule::getUploadRuleName);

		HorizontalLayout ruleTesterLayout = new HorizontalLayout(uploadRuleComboBox, testButton, saveButton);

		add(ruleTesterLayout);

		Grid<Budget> grid = new Grid<>(Budget.class, false);
		grid.setAllRowsVisible(true);

//		grid.addColumn(Budget::getBudgetId).setHeader("Budget ID").setSortable(true).setResizable(true);
		grid.addColumn(Budget::getAccountId).setHeader("Account ID").setSortable(true).setResizable(true);
		grid.addColumn(Budget::getBookingDate).setHeader("Booking Date").setSortable(true).setResizable(true);
		grid.addColumn(Budget::getTransactionDate).setHeader("Transaction Date").setSortable(true).setResizable(true);
		grid.addColumn(Budget::getAmount).setHeader("Amount").setSortable(true).setResizable(true);
		grid.addColumn(Budget::getAmountIn).setHeader("Amount In").setSortable(true).setResizable(true);
		grid.addColumn(Budget::getAmountOut).setHeader("Amount Out").setSortable(true).setResizable(true);
		grid.addColumn(Budget::getCurrency).setHeader("Currency").setSortable(true).setResizable(true);
		grid.addColumn(Budget::getDirection).setHeader("Direction").setSortable(true).setResizable(true);
		grid.addColumn(Budget::getOriginalId).setHeader("Original ID").setSortable(true).setResizable(true);
		grid.addColumn(Budget::getOtherPartyName).setHeader("Other Party Name").setSortable(true).setResizable(true);
		grid.addColumn(Budget::getOtherPartyAccountNumber).setHeader("Other Party Account").setSortable(true)
				.setResizable(true);
		grid.addColumn(Budget::getTransactionType).setHeader("Transaction Type").setSortable(true).setResizable(true);
		grid.addColumn(Budget::getNote).setHeader("Note").setSortable(true).setResizable(true);
//		grid.addColumn(Budget::getCategoryRuleId).setHeader("Category Rule ID").setSortable(true).setResizable(true);
//		grid.addColumn(budget -> budget.getCategory() != null ? budget.getCategory().toString() : "")
//				.setHeader("Category").setSortable(true).setResizable(true);
//		grid.addColumn(Budget::getManualCategoryId).setHeader("Manual Category ID").setSortable(true)
//				.setResizable(true);
//		grid.addColumn(Budget::getTransferId).setHeader("Transfer ID").setSortable(true).setResizable(true);

		add(grid);

		testButton.addClickListener(e -> {
			UploadRule selected = uploadRuleComboBox.getValue();
			if (selected != null) {
				testUploadRule(selected, exampleTable, grid);
			} else {
				Notification.show("Please select an Upload Rule first.");
			}
		});
		saveButton.addClickListener(e -> {
			budgetService.saveList(processedBudget);

			saveButton.setEnabled(false);
			allRows.clear();

			Notification.show("Done");
		});

	}

	private void testUploadRule(UploadRule rule, List<Map<String, String>> exampleTable, Grid<Budget> grid) {
		Notification.show("Testing UploadRule: " + rule.getUploadRuleName());

		List<Budget> exampleBudget = new ArrayList<>();

		boolean firstRow = true;

		for (Map<String, String> budget : exampleTable) {

			if (firstRow) {
				// skipheader
				firstRow = false;
				continue;
			}

			Budget row = new Budget();
			
			System.out.println(budget);
			
			if (rule.getDefaultAccount() != null)
				row.setAccountId(rule.getDefaultAccount().getAccountId());

			if (rule.getSourceTransactiongDateColumn() != null && rule.getTransactionDateFormat() != null) {
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern(rule.getTransactionDateFormat());
				LocalDate dt = LocalDate.parse(budget.get(rule.getSourceTransactiongDateColumn()), dtf);
				row.setTransactionDate(dt);
			}

			if (rule.getSourceBookingDateColumn() != null && rule.getBookingDateFormat() != null) {
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern(rule.getBookingDateFormat());
				LocalDate dt = LocalDate.parse(budget.get(rule.getSourceBookingDateColumn()), dtf);
				row.setBookingDate(dt);
			}
			String decimalSeparator = rule.getDecimalSeparator();
			String regexReplacer = "[^\\-0-9" + decimalSeparator + "]";

			if (rule.getAmountSplitted()) {
				String inStr = budget.get(rule.getSourceAmountInColumn()).replaceAll(regexReplacer, "");
				String outStr = budget.get(rule.getSourceAmountOutColumn()).replaceAll(regexReplacer, "");

				if (!decimalSeparator.equals(".")) {
					inStr.replace(decimalSeparator, ".");
					outStr.replace(decimalSeparator, ".");
				}
				BigDecimal amountIn = toBigDecimal(inStr);
				BigDecimal amountOut = toBigDecimal(outStr);
				BigDecimal amount = amountIn.add(amountOut);

				row.setAmount(amount);
				row.setAmountIn(amountIn);
				row.setAmountOut(amountOut);

				if (amount.signum() >= 0) {
					row.setDirection("+");
				} else {
					row.setDirection("-");
				}

			} else {

				String amountStr = budget.get(rule.getSourceAmountColumn()).replaceAll(regexReplacer, "");

				if (!decimalSeparator.equals(".")) {
					amountStr.replace(decimalSeparator, ".");
				}

				BigDecimal amount = toBigDecimal(amountStr);
				BigDecimal amountIn;
				BigDecimal amountOut;

				if (amount.signum() >= 0) {
					amountIn = amount;
					amountOut = BigDecimal.ZERO;
					row.setDirection("+");
				} else {
					amountIn = BigDecimal.ZERO;
					amountOut = amount;
					row.setDirection("-");
				}

				row.setAmount(amount);
				row.setAmountIn(amountIn);
				row.setAmountOut(amountOut);
			}
			// TODO nem HUZF default, hanem account currency default
			if (rule.getSourceCurrencyColumn() != null)
				row.setCurrency(budget.getOrDefault(rule.getSourceCurrencyColumn(), "HUF"));

			if (rule.getSourceOtherPartyNameColumns() != null) {
				String[] columns = rule.getSourceOtherPartyNameColumns().split(";");
				StringBuilder otherParty = new StringBuilder();
				for (String column : columns) {
					String clnColumn = budget.getOrDefault(column, "").trim();
					otherParty.append(clnColumn);
				}
				row.setOtherPartyName(otherParty.toString());
			}

			if (rule.getSourceOtherPartyAccountNumberColumns() != null) {
				String[] columns = rule.getSourceOtherPartyAccountNumberColumns().split(";");
				StringBuilder otherParty = new StringBuilder();
				for (String column : columns) {
					String clnColumn = budget.getOrDefault(column, "").trim();
					otherParty.append(clnColumn);
				}
				row.setOtherPartyAccountNumber(otherParty.toString());
			}

			if (rule.getSourceTransactionTypeColumn() != null) {
				String string = budget.getOrDefault(rule.getSourceTransactionTypeColumn(), "");
				row.setTransactionType(string.trim());
			}
			
			if (rule.getSourceNoteColumns() != null) {
				String[] columns = rule.getSourceNoteColumns().split(";");
				StringBuilder otherParty = new StringBuilder();
				for (String column : columns) {
					String clnColumn = budget.getOrDefault(column, "").trim();
					otherParty.append(clnColumn);
				}
				row.setNote(otherParty.toString());
			}

			exampleBudget.add(row);
		}

		grid.setItems(exampleBudget);
		saveButton.setEnabled(true);
		processedBudget = exampleBudget;

	}

	/*
	 * I do not want null-s its mess up later the functions
	 */
	private static BigDecimal toBigDecimal(String value) {
		return (value == null || value.isEmpty()) ? BigDecimal.ZERO : new BigDecimal(value);
	}

	private void readCSV(InputStream input) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
			String line;
			while ((line = reader.readLine()) != null) {
				List<String> row = parseCSVLine(line);
				allRows.add(row);
			}
		}
	}

	// Simple CSV parser (does not handle quoted fields with commas)
	private List<String> parseCSVLine(String line) {
		return Arrays.asList(line.split("\\s*,\\s*", -1));
	}

	public List<List<String>> readXLSX(InputStream tempFile, String sheetName)
			throws InvalidFormatException, IOException {

		List<List<String>> data = new ArrayList<>();

//		InputStream is = new FileInputStream(tempFile);
//		StreamingReader reader = StreamingReader.builder().rowCacheSize(100) // number																				 of																				 rows																				 to																				 keep																				 in																				 memory
////																				 (defaults
////																				 to
////																				 10)
//				.bufferSize(4096) // buffer size to use when reading InputStream
//				// to file (defaults to 1024)
//				.sheetIndex(0) // index of sheet to use (defaults to 0)
//				.read(is); // InputStream or File for XLSX file (required)

//		The StreamingWorkbook is an autocloseable resource, and it's important that you close it to free the filesystem resource it consumed. With Java 8, you can do this:
		try (Workbook open = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(tempFile);) {
			Sheet sheet = null;
			if (sheetName != null) {
				sheet = open.getSheet(sheetName);
			} else {
				sheet = open.getSheetAt(0);
			}
			// The first row defines how many columns we ready! To avoid any random trash in
			// the rows Wrapper needed because of lamda operator
			var wrapper = new Object() {
				int header = 0;
			};

			DecimalFormat df = new DecimalFormat("0.##########");
			// This force the decimal format to . intread of ,
			df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));

			DataFormatter dataFormatter = new DataFormatter();
			dataFormatter.setUseCachedValuesForFormulaCells(true);
			dataFormatter.setUse4DigitYearsInAllDateFormats(true);

			sheet.rowIterator().forEachRemaining(e -> {

				List<String> stringArrayRow = new LinkedList<>();

				int physicalNumberOfCells = e.getPhysicalNumberOfCells();
				if (wrapper.header == 0)
					wrapper.header = physicalNumberOfCells;

				for (int i = 0; i < wrapper.header; i++) {
					Cell cell = e.getCell(i);
					String cellStringValue = null;
					if (cell != null) {
//						cellStringValue = cell.getStringCellValue();
//						System.out.println("Defaul: " + cellStringValue);
//						String value = dataFormatter.formatCellValue(cell); // from apache poi 5.2.0 on
//						System.out.println("Formatter: " + value);

						try {
							if (DateUtil.isCellDateFormatted(cell)) {
								String formatRawCellContents = dataFormatter
										.formatRawCellContents(cell.getNumericCellValue(), 59, "yyyy-mm-dd");
								cellStringValue = formatRawCellContents;
//								System.out.println(formatRawCellContents);
							}
							// Megpróbáljuk még számként beolvasni!
							if (cellStringValue == null && !cell.getStringCellValue().startsWith("0")) {
								cellStringValue = df.format(cell.getNumericCellValue());
							}
						} catch (NumberFormatException ne) {
							cellStringValue = cell.getStringCellValue();
						}
						// Ha csak egy sima 0-van a cellában, akkor is ez a megoldás.
						if (cellStringValue == null)
							cellStringValue = cell.getStringCellValue();
					}
					stringArrayRow.add(cellStringValue);
				}
				data.add(stringArrayRow);
			});
			allRows = data;
			return data;
		}
	}
}