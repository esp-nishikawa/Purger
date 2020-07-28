package jp.co.esp.batch.purge.util;

import java.io.Closeable;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

import jp.co.esp.batch.purge.exception.DBException;

/**
 * DB接続マネージャー
 */
public class DBConnectionManager implements Closeable {

	/** MyBatis設定ファイルパス */
	private static final String MYBATIS_CONFIG_FILE_PATH = "jp/co/esp/batch/purge/mybatis/mybatis-config.xml";

	/** ロガー */
	private static final Logger logger = Logger.getLogger(DBConnectionManager.class);

	/** SQLセッション */
	private final SqlSession sqlSession;

	/**
	 * コンストラクタ
	 * 
	 * @param driver JDBCドライバーの完全修飾Javaクラス名
	 * @param url JDBC接続URL
	 * @param username ユーザー名
	 * @param password パスワード
	 * @throws DBException DB例外
	 */
	public DBConnectionManager(String driver, String url, String username, String password)
			throws DBException {
		final Properties properties = new Properties();
		properties.put("driver", driver);
		properties.put("url", url);
		properties.put("username", username);
		properties.put("password", password);
		try {
			final InputStream inputStream = Resources.getResourceAsStream(MYBATIS_CONFIG_FILE_PATH);
			final SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, properties);
			this.sqlSession = sqlSessionFactory.openSession(false);
		} catch (Exception e) {
			logger.error("SQLセッションの作成に失敗しました。" + properties.toString(), e);
			throw new DBException(e);
		}
	}

	/**
	 * SQLセッションを取得
	 *
	 * @return SQLセッション
	 */
	public SqlSession getSqlSession() {
		return this.sqlSession;
	}

	/**
	 * コミット
	 *
	 * @throws DBException DB例外
	 */
	public void commit() throws DBException {
		try {
			this.sqlSession.commit();
		} catch (Exception e) {
			logger.error("コミットに失敗しました。", e);
			throw new DBException(e);
		}
	}

	@Override
	public void close() throws IOException {
		this.sqlSession.close();
	}
}
