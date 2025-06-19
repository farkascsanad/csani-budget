package hu.csani.budget.data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "upload_rule", schema = "app")
public class UploadRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "upload_rule_id")
    private Integer uploadRuleId;

    @Column(name = "upload_rule_name")
    private String uploadRuleName;

    @Column(name = "description")
    private String description;
    
    
    
    @Column(name = "source_booking_date_column")
    private String sourceBookingDateColumn;
    
    @Column(name = "source_transaction_date_column")
    private String sourceTransactiongDateColumn;

    @Column(name = "amount_splitted")
    private Boolean amountSplitted;

    @Column(name = "source_amount_column")
    private String sourceAmountColumn;

    @Column(name = "source_amount_in_column")
    private String sourceAmountInColumn;

    @Column(name = "source_amount_out_column")
    private String sourceAmountOutColumn;

    @Column(name = "source_currency_column")
    private String sourceCurrencyColumn;

    @Column(name = "source_transaction_type_column")
    private String sourceTransactionTypeColumn;

    @Column(name = "source_other_party_name_columns")
    private String sourceOtherPartyNameColumns;

    @Column(name = "source_other_party_account_number_columns")
    private String sourceOtherPartyAccountNumberColumns;
    
    
    @Column(name = "booking_date_format")
    private String bookingDateFormat;
    
    @Column(name = "transaction_date_format")
    private String transactionDateFormat;
    
    @Column(name = "decimal_separator", length = 1)
    private String decimalSeparator;

    @Column(name = "source_note_columns")
    private String sourceNoteColumns;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "default_account_id", nullable = false, foreignKey = @ForeignKey(name = "upload_rule_account_fk"))
    private Account defaultAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manual_category_id", foreignKey = @ForeignKey(name = "upload_rule_category_fk"))
    private Category manualCategory;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

	public Integer getUploadRuleId() {
		return uploadRuleId;
	}

	public void setUploadRuleId(Integer uploadRuleId) {
		this.uploadRuleId = uploadRuleId;
	}

	public String getUploadRuleName() {
		return uploadRuleName;
	}

	public void setUploadRuleName(String uploadRuleName) {
		this.uploadRuleName = uploadRuleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getAmountSplitted() {
		return amountSplitted;
	}

	public void setAmountSplitted(Boolean amountSplitted) {
		this.amountSplitted = amountSplitted;
	}

	public String getSourceAmountColumn() {
		return sourceAmountColumn;
	}

	public void setSourceAmountColumn(String sourceAmountColumn) {
		this.sourceAmountColumn = sourceAmountColumn;
	}

	public String getSourceAmountInColumn() {
		return sourceAmountInColumn;
	}

	public void setSourceAmountInColumn(String sourceAmountInColumn) {
		this.sourceAmountInColumn = sourceAmountInColumn;
	}

	public String getSourceAmountOutColumn() {
		return sourceAmountOutColumn;
	}

	public void setSourceAmountOutColumn(String sourceAmountOutColumn) {
		this.sourceAmountOutColumn = sourceAmountOutColumn;
	}

	public String getSourceCurrencyColumn() {
		return sourceCurrencyColumn;
	}

	public void setSourceCurrencyColumn(String sourceCurrencyColumn) {
		this.sourceCurrencyColumn = sourceCurrencyColumn;
	}

	public String getSourceTransactionTypeColumn() {
		return sourceTransactionTypeColumn;
	}

	public void setSourceTransactionTypeColumn(String sourceTransactionTypeColumn) {
		this.sourceTransactionTypeColumn = sourceTransactionTypeColumn;
	}

	public String getSourceOtherPartyNameColumns() {
		return sourceOtherPartyNameColumns;
	}

	public void setSourceOtherPartyNameColumns(String sourceOtherPartyNameColumns) {
		this.sourceOtherPartyNameColumns = sourceOtherPartyNameColumns;
	}

	public String getSourceOtherPartyAccountNumberColumns() {
		return sourceOtherPartyAccountNumberColumns;
	}

	public void setSourceOtherPartyAccountNumberColumns(String sourceOtherPartyAccountNumberColumns) {
		this.sourceOtherPartyAccountNumberColumns = sourceOtherPartyAccountNumberColumns;
	}

	public String getSourceNoteColumns() {
		return sourceNoteColumns;
	}

	public void setSourceNoteColumns(String sourceNoteColumns) {
		this.sourceNoteColumns = sourceNoteColumns;
	}

	public Account getDefaultAccount() {
		return defaultAccount;
	}

	public void setDefaultAccount(Account defaultAccount) {
		this.defaultAccount = defaultAccount;
	}

	public Category getManualCategory() {
		return manualCategory;
	}

	public void setManualCategory(Category manualCategory) {
		this.manualCategory = manualCategory;
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
	
	

	public String getSourceBookingDateColumn() {
		return sourceBookingDateColumn;
	}

	public void setSourceBookingDateColumn(String sourceBookingDateColumn) {
		this.sourceBookingDateColumn = sourceBookingDateColumn;
	}

	public String getSourceTransactiongDateColumn() {
		return sourceTransactiongDateColumn;
	}

	public void setSourceTransactiongDateColumn(String sourceTransactiongDateColumn) {
		this.sourceTransactiongDateColumn = sourceTransactiongDateColumn;
	}

	@Override
	public String toString() {
		return "UploadRule [uploadRuleId=" + uploadRuleId + ", uploadRuleName=" + uploadRuleName + ", description="
				+ description + ", amountSplitted=" + amountSplitted + ", sourceAmountColumn=" + sourceAmountColumn
				+ ", sourceAmountInColumn=" + sourceAmountInColumn + ", sourceAmountOutColumn=" + sourceAmountOutColumn
				+ ", sourceCurrencyColumn=" + sourceCurrencyColumn + ", sourceTransactionTypeColumn="
				+ sourceTransactionTypeColumn + ", sourceOtherPartyNameColumns=" + sourceOtherPartyNameColumns
				+ ", sourceOtherPartyAccountNumberColumns=" + sourceOtherPartyAccountNumberColumns
				+ ", sourceNoteColumns=" + sourceNoteColumns + ", defaultAccount=" + defaultAccount
				+ ", manualCategory=" + manualCategory + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}

	public String getBookingDateFormat() {
		return bookingDateFormat;
	}

	public void setBookingDateFormat(String bookingDateFormat) {
		this.bookingDateFormat = bookingDateFormat;
	}

	public String getTransactionDateFormat() {
		return transactionDateFormat;
	}

	public void setTransactionDateFormat(String transactionDateFormat) {
		this.transactionDateFormat = transactionDateFormat;
	}

	public String getDecimalSeparator() {
		return decimalSeparator;
	}

	public void setDecimalSeparator(String decimalSeparator) {
		this.decimalSeparator = decimalSeparator;
	}



	
	

  
    
}
