package jp.co.esp.batch.purge.logic;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

import jp.co.esp.batch.purge.CommandlineArguments;
import jp.co.esp.batch.purge.PurgerProperties;
import jp.co.esp.batch.purge.dao.SampleDataDao;
import jp.co.esp.batch.purge.dto.SampleDataDto;
import jp.co.esp.batch.purge.util.DBConnectionManager;

/**
 * サンプルデータ棚卸処理のテスト
 */
@PrepareForTest({ PurgeSampleDataLogic.class })
public class PurgeSampleDataLogicTest {

	static {
		PowerMockAgent.initializeIfNeeded();
	}

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	private PurgerProperties properties;
	private DBConnectionManager connection;

	@BeforeClass
	public static void beforeClass() throws Exception {
		System.setProperty("log4j.configuration", "file:./src/test/conf/log4j.xml");
	}

	@Before
	public void before() throws Exception {
		this.properties = mock(PurgerProperties.class);
		when(this.properties.getJdbcDriver()).thenReturn("Driver");
		when(this.properties.getJdbcUrl()).thenReturn("Url");
		when(this.properties.getJdbcUsername()).thenReturn("Username");
		when(this.properties.getJdbcPassword()).thenReturn("Password");
		when(this.properties.getDeleteUnit()).thenReturn(3);
		when(this.properties.getCommitUnit()).thenReturn(2);
		when(this.properties.getFileOutputDirectory()).thenReturn("./src/test/data");
		this.connection = mock(DBConnectionManager.class);
		PowerMockito.whenNew(DBConnectionManager.class).withAnyArguments().thenReturn(this.connection);
	}

	@Test
	public void test1() throws Exception {
		// サンプルデータDAOのモック
		SampleDataDao dao = mock(SampleDataDao.class);
		List<SampleDataDto> sampleDataList  = new ArrayList<>();
		for (int i = 1; i <= 8; i++) {
			SampleDataDto dto = new SampleDataDto(i, String.format("メッセージ%04d", i),
					"TESTUSER", new SimpleDateFormat("yyyyMMddHHmmss").parse("20200401000000"),
					"TESTUSER", new SimpleDateFormat("yyyyMMddHHmmss").parse("20200401235959"));
			sampleDataList.add(dto);
		}
		when(dao.select(any(), any())).thenReturn(sampleDataList);
		PowerMockito.whenNew(SampleDataDao.class).withAnyArguments().thenReturn(dao);
		
		// 現在日時
		LocalDateTime now = LocalDateTime.of(2020, 4, 10, 12, 1, 0);
		PowerMockito.spy(LocalDateTime.class);
		PowerMockito.doReturn(now).when(LocalDateTime.class);
		LocalDateTime.now();
		
		// 出力ファイル
		File outputFile = new File(this.properties.getFileOutputDirectory() + File.separator + "SampleData20200410120100.tsv.gz");
		outputFile.delete();
		
		// 実行
		String[] args = { "-period", "180" };
		CommandlineArguments commandlineArguments = new CommandlineArguments(args);
		new PurgeSampleDataLogic(commandlineArguments, this.properties).execute();
		
		// 出力ファイルの確認
		File testFile = new File(this.properties.getFileOutputDirectory() + File.separator + "test1.tsv");
		String testContent = FileUtils.readFileToString(testFile, StandardCharsets.UTF_8);
		try (GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(outputFile))) {
			String outputContent = IOUtils.toString(gzip, StandardCharsets.UTF_8);
			assertThat(outputContent, is(testContent));
		}
		
		// selectの確認
		verify(dao, times(1)).select(eq(180), eq(null));
		
		// deleteの確認
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ArgumentCaptor<List<Integer>> dataIdList = ArgumentCaptor.forClass((Class)List.class);
		verify(dao, times(3)).delete(dataIdList.capture());
		assertThat(dataIdList.getAllValues().get(0), contains(1, 2, 3));
		assertThat(dataIdList.getAllValues().get(1), contains(4, 5, 6));
		assertThat(dataIdList.getAllValues().get(2), contains(7, 8));
		
