package jp.co.esp.batch.purge.dto;

import java.util.Arrays;
import java.util.Date;

import jp.co.esp.batch.purge.util.StringUtil;

/**
 * サンプルデータDTO
 */
public class SampleDataDto {

	/** カラム名 */
	public static final String[] COLUMN_NAME = { "DATA_ID", "MESSAGE",
			"CREATE_USER", "CREATE_DT", "UPDATE_USER", "UPDATE_DT" };

	/** データID */
	private Integer dataId;

	/** メッセージ */
	private String message;

	/** 作成ユーザ */
	private String createUser;

	/** 作成日時 */
	private Date createDt;

	/** 更新ユーザ */
	private String updateUser;

	/** 更新日時 */
	private Date updateDt;

	/**
	 * コンストラクタ
	 * 
	 * @param dataId データID
	 * @param message メッセージ
	 * @param createUser 作成ユーザ
	 * @param createDt 作成日時
	 * @param updateUser 更新ユーザ
	 * @param updateDt 更新日時
	 */
	public SampleDataDto(Integer dataId, String message,
			String createUser, Date createDt, String updateUser, Date updateDt) {
		this.dataId = dataId;
		this.message = message;
		this.createUser = createUser;
		this.createDt = createDt;
		this.updateUser = updateUser;
		this.updateDt = updateDt;
	}

	/**
	 * データIDを取得
	 * 
	 * @return データID
	 */
	public Integer getDataId() {
		return this.dataId;
	}

	/**
	 * メッセージを取得
	 * 
	 * @return メッセージ
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * 作成ユーザを取得
	 * 
	 * @return 作成ユーザ
	 */
	public String getCreateUser() {
		return this.createUser;
	}

	/**
	 * 作成日時を取得
	 * 
	 * @return 作成日時
	 */
	public Date getCreateDt() {
		return this.createDt;
	}

	/**
	 * 更新ユーザを取得
	 * 
	 * @return 更新ユーザ
	 */
	public String getUpdateUser() {
		return this.updateUser;
	}

	/**
	 * 更新日時を取得
	 * 
	 * @return 更新日時
	 */
	public Date getUpdateDt() {
		return this.updateDt;
	}

	/**
	 * 文字列配列形式に変換
	 * 
	 * @return 文字列配列形式のデータ
	 */
	public String[] toStringArray() {
		final String[] array = {
			String.valueOf(this.getDataId()),
			this.getMessage(),
			this.getCreateUser(),
			StringUtil.formatDate(this.getCreateDt(), "yyyyMMddHHmmss"),
			this.getUpdateUser(),
			StringUtil.formatDate(this.getUpdateDt(), "yyyyMMddHHmmss")
		};
		return array;
	}

	@Override
	public String toString() {
		return Arrays.toString(this.toStringArray());
	}
}
