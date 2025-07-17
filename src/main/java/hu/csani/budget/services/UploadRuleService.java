package hu.csani.budget.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;

import hu.csani.budget.data.Budget;
import hu.csani.budget.data.UploadRule;
import hu.csani.budget.repositories.UploadRuleRepository;
import hu.csani.budget.views.upload.UploadView;

@Service
public class UploadRuleService {

	private final UploadRuleRepository uploadRuleRepository;
	private BudgetService budService;
	private UploadView uploadView;

	public UploadRuleService(UploadRuleRepository uploadRuleRepository, BudgetService budService) {
		this.uploadRuleRepository = uploadRuleRepository;
		this.budService = budService;
	}

	public List<UploadRule> findAll() {
		return uploadRuleRepository.findAll();
	}

	public Page<UploadRule> findAll(Pageable pageable) {
		return uploadRuleRepository.findAll(pageable);
	}

	public Optional<UploadRule> findById(Integer id) {
		return uploadRuleRepository.findById(id);
	}

	public UploadRule save(UploadRule uploadRule) {
		return uploadRuleRepository.save(uploadRule);
	}

	public List<Budget> getLastDaysFromDatabase(Grid<Budget> budgetTempGrid) {

		List<Budget> allBudgets = budgetTempGrid.getListDataView().getItems().sorted().toList();

		Budget firstbudget = allBudgets.get(0);
		Budget lastbudget = allBudgets.get(allBudgets.size()-1);
//		List<Budget> byAccountId = budService.findByAccountId(allBudgets.get(0).getAccountId());
		List<Budget> byAccountId = budService.findByAccountIdAndTransactionDateBetween(allBudgets.get(0).getAccountId(),
				firstbudget.getTransactionDate().minusDays(1), lastbudget.getTransactionDate()).stream().sorted().toList();

//		Set<String> referenceHashes = byAccountId.stream().map(Budget::getContentMd5).collect(Collectors.toSet());

//		
//		List<Budget> collect = allBudgets.stream().filter(b -> referenceHashes.contains(b.getContentMd5()))
//				.collect(Collectors.toList());

		return byAccountId;
	}

	public void deleteById(Integer id) {
		uploadRuleRepository.deleteById(id);
	}

	public List<Budget> testUploadRule(UploadRule rule, List<Map<String, String>> exampleTable) {
		Notification.show("Testing UploadRule: " + rule.getUploadRuleName());

		List<Budget> exampleBudget = new ArrayList<>();

		boolean firstRow = true;

//		int id = 0;

		for (Map<String, String> budget : exampleTable) {

			if (firstRow) {
				// skipheader
				firstRow = false;
				continue;
			}

			Budget row = new Budget();
//			row.setBudgetId(++id);

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

				if (decimalSeparator != null && !decimalSeparator.equals(".")) {
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

//		grid.setItems(exampleBudget);
//		uploadView.setBudgetTable(exampleBudget);

		return exampleBudget;

	}

	/*
	 * I do not want null-s its mess up later the functions
	 */
	private static BigDecimal toBigDecimal(String value) {
		return (value == null || value.isEmpty()) ? BigDecimal.ZERO : new BigDecimal(value);
	}

	public void setUploadView(UploadView uploadView) {
		this.uploadView = uploadView;

	}
}
