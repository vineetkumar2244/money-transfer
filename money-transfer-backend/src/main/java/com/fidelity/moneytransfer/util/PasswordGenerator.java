package com.fidelity.moneytransfer.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String[] rawPasswords = {
            "alice123", "bob123", "charlie123", "dave123", "eve123",
            "frank123", "grace123", "heidi123", "ivan123", "judy123",
            "admin123"
        };

        for (String raw : rawPasswords) {
            String encoded = encoder.encode(raw);
            System.out.println(raw + " -> " + encoded);
        }
    }
}
