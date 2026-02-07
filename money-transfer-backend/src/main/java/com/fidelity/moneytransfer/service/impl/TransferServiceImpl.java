package com.fidelity.moneytransfer.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fidelity.moneytransfer.domain.Account;
import com.fidelity.moneytransfer.domain.TransactionLog;
import com.fidelity.moneytransfer.dto.TransferRequest;
import com.fidelity.moneytransfer.dto.TransferResponse;
import com.fidelity.moneytransfer.enums.TransactionStatus;
import com.fidelity.moneytransfer.exception.AccountNotFoundException;
import com.fidelity.moneytransfer.exception.DuplicateTransferException;
import com.fidelity.moneytransfer.repository.AccountRepository;
import com.fidelity.moneytransfer.repository.TransactionLogRepository;
import com.fidelity.moneytransfer.service.TransferService;

@Service
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;
    private final TransactionLogRepository transactionLogRepository;

    public TransferServiceImpl(AccountRepository accountRepository,
                               TransactionLogRepository transactionLogRepository) {
        this.accountRepository = accountRepository;
        this.transactionLogRepository = transactionLogRepository;
    }

    @Override
    @Transactional
    public TransferResponse transfer(TransferRequest request) {

        // ✅ Idempotency check
        transactionLogRepository.findByIdempotencyKey(request.getIdempotencyKey())
                .ifPresent(t -> {
                    throw new DuplicateTransferException(request.getIdempotencyKey());
                });

        // ✅ Validate accounts are different
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new IllegalArgumentException("Source and destination accounts must be different");
        }

        // 1️⃣ Fetch accounts
        Account fromAccount = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() -> new AccountNotFoundException(request.getFromAccountId()));

        Account toAccount = accountRepository.findById(request.getToAccountId())
                .orElseThrow(() -> new AccountNotFoundException(request.getToAccountId()));

        // 2️⃣ Debit/Credit
        fromAccount.debit(request.getAmount());
        toAccount.credit(request.getAmount());

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // 3️⃣ Create transaction log manually
        TransactionLog log = new TransactionLog(
                fromAccount.getId(),
                toAccount.getId(),
                request.getAmount(),
                TransactionStatus.SUCCESS,
                null,
                request.getIdempotencyKey()
        );

        transactionLogRepository.save(log);

        // 4️⃣ Return response manually
        return new TransferResponse(
                log.getId(),
                "SUCCESS",
                "Transfer completed successfully",
                fromAccount.getId(),
                toAccount.getId(),
                request.getAmount()
        );
    }
}
