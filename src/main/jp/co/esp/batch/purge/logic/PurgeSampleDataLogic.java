package jp.co.esp.batch.purge.logic;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

import jp.co.esp.batch.purge.CommandlineArguments;
import jp.co.esp.batch.purge.PurgerProperties;
import jp.co.esp.batch.purge.dao.SampleDataDao;
import jp.co.esp.batch.purge.dto.SampleDataDto;
import jp.co.esp.batch.purge.exception.DBException;
import jp.co.esp.batch.purge.repository.SampleDataRepository;
import jp.co.esp.batch.purge.util.DBConnectionManager;
import jp.co.esp.batch.purge.util.StringUtil;

/**
 * サンプルデータ棚卸処理
 */
public class PurgeSampleDataLogic {

	/** ファイル名パターン */
	private static final String FILE_NAME_PATTERN = "'SampleData'yyyyMMddHHmmss'.tsv.gz'";

	/** 区切り文字 */
	private static final String SEPARATOR = "\t";

	/** 改行文字 */
	private static final String NEWLINE = "\n";

	/** ロガー */
	private static final Logger logger = Logger.getLogger(PurgeSampleDataLogic.class);

	/** 起動引数 */
	private final CommandlineArguments arguments;

	/** プロパティ */
	private final PurgerProperties properties;

	/**
	 * コンストラクタ
	 * 
	 * @param arguments 起動引数
	 * @param properties プロパティ
	 */
	public PurgeSampleDataLogic(CommandlineArguments arguments, PurgerProperties properties) {
		this.arguments = arguments;
		this.properties = properties;
	}

	/**
	 * サンプルデータ棚卸処理
	 * 
	 * @throws DBException DB例外
	 * @throws IOException 入出力例外
	 */
	public void execute() throws DBException, IOException {
		try (DBConnectionManager connection = new DBConnectionManager(
				this.properties.getJdbcDriver(),
				this.properties.getJdbcUrl(),
				this.properties.getJdbcUsername(),
				this.properties.getJdbcPassword())) {
			// DAO
			final SampleDataDao dao = new SampleDataDao(connection.getSqlSession());
			
			// リポジトリ
			final SampleDataRepository repository = new SampleDataRepository();
			
			// 保持期間を過ぎたデータを取得
			final List<SampleDataDto> sampleDataList = dao.select(
					this.arguments.getRetentionPeriod(), this.arguments.getRetentionDate());
			repository.addSampleDataList(sampleDataList);
			logger.info("件数:" + repository.getSampleDataList().size());
			
			// ファイル出力（GZIP圧縮）
			final String fileName = DateTimeFormatter.ofPattern(FILE_NAME_PATTERN).format(LocalDateTime.now());
			final Path filePath = Paths.get(this.properties.getFileOutputDirectory(), fileName);
			try (OutputStream os = Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW);
					GZIPOutputStream gzip = new GZIPOutputStream(os);
					OutputStreamWriter osw = new OutputStreamWriter(gzip, StandardCharsets.UTF_8);
					BufferedWriter writer = new BufferedWriter(osw)) {
				writer.write(String.join(SEPARATOR, SampleDataDto.COLUMN_NAME) + NEWLINE);
				for (SampleDataDto dto : repository.getSampleDataList()) {
					writer.write(String.join(SEPARATOR, dto.toStringArray()) + NEWLINE);
				}
				logger.info("Javaメモリ情報:" + StringUtil.getJavaMemoryInfo());
			}
			
			// データを削除
			int deleteCount = 0;
			int dataCount = 0;
			for (List<Integer> dataIdList : repository.getPartitionDataIdList(this.properties.getDeleteUnit())) {
				dao.delete(dataIdList);
				deleteCount++;
				dataCount += dataIdList.size();
				if (deleteCount % this.properties.getCommitUnit() == 0) {
					connection.commit();
					logger.info(dataCount + "件削除しました。");
				}
			}
			if (deleteCount % this.properties.getCommitUnit() != 0) {
				connection.commit();
				logger.info(dataCount + "件削除しました。");
			}
		}
	}
}
