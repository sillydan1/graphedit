package dk.gtz.graphedit.util;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Download implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Download.class);
    public static enum State {
	DOWNLOADING,
	PAUSED,
	COMPLETE,
	CANCELLED,
	ERROR
    }
    private static final int MAX_BUFFER_SIZE = 1024;
    private URL url;
    private int size;
    private int downloaded;
    private State status;
    private Runnable onStateChange;
    private Optional<String> downloadedFilepath;

    public Download(URL url) {
	this.url = url;
	this.onStateChange = () -> {};
	downloadedFilepath = Optional.empty();
	size = -1;
	downloaded = 0;
    }

    public void setOnStateChanged(Runnable onStateChange) {
	this.onStateChange = onStateChange;
    }

    public String getUrl() {
	return url.toString();
    }

    public int getSize() {
	return size;
    }

    public float getProgress() {
	return ((float) downloaded / size) * 100;
    }

    public State getStatus() {
	return status;
    }

    public void pause() {
	status = State.PAUSED;
	stateChanged();
    }

    public void resume() {
	status = State.DOWNLOADING;
	stateChanged();
	download();
    }

    public void cancel() {
	status = State.CANCELLED;
	stateChanged();
    }

    private void error(String message) {
	logger.error(message);
	status = State.ERROR;
	stateChanged();
    }

    public void download() {
	status = State.DOWNLOADING;
	new Thread(this).start();
    }

    public String getFileName(URL url) {
	var fileName = url.getFile();
	return fileName.substring(fileName.lastIndexOf('/') + 1);
    }

    public Optional<String> downloadedFile() {
	return downloadedFilepath;	
    }

    public void run() {
	RandomAccessFile file = null;
	InputStream stream = null;
	try {
	    var connection = (HttpURLConnection) url.openConnection();
	    connection.setRequestProperty("Range", "bytes=" + downloaded + "-");
	    connection.connect();
	    if (connection.getResponseCode() / 100 != 2)
		error(connection.getResponseMessage());
	    var contentLength = connection.getContentLength();
	    if (contentLength < 1)
		error("zero content");
	    if (size == -1) {
		size = contentLength;
		stateChanged();
	    }
	    file = new RandomAccessFile(getFileName(url), "rw");
	    file.seek(downloaded);
	    downloadedFilepath = Optional.of(file.toString());
	    stream = connection.getInputStream();
	    while (status == State.DOWNLOADING) {
		byte buffer[];
		if (size - downloaded > MAX_BUFFER_SIZE)
		    buffer = new byte[MAX_BUFFER_SIZE];
		else
		    buffer = new byte[size - downloaded];
		var read = stream.read(buffer);
		if (read == -1)
		    break;
		file.write(buffer, 0, read);
		downloaded += read;
		stateChanged();
	    }

	    if (status == State.DOWNLOADING) {
		status = State.COMPLETE;
		stateChanged();
	    }
	} catch (Exception e) {
	    error(e.getMessage());
	} finally {
	    if (file != null) {
		try {
		    file.close();
		} catch (Exception e) {}
	    }
	    if (stream != null) {
		try {
		    stream.close();
		} catch (Exception e) {}
	    }
	}
    }

    private void stateChanged() {
	onStateChange.run();
    }
}
