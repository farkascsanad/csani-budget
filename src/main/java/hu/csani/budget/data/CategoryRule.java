package hu.csani.budget.data;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "category_rule", schema = "app")
public class CategoryRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_rule_id")
    private Long categoryRuleId;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "rule_name", nullable = false)
    private String ruleName;

    @Column(name = "description")
    private String description;

    @Column(name = "priority", nullable = false)
    private Integer priority = 100;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "account_id")
    private Integer accountId;

    @Column(name = "min_amount")
    private BigDecimal minAmount;

    @Column(name = "max_amount")
    private BigDecimal maxAmount;

    @Column(name = "direction")
    private String direction;

    @Column(name = "currency")
    private String currency;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "other_party_name")
    private String otherPartyName;

    @Column(name = "other_party_account_number")
    private String otherPartyAccountNumber;

    @Column(name = "note_pattern")
    private String notePattern;

    @Column(name = "booking_date_from")
    private LocalDate bookingDateFrom;

    @Column(name = "booking_date_to")
    private LocalDate bookingDateTo;

    @Column(name = "transaction_date_from")
    private LocalDate transactionDateFrom;

    @Column(name = "transaction_date_to")
    private LocalDate transactionDateTo;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    // Getters and Setters

    public Long getCategoryRuleId() {
        return categoryRuleId;
    }

    public void setCategoryRuleId(Long categoryRuleId) {
        this.categoryRuleId = categoryRuleId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
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

    public String getNotePattern() {
        return notePattern;
    }

    public void setNotePattern(String notePattern) {
        this.notePattern = notePattern;
    }

    public LocalDate getBookingDateFrom() {
        return bookingDateFrom;
    }

    public void setBookingDateFrom(LocalDate bookingDateFrom) {
        this.bookingDateFrom = bookingDateFrom;
    }

    public LocalDate getBookingDateTo() {
        return bookingDateTo;
    }

    public void setBookingDateTo(LocalDate bookingDateTo) {
        this.bookingDateTo = bookingDateTo;
    }

    public LocalDate getTransactionDateFrom() {
        return transactionDateFrom;
    }

    public void setTransactionDateFrom(LocalDate transactionDateFrom) {
        this.transactionDateFrom = transactionDateFrom;
    }

    public LocalDate getTransactionDateTo() {
        return transactionDateTo;
    }

    public void setTransactionDateTo(LocalDate transactionDateTo) {
        this.transactionDateTo = transactionDateTo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}