package org.mounanga.userservice.util;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;

public class VerificationCodeGenerator {
    private static final SecureRandom random = new SecureRandom();
    private static final String DIGITS = "0123456789";

    private VerificationCodeGenerator() {
        super();
    }

    public static @NotNull String generateCode(int length) {
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(DIGITS.length());
            code.append(DIGITS.charAt(index));
        }
        return code.toString();
    }
}
