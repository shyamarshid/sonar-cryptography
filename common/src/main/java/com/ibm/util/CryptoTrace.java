package com.ibm.util;

import java.time.Instant;

public final class CryptoTrace {
    private CryptoTrace() {}

    public static boolean isEnabled() {
        return Boolean.getBoolean("crypto.trace")
                || "true".equalsIgnoreCase(System.getProperty("sonar.crypto.trace"));
    }

    public static String fmt(Object self, String method, String msg) {
        return "[CRYPTO-TRACE] "
                + Instant.now()
                + " "
                + Thread.currentThread().getName()
                + " "
                + self.getClass().getSimpleName()
                + "#"
                + method
                + " "
                + msg;
    }

    public static String fmt(Class<?> cls, String method, String msg) {
        return "[CRYPTO-TRACE] "
                + Instant.now()
                + " "
                + Thread.currentThread().getName()
                + " "
                + cls.getSimpleName()
                + "#"
                + method
                + " "
                + msg;
    }
}
