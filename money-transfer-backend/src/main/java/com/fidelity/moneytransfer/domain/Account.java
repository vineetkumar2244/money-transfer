package com.fidelity.moneytransfer.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fidelity.moneytransfer.enums.AccountStatus;
import com.fidelity.moneytransfer.exception.AccountNotActiveException;
import com.fidelity.moneytransfer.exception.InsufficientBalanceException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String holderName;

    @Column(nullable = false)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Version
    private Integer version;

    private LocalDateTime lastUpdated;

    // ✅ REQUIRED by JPA
    public Account() {
    }

    // Optional convenience constructor
    public Account(Long id, String holderName, BigDecimal balance,
                   AccountStatus status, Integer version, LocalDateTime lastUpdated) {
        this.id = id;
        this.holderName = holderName;
        this.balance = balance;
        this.status = status;
        this.version = version;
        this.lastUpdated = lastUpdated;
    }

    // ---------- Getters ----------

    public Long getId() {
        return id;
    }

    public String getHolderName() {
        return holderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public Integer getVersion() {
        return version;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    // ---------- Setters (keep minimal) ----------

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    // ---------- Business Logic ----------

    public boolean isActive() {
        return this.status == AccountStatus.ACTIVE;
    }

    public void debit(BigDecimal amount) {
        if (!isActive()) {
            throw new AccountNotActiveException(this.id);
        }
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
        this.balance = this.balance.subtract(amount);
        this.lastUpdated = LocalDateTime.now();
    }

    public void credit(BigDecimal amount) {
        if (!isActive()) {
            throw new AccountNotActiveException(this.id);
        }
        this.balance = this.balance.add(amount);
        this.lastUpdated = LocalDateTime.now();
    }
}
