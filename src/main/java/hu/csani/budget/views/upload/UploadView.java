package hu.csani.budget.views.upload;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.MultiSortPriority;
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
import hu.csani.budget.services.FileProcessingService;
import hu.csani.budget.services.UploadRuleService;

@PageTitle("Upload")
@Route("upload")
@Menu(order =1, icon = LineAwesomeIconUrl.PENCIL_RULER_SOLID)
public class UploadView extends VerticalLayout {

	private List<List<String>> allRows = new ArrayList<>();
//	private VerticalLayout centerLayout;
	private Grid<Map<String, String>> uploadTempDynamicGrid;
	private Grid<Budget> budgetTempGrid;

	private List<Budget> processedBudget;

	@Autowired
	UploadRuleService uploadRuleService;

	@Autowired
	BudgetService budgetService;

	FileProcessingService fileService;

	private final ComboBox<UploadRule> uploadRuleComboBox = new ComboBox<>("Select Upload Rule");
	private final Button applyUploadRule = new Button("Apply upload rule");
	private final Button fetchDatabaseAndCleanDuplicatesUntilLastDay = new Button("FetchDb and clean");
	private final Button saveButton = new Button("Save");

	private Div scrollableDiv = new Div();

	public UploadView(FileProcessingService fileProcessingService, UploadRuleService uploadRuleService) {

		scrollableDiv.setId("scrollable-content");
		scrollableDiv.setHeightFull();
		scrollableDiv.setWidthFull();
		scrollableDiv.getStyle().set("overflow", "auto");
		add(scrollableDiv);

		this.fileService = fileProcessingService;
		this.uploadRuleService = uploadRuleService;

		this.uploadRuleService.setUploadView(this);

//		this.centerLayout = new VerticalLayout();

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
					allRows = fileService.readCSV(buffer.getInputStream());
				} else if (fileName.endsWith(".xlsx")) {
					allRows = fileService.readXLSX(buffer.getInputStream(), null);
				} else {
					Notification.show("Unsupported file type!", 3000, Notification.Position.MIDDLE);
					return;
				}
				List<Map<String, String>> exampleTable = showTable(0);

