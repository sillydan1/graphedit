package dk.gtz.graphedit.util;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RetryUtils {
    private static final Logger logger = LoggerFactory.getLogger(RetryUtils.class);

    public static <T> T tryTimes(int maxAttempts, int sleepMillis, Supplier<T> f) {
	for(var attempts = 0; attempts < maxAttempts; attempts++) {
	    try {
		return f.get();
	    } catch(Exception e) {
		logger.warn("'{}' {}/{} attempts left", e.getMessage(), attempts+1, maxAttempts);
		sleep(sleepMillis);
	    }
	}
	throw new RuntimeException("too many attempts");
    }

    public static void sleep(int milliseconds) {
	try {
	    Thread.sleep(milliseconds);
	} catch(InterruptedException e2) {
	    // ignored
	}
    }
}
