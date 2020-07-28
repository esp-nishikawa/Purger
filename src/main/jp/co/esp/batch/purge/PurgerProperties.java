package jp.co.esp.batch.purge;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

/**
 * 設定ファイル
 */
public class PurgerProperties {

	/** プロパティファイル名 */
	private static final String FILE_NAME = "purger.properties";

	/** プロパティファイルURLのシステムプロパティキー */
	private static final String SYSTEM_PROPERTY_KEY_URL = "purger.configuration";

	/** プロパティ */
	private final Properties properties = new Properties();

	/**
	 * コンストラクタ
	 * 
	 * @throws IOException 入出力例外
	 */
	public PurgerProperties() throws IOException {
		final URL url = System.getProperty(SYSTEM_PROPERTY_KEY_URL) == null
				? this.getClass().getClassLoader().getResource(FILE_NAME)
				: new URL(System.getProperty(SYSTEM_PROPERTY_KEY_URL));
		if (url == null) {
			throw new FileNotFoundException(FILE_NAME);
		}
		try (InputStream inputStream = url.openStream()) {
			this.properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
		}
	}

	/**
	 * JDBCドライバーの完全修飾Javaクラス名を取得
	 *
	 * @return JDBCドライバーの完全修飾Javaクラス名
	 */
	public String getJdbcDriver() {
		return Objects.requireNonNull(this.properties.getProperty("jdbc.driver"));
	}

	/**
	 * JDBC接続URLを取得
	 *
	 * @return JDBC接続URL
	 */
	public String getJdbcUrl() {
		return Objects.requireNonNull(this.properties.getProperty("jdbc.url"));
	}

	/**
	 * JDBC接続ユーザー名を取得
	 *
	 * @return JDBC接続ユーザー名
	 */
	public String getJdbcUsername() {
		return Objects.requireNonNull(this.properties.getProperty("jdbc.username"));
	}

	/**
	 * JDBC接続パスワードを取得
	 *
	 * @return JDBC接続パスワード
	 */
	public String getJdbcPassword() {
		return Objects.requireNonNull(this.properties.getProperty("jdbc.password"));
	}

	/**
	 * 削除単位件数を取得
	 *
	 * @return 削除単位件数
	 */
	public int getDeleteUnit() {
		return Integer.parseInt(this.properties.getProperty("delete.unit"));
	}

	/**
	 * コミット単位件数を取得
	 *
	 * @return コミット単位件数
	 */
	public int getCommitUnit() {
		return Integer.parseInt(this.properties.getProperty("commit.unit"));
	}

	/**
	 * ファイル出力先を取得
	 *
	 * @return ファイル出力先
	 */
	public String getFileOutputDirectory() {
		return Objects.requireNonNull(this.properties.getProperty("file.output.directory"));
	}
}
