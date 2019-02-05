package nexusvault.cli.exception;

import nexusvault.cli.NexusvaultCLIBaseException;

public class FileNotReadableException extends NexusvaultCLIBaseException {

	private static final long serialVersionUID = 8456085386529168553L;

	public FileNotReadableException() {
		super();
	}

	public FileNotReadableException(String message) {
		super(message);
	}

	public FileNotReadableException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileNotReadableException(Throwable cause) {
		super(cause);
	}

}
