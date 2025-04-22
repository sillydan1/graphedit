package dk.gtz.graphedit.util;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility functions for retrying actions multiple times.
 */
public final class RetryUtils {
	private static final Logger logger = LoggerFactory.getLogger(RetryUtils.class);

	private RetryUtils() {
	}

	/**
	 * Try an action until it returns a value or until a set amount of attempts
	 * failed.
	 * 
	 * @param <T>         The return type that the function should return.
	 * @param maxAttempts Maximum amount of times to try
	 * @param sleepMillis Amount of time to wait between failed attempts
	 * @param f           The supplier function to try
	 * @return The return value of the supplier function f
	 * @throws RuntimeException if too many attempts happenned
	 */
	public static <T> T tryTimes(int maxAttempts, int sleepMillis, Supplier<T> f) {
		for (var attempts = 0; attempts < maxAttempts; attempts++) {
			try {
				return f.get();
			} catch (Exception e) {
				logger.trace("'{}' {}/{} attempts left", e.getMessage(), attempts + 1, maxAttempts);
				sleep(sleepMillis);
			}
		}
		throw new RuntimeException("too many attempts");
	}

	/**
	 * Sleep for a set amount of milliseconds. If interrupted, simply return.
	 * 
	 * @param milliseconds The length of time to wait in milliseconds
	 */
	public static void sleep(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e2) {
			// ignored
		}
	}
}
