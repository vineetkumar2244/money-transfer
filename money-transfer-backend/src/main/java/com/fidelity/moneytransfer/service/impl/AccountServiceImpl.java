package com.fidelity.moneytransfer.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fidelity.moneytransfer.domain.Account;
import com.fidelity.moneytransfer.domain.TransactionLog;
import com.fidelity.moneytransfer.exception.AccountNotFoundException;
import com.fidelity.moneytransfer.repository.AccountRepository;
import com.fidelity.moneytransfer.repository.TransactionLogRepository;
import com.fidelity.moneytransfer.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionLogRepository transactionLogRepository;

    // ✅ Manual constructor (Spring will inject automatically)
    public AccountServiceImpl(AccountRepository accountRepository,
                              TransactionLogRepository transactionLogRepository) {
        this.accountRepository = accountRepository;
        this.transactionLogRepository = transactionLogRepository;
    }

    @Override
    public Account getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    public BigDecimal getBalance(Long accountId) {
        return getAccount(accountId).getBalance();
    }

    @Override
    public List<TransactionLog> getTransactions(Long accountId) {
        return transactionLogRepository.findAll()
                .stream()
                .filter(t -> t.getFromAccountId().equals(accountId)
                          || t.getToAccountId().equals(accountId))
                .toList();
    }
}
