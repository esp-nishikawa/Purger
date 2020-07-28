package jp.co.esp.batch.purge.util;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 文字列ユーティリティ
 */
public class StringUtil {

	/**
	 * Date型を文字列に変換
	 * 
	 * @param date Date型
	 * @param pattern 文字列のパターン
	 * @return 文字列形式の日時
	 */
	public static String formatDate(Date date, String pattern) {
		try {
			final LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
			return DateTimeFormatter.ofPattern(pattern).format(localDateTime);
		} catch (Exception e) {
			return String.valueOf(date);
		}
	}

	/**
	 * Javaメモリ情報メッセージ取得
	 *
	 * @return Javaメモリ情報メッセージ
	 */
	public static String getJavaMemoryInfo() {
		final long free = Runtime.getRuntime().freeMemory() / 1024;
		final long total = Runtime.getRuntime().totalMemory() / 1024;
		final long max = Runtime.getRuntime().maxMemory() / 1024;
		final long used = total - free;
		final double ratio = (double)used / total;
		final DecimalFormat sizeFormat = new DecimalFormat("#,###KB");
		final DecimalFormat ratioFormat = new DecimalFormat("0.0%");
		return "Total=" + sizeFormat.format(total)
				+ ", Used=" + sizeFormat.format(used)
				+ " (" + ratioFormat.format(ratio)
				+ "), Max=" + sizeFormat.format(max);
	}
}
