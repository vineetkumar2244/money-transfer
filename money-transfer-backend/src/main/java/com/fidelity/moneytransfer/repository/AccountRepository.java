package com.fidelity.moneytransfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fidelity.moneytransfer.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
