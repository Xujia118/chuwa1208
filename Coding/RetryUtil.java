package com.github.xujia118.simpletaskmanager;

import java.util.concurrent.Callable;

public class RetryUtil {

    public static <T> T executeWithRetry(
            Callable<T> task,
            int maxRetries,
            long baseDelayMs,
            long maxDelayMs) throws Exception {

        int attempt = 0; // 0 is the initial try
        Exception lastException = null;

        while (attempt < maxRetries) {
            try {
                return task.call();
            } catch (Exception e) {
                lastException = e;

                // If this was our last allowed attempt, don't sleep, just exit loop
                if (attempt >= maxRetries) {
                    break;
                }

                // Exponential backoff calculation: base * 2^attempt
                long delay = (long) (baseDelayMs * Math.pow(2, attempt));
                long sleepTime = Math.min(delay, maxDelayMs);

                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }

                attempt++; // Move to the next retry attempt
            }
        }

        throw lastException;
    }
}