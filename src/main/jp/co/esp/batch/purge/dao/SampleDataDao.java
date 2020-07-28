package jp.co.esp.batch.purge.dao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import jp.co.esp.batch.purge.dto.SampleDataDto;
import jp.co.esp.batch.purge.exception.DBException;

/**
 * サンプルデータDAO
 */
public class SampleDataDao {

	/** マッパーの名前空間 */
	private static final String MAPPER_NAMESPACE = "jp.co.esp.batch.purge.mybatis.mappers.sample_data";

	/** ロガー */
	private static final Logger logger = Logger.getLogger(SampleDataDao.class);

	/** SQLセッション */
	private final SqlSession sqlSession;

	/**
	 * コンストラクタ
	 * 
	 * @param sqlSession SQLセッション
	 */
	public SampleDataDao(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	/**
	 * 保持期間を過ぎたデータを取得
	 * 
	 * @param period データ保持期間（日数）
	 * @param date データ保持期間（日時）
	 * @return 保持期間を過ぎたデータのリスト
	 * @throws DBException DB例外
	 */
	public List<SampleDataDto> select(Integer period, LocalDateTime date)
		throws DBException {
		final Map<String, Object> parameters = new HashMap<>();
		if (period != null) {
			parameters.put("period", period);
		}
		if (date != null) {
			parameters.put("date", Timestamp.valueOf(date));
		}
		try {
			return this.sqlSession.selectList(MAPPER_NAMESPACE + ".select", parameters);
		} catch (Exception e) {
			logger.error("サンプルデータの取得に失敗しました。" + parameters.toString(), e);
			throw new DBException(e);
		}
	}

	/**
	 * データを削除
	 * 
	 * @param dataIdList データIDリスト
	 * @throws DBException DB例外
	 */
	public void delete(List<Integer> dataIdList)
		throws DBException {
		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("dataIdList", dataIdList);
		try {
			this.sqlSession.delete(MAPPER_NAMESPACE + ".delete", parameters);
		} catch (Exception e) {
			logger.error("サンプルデータの削除に失敗しました。" + parameters.toString(), e);
			throw new DBException(e);
		}
	}

	/**
	 * データを登録
	 * 
	 * @param dataId データID
	 * @param message メッセージ
	 * @param userId ユーザID
	 * @throws DBException DB例外
	 */
	public void insert(Integer dataId, String message, String userId)
		throws DBException {
		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("dataId", dataId);
		parameters.put("message", message);
		parameters.put("userId", userId);
		try {
			this.sqlSession.insert(MAPPER_NAMESPACE + ".insert", parameters);
		} catch (Exception e) {
			logger.error("サンプルデータの登録に失敗しました。" + parameters.toString(), e);
			throw new DBException(e);
		}
	}
}
