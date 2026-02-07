package com.fidelity.moneytransfer.service;

import com.fidelity.moneytransfer.dto.AuthRequest;
import com.fidelity.moneytransfer.dto.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request);
}
