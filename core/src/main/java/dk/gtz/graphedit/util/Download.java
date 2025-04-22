package dk.gtz.graphedit.util;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for downloading files and getting the progress of the download.
 */
public class Download implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(Download.class);

	/**
	 * The state that the download can be in.
	 */
	public static enum State {
		/**
		 * The download is currently in progress.
		 */
		DOWNLOADING,
		/**
		 * The download is paused. Call {@link Download#resume} to resume.
		 */
		PAUSED,
		/**
		 * The download is complete. Call {@link Download#downloadedFile} to get the
		 * downloaded file.
		 */
		COMPLETE,
		/**
		 * The download was cancelled. Discard the {@link Download} instance and start
		 * again.
		 */
		CANCELLED,
		/**
		 * An error occurred during the download. Check the logs for more information.
		 */
		ERROR
	}

	private static final int MAX_BUFFER_SIZE = 1024;
	private URL url;
	private int size;
	private int downloaded;
	private State status;
	private Runnable onStateChange;
	private Optional<String> downloadedFilepath;

	/**
	 * Create a new download instance.
	 * 
	 * @param url The URL to download from.
	 */
	public Download(URL url) {
		this.url = url;
		this.onStateChange = () -> {
		};
		downloadedFilepath = Optional.empty();
		size = -1;
		downloaded = 0;
	}

	/**
	 * Set the callback that will be called when the state of the download changes.
	 * This includes when some progress has been made.
	 * 
	 * @param onStateChange The callback to call.
	 */
	public void setOnStateChanged(Runnable onStateChange) {
		this.onStateChange = onStateChange;
	}

	/**
	 * Get the URL that is being downloaded from.
	 * 
	 * @return The URL.
	 */
	public String getUrl() {
		return url.toString();
	}

	/**
	 * Get the size of the file being downloaded.
	 * 
	 * @return The size in bytes.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Get the current percentage [0.0 - 100.0] of the download.
	 * 
	 * @return The percentage of completion.
	 */
	public float getProgress() {
		return ((float) downloaded / size) * 100;
	}

	/**
	 * Get the current state of the download.
	 * 
	 * @return The state of the download.
	 */
	public State getStatus() {
		return status;
	}

	/**
	 * Pause the download. Call {@link Download#resume} to resume.
	 */
	public void pause() {
		status = State.PAUSED;
		stateChanged();
	}

	/**
	 * Resume the download if it was paused.
	 */
	public void resume() {
		status = State.DOWNLOADING;
		stateChanged();
		download();
	}

	/**
	 * Cancel the download. Discard the this instance and start again.
	 */
	public void cancel() {
		status = State.CANCELLED;
		stateChanged();
	}

	private void error(String message) {
		logger.error(message);
		status = State.ERROR;
		stateChanged();
	}

	/**
	 * Start the download.
	 */
	public void download() {
		status = State.DOWNLOADING;
		new Thread(this).start();
	}

	/**
	 * Get the file name from a URL.
	 * 
	 * @param url The URL to get the file name from.
	 * @return The file name.
	 */
	public static String getFileName(URL url) {
		var fileName = url.getFile();
		return fileName.substring(fileName.lastIndexOf('/') + 1);
	}

	/**
	 * Get the downloaded file path.
	 * 
	 * @return The downloaded file path.
	 */
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
				} catch (Exception e) {
				}
			}
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
				}
			}
		}
	}

	private void stateChanged() {
		onStateChange.run();
	}
}
