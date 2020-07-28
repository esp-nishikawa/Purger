package jp.co.esp.batch.purge;

import org.apache.log4j.Logger;

import jp.co.esp.batch.purge.constants.ExitStatus;
import jp.co.esp.batch.purge.logic.PurgeSampleDataLogic;

/**
 * エントリーポイント
 */
public class PurgerMain {

	/** ロガー */
	private static final Logger logger = Logger.getLogger(PurgerMain.class);

	/**
	 * エントリーポイント
	 *
	 * @param args 起動引数
	 */
	public static void main(String[] args) {
		int exitStatus = ExitStatus.NG;
		try {
			logger.info("処理を開始しました。" + String.join(" ", args));
			final CommandlineArguments arguments = new CommandlineArguments(args);
			final PurgerProperties properties = new PurgerProperties();
			new PurgeSampleDataLogic(arguments, properties).execute();
			exitStatus = ExitStatus.OK;
		} catch (Exception e) {
			logger.error("エラーが発生しました。", e);
		} finally {
			logger.info("処理を終了しました。" + exitStatus);
		}
		System.exit(exitStatus);
	}
}
