package hu.csani.budget.data;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "budget", schema = "app")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    private Integer budgetId;

    @Column(name = "account_id")
    private Integer accountId; // int2 maps to Short

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

    @ManyToOne(fetch = FetchType.LAZY)
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

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

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
}
