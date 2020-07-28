package jp.co.esp.batch.purge.exception;

/**
 * 起動引数例外
 */
public class CommandlineArgumentsException extends Exception {

	/**
	 * コンストラクタ
	 */
	public CommandlineArgumentsException() {
		super();
	}

	/**
	 * コンストラクタ
	 * 
	 * @param message メッセージ
	 */
	public CommandlineArgumentsException(String message) {
		super(message);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param message メッセージ
	 * @param cause 原因
	 */
	public CommandlineArgumentsException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param cause 原因
	 */
	public CommandlineArgumentsException(Throwable cause) {
		super(cause);
	}
}
