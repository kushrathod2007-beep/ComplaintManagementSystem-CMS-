package com.cms.util;

import java.security.SecureRandom;
import java.time.LocalDate;

public class ComplaintIdGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    // Example: CMS-2026-483920 (15 characters, fits the VARCHAR(20) column)
    public static String generateId() {
        int randomNumber = 100000 + RANDOM.nextInt(900000);
        return "CMS-" + LocalDate.now().getYear() + "-" + randomNumber;
    }
}
