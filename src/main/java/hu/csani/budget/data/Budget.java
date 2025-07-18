package hu.csani.budget.data;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

@Entity
@Table(name = "budget", schema = "app")
public class Budget implements Comparable<Budget> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "budget_id")
	private Integer budgetId;

//	@Column(name = "account_id")
//	private Integer accountId; // int2 maps to Short
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "account_id")
	private Account account;

	@Column(name = "booking_date")
	private LocalDate bookingDate;

	@Column(name = "transaction_date")
	private LocalDate transactionDate;

	@Column(name = "amount")
	private BigDecimal amount;

	@Column(name = "amount_in")
	private BigDecimal amountIn;

	@Column(name = "amount_out")
	private BigDecimal amountOut;

	@Column(name = "currency", length = 3)
	private String currency;

	@Column(name = "direction", length = 1)
	private String direction;

	@Column(name = "original_id")
	private String originalId;

	@Column(name = "other_party_name")
	private String otherPartyName;

	@Column(name = "other_party_account_number")
	private String otherPartyAccountNumber;

	@Column(name = "transaction_type")
	private String transactionType;

	@Column(name = "note")
	private String note;

	@Column(name = "category_rule_id")
	private Integer categoryRuleId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id")
	private Category category;

	@Column(name = "manual_category_id")
	private Integer manualCategoryId;

	@Column(name = "transfer_id")
	private Integer transferId;

	// Getters and setters

	public Integer getBudgetId() {
		return budgetId;
	}

	public void setBudgetId(Integer budgetId) {
		this.budgetId = budgetId;
	}

//	public Integer getAccountId() {
//		return accountId;
//	}
//
//	public void setAccountId(Integer accountId) {
//		this.accountId = accountId;
//	}

	public LocalDate getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(LocalDate bookingDate) {
		this.bookingDate = bookingDate;
	}

	public LocalDate getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDate transactionDate) {
		this.transactionDate = transactionDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmountIn() {
		return amountIn;
	}

	public void setAmountIn(BigDecimal amountIn) {
		this.amountIn = amountIn;
	}

	public BigDecimal getAmountOut() {
		return amountOut;
	}

	public void setAmountOut(BigDecimal amountOut) {
		this.amountOut = amountOut;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getOriginalId() {
		return originalId;
	}

	public void setOriginalId(String originalId) {
		this.originalId = originalId;
	}

	public String getOtherPartyName() {
		return otherPartyName;
	}

	public void setOtherPartyName(String otherPartyName) {
		this.otherPartyName = otherPartyName;
	}

	public String getOtherPartyAccountNumber() {
		return otherPartyAccountNumber;
	}

	public void setOtherPartyAccountNumber(String otherPartyAccountNumber) {
		this.otherPartyAccountNumber = otherPartyAccountNumber;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getCategoryRuleId() {
		return categoryRuleId;
	}

	public void setCategoryRuleId(Integer categoryRuleId) {
		this.categoryRuleId = categoryRuleId;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Integer getManualCategoryId() {
		return manualCategoryId;
	}

	public void setManualCategoryId(Integer manualCategoryId) {
		this.manualCategoryId = manualCategoryId;
	}

	public Integer getTransferId() {
		return transferId;
	}

	public void setTransferId(Integer transferId) {
		this.transferId = transferId;
	}

	@Override
	public String toString() {
		return "Budget [budgetId=" + budgetId + ", account=" + account.getAccountName() + ", bookingDate=" + bookingDate
				+ ", transactionDate=" + transactionDate + ", amount=" + amount + ", amountIn=" + amountIn
				+ ", amountOut=" + amountOut + ", currency=" + currency + ", direction=" + direction + ", originalId="
				+ originalId + ", otherPartyName=" + otherPartyName + ", otherPartyAccountNumber="
				+ otherPartyAccountNumber + ", transactionType=" + transactionType + ", note=" + note
				+ ", categoryRuleId=" + categoryRuleId + ", category=" + category + ", manualCategoryId="
				+ manualCategoryId + ", transferId=" + transferId + "]";
	}

	public String getContentMd5() {
		// Concatenate relevant fields as a string (null-safe)
		String data = String.valueOf(account.getAccountId()) + "|" + String.valueOf(bookingDate) + "|"
				+ String.valueOf(transactionDate) + "|" + String.valueOf(amount) + "|" + String.valueOf(amountIn) + "|"
				+ String.valueOf(amountOut) + "|" + String.valueOf(currency) + "|" + String.valueOf(direction) + "|"
				+ String.valueOf(originalId) + "|" + String.valueOf(otherPartyName) + "|"
				+ String.valueOf(otherPartyAccountNumber) + "|" + String.valueOf(transactionType) + "|"
				+ String.valueOf(note);

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(data.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (byte b : digest) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 algorithm not found", e);
		}
	}

	@Override
	public int compareTo(Budget o) {
		// usually toString should not be used,
		// instead one of the attributes or more in a comparator chain
		return transactionDate.compareTo(o.getTransactionDate());
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}



}
