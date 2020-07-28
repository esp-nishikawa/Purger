package jp.co.esp.batch.purge.exception;

/**
 * DB例外
 */
public class DBException extends Exception {

	/**
	 * コンストラクタ
	 */
	public DBException() {
		super();
	}

	/**
	 * コンストラクタ
	 * 
	 * @param message メッセージ
	 */
	public DBException(String message) {
		super(message);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param message メッセージ
	 * @param cause 原因
	 */
	public DBException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param cause 原因
	 */
	public DBException(Throwable cause) {
		super(cause);
	}
}
