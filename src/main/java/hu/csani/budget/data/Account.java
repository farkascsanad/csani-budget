package hu.csani.budget.data;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "account", schema = "app")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Integer accountId;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "iban_account_number")
    private String ibanAccountNumber;

    @Column(name = "account_description")
    private String accountDescription;

    @Column(name = "account_active_from")
    private LocalDate accountActiveFrom;

    @Column(name = "account_active_to")
    private LocalDate accountActiveTo;

    @Column(name = "table_pattern")
    private String tablePattern;


    @Column(name = "account_type")
    private String accountType;

    // Getters and Setters

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIbanAccountNumber() {
        return ibanAccountNumber;
    }

    public void setIbanAccountNumber(String ibanAccountNumber) {
        this.ibanAccountNumber = ibanAccountNumber;
    }

    public String getAccountDescription() {
        return accountDescription;
    }

    public void setAccountDescription(String accountDescription) {
        this.accountDescription = accountDescription;
    }

    public LocalDate getAccountActiveFrom() {
        return accountActiveFrom;
    }

    public void setAccountActiveFrom(LocalDate accountActiveFrom) {
        this.accountActiveFrom = accountActiveFrom;
    }

    public LocalDate getAccountActiveTo() {
        return accountActiveTo;
    }

    public void setAccountActiveTo(LocalDate accountActiveTo) {
        this.accountActiveTo = accountActiveTo;
    }

    public String getTablePattern() {
        return tablePattern;
    }

    public void setTablePattern(String tablePattern) {
        this.tablePattern = tablePattern;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

	@Override
	public String toString() {
		return accountName +"("+accountId+")";
	}
    
    
}
