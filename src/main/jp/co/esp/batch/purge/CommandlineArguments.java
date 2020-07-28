package jp.co.esp.batch.purge;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Arrays;

import org.apache.log4j.Logger;

import jp.co.esp.batch.purge.exception.CommandlineArgumentsException;

/**
 * 起動引数
 */
public class CommandlineArguments {

	/** ロガー */
	private static final Logger logger = Logger.getLogger(CommandlineArguments.class);

	/** データ保持期間（日数） */
	private Integer period;

	/** データ保持期間（日時） */
	private LocalDateTime date;

	/**
	 * コンストラクタ
	 * 
	 * @param args 起動引数
	 * @throws CommandlineArgumentsException 起動引数例外
	 */
	public CommandlineArguments(String[] args) throws CommandlineArgumentsException {
		if (args.length == 0) {
			logger.error("起動引数 -period または -date を指定して下さい。");
			throw new CommandlineArgumentsException();
		}
		for (int i = 0; i < args.length; i++) {
			if ("-period".equals(args[i])) {
				try {
					this.period = Integer.parseInt(args[++i]);
				} catch (Exception e) {
					logger.error("起動引数 -period は数値で指定して下さい。" + Arrays.toString(args), e);
					throw new CommandlineArgumentsException(e);
				}
			} else if ("-date".equals(args[i])) {
				try {
					this.date = DateTimeFormatter.ofPattern("uuuuMMddHHmmss")
						.withResolverStyle(ResolverStyle.STRICT)
						.parse(args[++i], LocalDateTime::from);
				} catch (Exception e) {
					logger.error("起動引数 -date は yyyyMMddHHmmss で指定して下さい。" + Arrays.toString(args), e);
					throw new CommandlineArgumentsException(e);
				}
			} else {
				logger.error("起動引数は -period または -date で指定して下さい。" + Arrays.toString(args));
				throw new CommandlineArgumentsException();
			}
		}
	}

	/**
	 * データ保持期間（日数）を取得
	 *
	 * @return データ保持期間（日数）
	 */
	public Integer getRetentionPeriod() {
		return this.period;
	}

	/**
	 * データ保持期間（日時）を取得
	 *
	 * @return データ保持期間（日時）
	 */
	public LocalDateTime getRetentionDate() {
		return this.date;
	}
}
