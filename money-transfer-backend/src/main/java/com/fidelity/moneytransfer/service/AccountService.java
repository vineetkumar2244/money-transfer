package com.fidelity.moneytransfer.service;

import java.math.BigDecimal;
import java.util.List;

import com.fidelity.moneytransfer.domain.Account;
import com.fidelity.moneytransfer.domain.TransactionLog;

public interface AccountService {

    Account getAccount(Long accountId);

    BigDecimal getBalance(Long accountId);

    List<TransactionLog> getTransactions(Long accountId);
}