		// DB接続マネージャーの確認
		PowerMockito.verifyNew(DBConnectionManager.class, times(1)).withArguments(eq("Driver"), eq("Url"), eq("Username"), eq("Password"));
		verify(this.connection, times(2)).commit();
		verify(this.connection, times(1)).close();
	}

	@Test
	public void test2() throws Exception {
		// サンプルデータDAOのモック
		SampleDataDao dao = mock(SampleDataDao.class);
		List<SampleDataDto> sampleDataList  = new ArrayList<>();
		for (int i = 1; i <= 12; i++) {
			SampleDataDto dto = new SampleDataDto(i, String.format("メッセージ%04d", i),
					"TESTUSER", new SimpleDateFormat("yyyyMMddHHmmss").parse("20200401000000"),
					"TESTUSER", new SimpleDateFormat("yyyyMMddHHmmss").parse("20200401235959"));
			sampleDataList.add(dto);
		}
		when(dao.select(any(), any())).thenReturn(sampleDataList);
		PowerMockito.whenNew(SampleDataDao.class).withAnyArguments().thenReturn(dao);
		
		// 現在日時
		LocalDateTime now = LocalDateTime.of(2020, 4, 10, 12, 2, 0);
		PowerMockito.spy(LocalDateTime.class);
		PowerMockito.doReturn(now).when(LocalDateTime.class);
		LocalDateTime.now();
		
		// 出力ファイル
		File outputFile = new File(this.properties.getFileOutputDirectory() + File.separator + "SampleData20200410120200.tsv.gz");
		outputFile.delete();
		
		// 実行
		String[] args = { "-date", "20200101000000" };
		CommandlineArguments commandlineArguments = new CommandlineArguments(args);
		new PurgeSampleDataLogic(commandlineArguments, this.properties).execute();
		
		// 出力ファイルの確認
		File testFile = new File(this.properties.getFileOutputDirectory() + File.separator + "test2.tsv");
		String testContent = FileUtils.readFileToString(testFile, StandardCharsets.UTF_8);
		try (GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(outputFile))) {
			String outputContent = IOUtils.toString(gzip, StandardCharsets.UTF_8);
			assertThat(outputContent, is(testContent));
		}
		
		// selectの確認
		ArgumentCaptor<LocalDateTime> date = ArgumentCaptor.forClass(LocalDateTime.class);
		verify(dao, times(1)).select(eq(null), date.capture());
		assertThat(date.getValue(), is(LocalDateTime.of(2020, 1, 1, 0, 0, 0)));
		
		// deleteの確認
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ArgumentCaptor<List<Integer>> dataIdList = ArgumentCaptor.forClass((Class)List.class);
		verify(dao, times(4)).delete(dataIdList.capture());
		assertThat(dataIdList.getAllValues().get(0), contains(1, 2, 3));
		assertThat(dataIdList.getAllValues().get(1), contains(4, 5, 6));
		assertThat(dataIdList.getAllValues().get(2), contains(7, 8, 9));
		assertThat(dataIdList.getAllValues().get(3), contains(10, 11, 12));
		
		// DB接続マネージャーの確認
		PowerMockito.verifyNew(DBConnectionManager.class, times(1)).withArguments(eq("Driver"), eq("Url"), eq("Username"), eq("Password"));
		verify(this.connection, times(2)).commit();
		verify(this.connection, times(1)).close();
	}

	@Test
	public void test3() throws Exception {
		// サンプルデータDAOのモック
		SampleDataDao dao = mock(SampleDataDao.class);
		List<SampleDataDto> sampleDataList  = new ArrayList<>();
		when(dao.select(any(), any())).thenReturn(sampleDataList);
		PowerMockito.whenNew(SampleDataDao.class).withAnyArguments().thenReturn(dao);
		
		// 現在日時
		LocalDateTime now = LocalDateTime.of(2020, 4, 10, 12, 3, 0);
		PowerMockito.spy(LocalDateTime.class);
		PowerMockito.doReturn(now).when(LocalDateTime.class);
		LocalDateTime.now();
		
		// 出力ファイル
		File outputFile = new File(this.properties.getFileOutputDirectory() + File.separator + "SampleData20200410120300.tsv.gz");
		outputFile.delete();
		
		// 実行
		String[] args = { "-period", "180", "-date", "20200101000000" };
		CommandlineArguments commandlineArguments = new CommandlineArguments(args);
		new PurgeSampleDataLogic(commandlineArguments, this.properties).execute();
		
		// 出力ファイルの確認
		File testFile = new File(this.properties.getFileOutputDirectory() + File.separator + "test3.tsv");
		String testContent = FileUtils.readFileToString(testFile, StandardCharsets.UTF_8);
		try (GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(outputFile))) {
			String outputContent = IOUtils.toString(gzip, StandardCharsets.UTF_8);
			assertThat(outputContent, is(testContent));
		}
		
		// selectの確認
		ArgumentCaptor<LocalDateTime> date = ArgumentCaptor.forClass(LocalDateTime.class);
		verify(dao, times(1)).select(eq(180), date.capture());
		assertThat(date.getValue(), is(LocalDateTime.of(2020, 1, 1, 0, 0, 0)));
		
		// deleteの確認
		verify(dao, never()).delete(any());
		
		// DB接続マネージャーの確認
		PowerMockito.verifyNew(DBConnectionManager.class, times(1)).withArguments(eq("Driver"), eq("Url"), eq("Username"), eq("Password"));
		verify(this.connection, never()).commit();
		verify(this.connection, times(1)).close();
	}
}
