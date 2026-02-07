package com.fidelity.moneytransfer.service.impl;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
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

        // 1️⃣ Idempotency check
        transactionLogRepository.findByIdempotencyKey(request.getIdempotencyKey())
                .ifPresent(t -> {
                    throw new DuplicateTransferException(request.getIdempotencyKey());
                });

        // 2️⃣ Validate accounts
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new IllegalArgumentException("Source and destination accounts must be different");
        }

        // 3️⃣ Fetch accounts
        Account fromAccount = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() -> new AccountNotFoundException(request.getFromAccountId()));

        Account toAccount = accountRepository.findById(request.getToAccountId())
                .orElseThrow(() -> new AccountNotFoundException(request.getToAccountId()));

        // 4️⃣ Ownership check
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!fromAccount.getOwnerUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You cannot transfer from another user's account");
        }

        // 5️⃣ Debit/Credit with balance check
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance in source account");
        }

        fromAccount.debit(request.getAmount());
        toAccount.credit(request.getAmount());

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // 6️⃣ Transaction log
        TransactionLog log = new TransactionLog(
                fromAccount.getId(),
                toAccount.getId(),
                request.getAmount(),
                TransactionStatus.SUCCESS,
                null,
                request.getIdempotencyKey()
        );
        transactionLogRepository.save(log);

        // 7️⃣ Return response
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