				showUploadRuleChooser(exampleTable);
			} catch (Exception ex) {
				ex.printStackTrace();
				Notification.show("Failed to read file: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
			}
		});

		scrollableDiv.add(uploadInstructions, upload);
	}

	/*
	 * Upload GRID full dynamic
	 */
	private List<Map<String, String>> showTable(int limitRowCount) {
		if (allRows.isEmpty()) {
			Notification.show("File is empty or failed to parse.", 3000, Notification.Position.MIDDLE);
			return null;
		}
		List<String> headers = allRows.get(0);
		List<List<String>> rows = null;

		if (limitRowCount == 0) {
			rows = allRows;
		} else {
			rows = allRows.subList(1, Math.min(limitRowCount + 1, allRows.size())); // up to 10 rows
		}

//		// Remove previous grids
//		if (uploadTempDynamicGrid != null) {
//			centerLayout.remove(uploadTempDynamicGrid);
//		}

		uploadTempDynamicGrid = new Grid<>();
		uploadTempDynamicGrid.setWidthFull();

		// Set columns dynamically
		for (String header : headers) {
			uploadTempDynamicGrid.addColumn(map -> map.getOrDefault(header, "")).setHeader(header).setResizable(true);
		}

		List<Map<String, String>> items = rows.stream().map(row -> {
			Map<String, String> map = new LinkedHashMap<>();
			for (int i = 0; i < headers.size(); i++) {
				map.put(headers.get(i), i < row.size() ? row.get(i) : "");
			}
			return map;
		}).collect(Collectors.toList());

		uploadTempDynamicGrid.setItems(items);

		scrollableDiv.add(uploadTempDynamicGrid);

		return items;
	}

	private void showUploadRuleChooser(List<Map<String, String>> exampleTable) {

		// Betöltjük az összes UploadRule-t és beállítjuk a combobox elemeit
		List<UploadRule> all = uploadRuleService.findAll();
		uploadRuleComboBox.setItems(all);

		// Hogyan jelenjen meg az elem (pl. az uploadRuleName)
		uploadRuleComboBox.setItemLabelGenerator(UploadRule::getUploadRuleName);

		HorizontalLayout ruleTesterLayout = new HorizontalLayout(uploadRuleComboBox, applyUploadRule,
				fetchDatabaseAndCleanDuplicatesUntilLastDay, saveButton);

		scrollableDiv.add(ruleTesterLayout);

		budgetTempGrid = new Grid<>(Budget.class, false);

		budgetTempGrid.setMultiSort(true, MultiSortPriority.APPEND);
//		budgetTempGrid.addThemeVariants(GridVariant.LUMO_COMPACT);

//		budgetTempGrid.setAllRowsVisible(true);

		budgetTempGrid.addColumn(Budget::getBudgetId).setHeader("Budget ID").setSortable(true).setResizable(true);
		budgetTempGrid.addComponentColumn(budget -> {
			Button deleteButton = new Button("Delete", event -> {
				int ridx = 0;
				for (int i = 0; i < processedBudget.size(); i++) {
					if (processedBudget.get(i).getContentMd5().equals(budget.getContentMd5())) {
						ridx = i;
						break;
					}
				}
				processedBudget.remove(ridx);
				budgetTempGrid.setItems(processedBudget); // Refresh the grid
			});
			deleteButton.getElement().getThemeList().add("error"); // Optional red styling
			deleteButton.setEnabled(budget.getBudgetId() == null ? true : false);
			return deleteButton;
		}).setHeader("Actions").setAutoWidth(true).setResizable(true);
		budgetTempGrid.addColumn(budget -> budget.getAccount().getAccountName()).setHeader("Account ID").setSortable(true).setResizable(true);
		budgetTempGrid.addColumn(Budget::getBookingDate).setHeader("Booking Date").setSortable(true).setResizable(true);
		budgetTempGrid.addColumn(Budget::getTransactionDate).setHeader("Transaction Date").setSortable(true)
				.setResizable(true);
		budgetTempGrid.addColumn(Budget::getAmount).setHeader("Amount").setSortable(true).setResizable(true);
		budgetTempGrid.addColumn(Budget::getAmountIn).setHeader("Amount In").setSortable(true).setResizable(true);
		budgetTempGrid.addColumn(Budget::getAmountOut).setHeader("Amount Out").setSortable(true).setResizable(true);
		budgetTempGrid.addColumn(Budget::getCurrency).setHeader("Currency").setSortable(true).setResizable(true);
		budgetTempGrid.addColumn(Budget::getDirection).setHeader("Direction").setSortable(true).setResizable(true);
		budgetTempGrid.addColumn(Budget::getOriginalId).setHeader("Original ID").setSortable(true).setResizable(true);
		budgetTempGrid.addColumn(Budget::getOtherPartyName).setHeader("Other Party Name").setSortable(true)
				.setResizable(true);
		budgetTempGrid.addColumn(Budget::getOtherPartyAccountNumber).setHeader("Other Party Account").setSortable(true)
				.setResizable(true);
		budgetTempGrid.addColumn(Budget::getTransactionType).setHeader("Transaction Type").setSortable(true)
				.setResizable(true);
		budgetTempGrid.addColumn(Budget::getNote).setHeader("Note").setSortable(true).setResizable(true);
//		grid.addColumn(Budget::getCategoryRuleId).setHeader("Category Rule ID").setSortable(true).setResizable(true);
//		grid.addColumn(budget -> budget.getCategory() != null ? budget.getCategory().toString() : "")
//				.setHeader("Category").setSortable(true).setResizable(true);
//		grid.addColumn(Budget::getManualCategoryId).setHeader("Manual Category ID").setSortable(true)
//				.setResizable(true);
//		grid.addColumn(Budget::getTransferId).setHeader("Transfer ID").setSortable(true).setResizable(true);

		scrollableDiv.add(budgetTempGrid);

		applyUploadRule.addClickListener(e -> {
			UploadRule selected = uploadRuleComboBox.getValue();
			if (selected != null) {
				List<Budget> testUploadRule = uploadRuleService.testUploadRule(selected, exampleTable);
				setBudgetTable(testUploadRule);
				scrollToBottom();
				mergeDbWithUpload();
			} else {
				Notification.show("Please select an Upload Rule first.");
			}
		});
		fetchDatabaseAndCleanDuplicatesUntilLastDay.addClickListener(e -> {
			// only for test
		});

		saveButton.addClickListener(e -> {

			List<Budget> onlyNew = processedBudget.stream().filter(b -> b.getBudgetId() == null).toList();

			budgetService.saveList(onlyNew);

			saveButton.setEnabled(false);
			allRows.clear();
			processedBudget = new ArrayList<>();

			Notification.show("Done");
		});

		scrollToBottom();

	}

	private void mergeDbWithUpload() {
		List<Budget> databaseLatestRecords = uploadRuleService.getLastDaysFromDatabase(budgetTempGrid);

		LocalDate currentDatabaseLastDateMinus1 = databaseLatestRecords.get(databaseLatestRecords.size() - 1)
				.getTransactionDate().minusDays(1);

		// A mostani feltöltésből kiszedünk mindent ami az adatbázisban benne van, egy
		// kis metszetett benne hagyva
		processedBudget = processedBudget.stream()
				.filter(b -> b.getTransactionDate().compareTo(currentDatabaseLastDateMinus1) > 0)
				.collect(Collectors.toList());
		// Megjelenítésnek oda adjuk az utolsó pár napot a db-ből, hogy a tört napot
		// jobban lehessen látni
		databaseLatestRecords = databaseLatestRecords.stream()
				.filter(b -> b.getTransactionDate().compareTo(currentDatabaseLastDateMinus1) > 0)
				.collect(Collectors.toList());

		processedBudget.addAll(databaseLatestRecords);
		Collections.sort(processedBudget);
		budgetTempGrid.setItems(processedBudget);

//			budgetTempGrid.setPartNameGenerator(budget -> conflict.contains(budget.getContentMd5()) ? "warn" : null);
	}

	/*
	 * I do not want null-s its mess up later the functions
	 */
	private static BigDecimal toBigDecimal(String value) {
		return (value == null || value.isEmpty()) ? BigDecimal.ZERO : new BigDecimal(value);
	}

	public void setBudgetTable(List<Budget> exampleBudget) {
		saveButton.setEnabled(true);
		processedBudget = exampleBudget;
		budgetTempGrid.setItems(processedBudget);

	}

	// When you want to scroll:
	private void scrollToBottom() {
		UI.getCurrent().getPage()
				.executeJs("setTimeout(function() {" + "var div = document.getElementById('scrollable-content');"
						+ "if(div) div.scrollTop = div.scrollHeight;" + "}, 100);");
	}

}