package com.fidelity.moneytransfer.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fidelity.moneytransfer.domain.TransactionLog;
import com.fidelity.moneytransfer.dto.AccountResponse;
import com.fidelity.moneytransfer.service.AccountService;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long id) {
        return ResponseEntity.ok(AccountResponse.fromEntity(accountService.getAccount(id)));
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getBalance(id));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionLog>> getTransactions(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getTransactions(id));
    }
}
