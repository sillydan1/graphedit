package dk.gtz.graphedit.view.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

/**
 * The infamous stream gobbler. Use this to capture output streams.
 *
 * e.g.:
 * var process = new ProcessBuilder("ls").start();
 * var streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
 */
public class StreamGobbler implements Runnable {
    private InputStream inputStream;
    private Consumer<String> consumeInputLine;

    public StreamGobbler(InputStream inputStream, Consumer<String> consumeInputLine) {
        this.inputStream = inputStream;
        this.consumeInputLine = consumeInputLine;
    }

    public void run() {
        new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumeInputLine);
    }
}

