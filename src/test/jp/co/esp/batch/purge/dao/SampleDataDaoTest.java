package jp.co.esp.batch.purge.dao;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.esp.batch.purge.PurgerProperties;
import jp.co.esp.batch.purge.dto.SampleDataDto;
import jp.co.esp.batch.purge.exception.DBException;
import jp.co.esp.batch.purge.util.DBConnectionManager;

/**
 * サンプルデータDAOのテスト
 */
public class SampleDataDaoTest {

	private PurgerProperties properties;
	private DBConnectionManager connection;

	@BeforeClass
	public static void beforeClass() throws Exception {
		System.setProperty("purger.configuration", "file:./src/test/conf/purger.properties");
		System.setProperty("log4j.configuration", "file:./src/test/conf/log4j.xml");
	}

	@Before
	public void before() throws Exception {
		this.properties = new PurgerProperties();
		this.connection = new DBConnectionManager(
				this.properties.getJdbcDriver(),
				this.properties.getJdbcUrl(),
				this.properties.getJdbcUsername(),
				this.properties.getJdbcPassword());
		this.deleteSampleData();
	}

	@After
	public void after() throws Exception {
		this.deleteSampleData();
		this.connection.close();
	}

	@Test
	public void test1Select() throws Exception {
		this.insertSampleData(1, "メッセージ1", "TESTUSER", LocalDateTime.now(), "TESTUSER", LocalDateTime.now());
		this.insertSampleData(2, "メッセージ2", "TESTUSER", LocalDateTime.now().minusDays(10), "TESTUSER", LocalDateTime.now().minusDays(8));
		this.insertSampleData(3, "メッセージ3", "TESTUSER", LocalDateTime.now().minusDays(10), "TESTUSER", LocalDateTime.now().minusDays(10));
		
		SampleDataDao dao = new SampleDataDao(this.connection.getSqlSession());
		{
			List<SampleDataDto> sampleDataList = dao.select(null, null);
			assertThat(sampleDataList, hasSize(3));
			{
				SampleDataDto sampleData = sampleDataList.get(0);
				assertThat(sampleData.getDataId(), is(1));
				assertThat(sampleData.getMessage(), is("メッセージ1"));
				assertThat(sampleData.getCreateUser(), is("TESTUSER"));
				assertThat(sampleData.getCreateDt(), notNullValue());
				assertThat(sampleData.getUpdateUser(), is("TESTUSER"));
				assertThat(sampleData.getUpdateDt(), notNullValue());
			}
			{
				SampleDataDto sampleData = sampleDataList.get(1);
				assertThat(sampleData.getDataId(), is(2));
				assertThat(sampleData.getMessage(), is("メッセージ2"));
				assertThat(sampleData.getCreateUser(), is("TESTUSER"));
				assertThat(sampleData.getCreateDt(), notNullValue());
				assertThat(sampleData.getUpdateUser(), is("TESTUSER"));
				assertThat(sampleData.getUpdateDt(), notNullValue());
			}
			{
				SampleDataDto sampleData = sampleDataList.get(2);
				assertThat(sampleData.getDataId(), is(3));
				assertThat(sampleData.getMessage(), is("メッセージ3"));
				assertThat(sampleData.getCreateUser(), is("TESTUSER"));
				assertThat(sampleData.getCreateDt(), notNullValue());
				assertThat(sampleData.getUpdateUser(), is("TESTUSER"));
				assertThat(sampleData.getUpdateDt(), notNullValue());
			}
		}
		{
			List<SampleDataDto> sampleDataList = dao.select(7, null);
			assertThat(sampleDataList, hasSize(2));
			{
				SampleDataDto sampleData = sampleDataList.get(0);
				assertThat(sampleData.getDataId(), is(2));
				assertThat(sampleData.getMessage(), is("メッセージ2"));
				assertThat(sampleData.getCreateUser(), is("TESTUSER"));
				assertThat(sampleData.getCreateDt(), notNullValue());
				assertThat(sampleData.getUpdateUser(), is("TESTUSER"));
				assertThat(sampleData.getUpdateDt(), notNullValue());
			}
			{
				SampleDataDto sampleData = sampleDataList.get(1);
				assertThat(sampleData.getDataId(), is(3));
				assertThat(sampleData.getMessage(), is("メッセージ3"));
				assertThat(sampleData.getCreateUser(), is("TESTUSER"));
				assertThat(sampleData.getCreateDt(), notNullValue());
				assertThat(sampleData.getUpdateUser(), is("TESTUSER"));
				assertThat(sampleData.getUpdateDt(), notNullValue());
			}
		}
		{
			List<SampleDataDto> sampleDataList = dao.select(9, null);
			assertThat(sampleDataList, hasSize(1));
			{
				SampleDataDto sampleData = sampleDataList.get(0);
				assertThat(sampleData.getDataId(), is(3));
				assertThat(sampleData.getMessage(), is("メッセージ3"));
				assertThat(sampleData.getCreateUser(), is("TESTUSER"));
				assertThat(sampleData.getCreateDt(), notNullValue());
				assertThat(sampleData.getUpdateUser(), is("TESTUSER"));
				assertThat(sampleData.getUpdateDt(), notNullValue());
			}
		}
		{
			List<SampleDataDto> sampleDataList = dao.select(11, null);
			assertThat(sampleDataList, hasSize(0));
		}
		{
			List<SampleDataDto> sampleDataList = dao.select(null, LocalDateTime.now().minusDays(7));
			assertThat(sampleDataList, hasSize(2));
			{
				SampleDataDto sampleData = sampleDataList.get(0);
				assertThat(sampleData.getDataId(), is(2));
				assertThat(sampleData.getMessage(), is("メッセージ2"));
				assertThat(sampleData.getCreateUser(), is("TESTUSER"));
				assertThat(sampleData.getCreateDt(), notNullValue());
				assertThat(sampleData.getUpdateUser(), is("TESTUSER"));
				assertThat(sampleData.getUpdateDt(), notNullValue());
			}
			{
				SampleDataDto sampleData = sampleDataList.get(1);
				assertThat(sampleData.getDataId(), is(3));
				assertThat(sampleData.getMessage(), is("メッセージ3"));
				assertThat(sampleData.getCreateUser(), is("TESTUSER"));
				assertThat(sampleData.getCreateDt(), notNullValue());
				assertThat(sampleData.getUpdateUser(), is("TESTUSER"));
				assertThat(sampleData.getUpdateDt(), notNullValue());
			}
		}
		{
			List<SampleDataDto> sampleDataList = dao.select(null, LocalDateTime.now().minusDays(9));
			assertThat(sampleDataList, hasSize(1));
			{
				SampleDataDto sampleData = sampleDataList.get(0);
				assertThat(sampleData.getDataId(), is(3));
				assertThat(sampleData.getMessage(), is("メッセージ3"));
				assertThat(sampleData.getCreateUser(), is("TESTUSER"));
				assertThat(sampleData.getCreateDt(), notNullValue());
				assertThat(sampleData.getUpdateUser(), is("TESTUSER"));
				assertThat(sampleData.getUpdateDt(), notNullValue());
			}
		}
		{
			List<SampleDataDto> sampleDataList = dao.select(null, LocalDateTime.now().minusDays(11));
			assertThat(sampleDataList, hasSize(0));
		}
		{
			List<SampleDataDto> sampleDataList = dao.select(7, LocalDateTime.now().minusDays(9));
			assertThat(sampleDataList, hasSize(1));
			{
				SampleDataDto sampleData = sampleDataList.get(0);
				assertThat(sampleData.getDataId(), is(3));
				assertThat(sampleData.getMessage(), is("メッセージ3"));
				assertThat(sampleData.getCreateUser(), is("TESTUSER"));
				assertThat(sampleData.getCreateDt(), notNullValue());
				assertThat(sampleData.getUpdateUser(), is("TESTUSER"));
				assertThat(sampleData.getUpdateDt(), notNullValue());
			}
		}
	}

	@Test
	public void test2InsertDelete() throws Exception {
		SampleDataDao dao = new SampleDataDao(this.connection.getSqlSession());
		
		// データを登録
		dao.insert(1, "メッセージ1", "TESTUSER");
		dao.insert(2, "メッセージ2", "TESTUSER");
		dao.insert(3, "メッセージ3", "TESTUSER");
		this.connection.commit();
		{
			List<Map<String, Object>> sampleDataList = this.selectSampleData();
			assertThat(sampleDataList, hasSize(3));
			{
				Map<String, Object> sampleData = sampleDataList.get(0);
				assertThat(String.valueOf(sampleData.get("DATA_ID")), is("1"));
				assertThat(sampleData.get("MESSAGE"), is("メッセージ1"));
				assertThat(sampleData.get("CREATE_USER"), is("TESTUSER"));
				assertThat(sampleData.get("CREATE_DT"), notNullValue());
				assertThat(sampleData.get("UPDATE_USER"), is("TESTUSER"));
				assertThat(sampleData.get("UPDATE_DT"), notNullValue());
			}
			{
				Map<String, Object> sampleData = sampleDataList.get(1);
				assertThat(String.valueOf(sampleData.get("DATA_ID")), is("2"));
				assertThat(sampleData.get("MESSAGE"), is("メッセージ2"));
				assertThat(sampleData.get("CREATE_USER"), is("TESTUSER"));
				assertThat(sampleData.get("CREATE_DT"), notNullValue());
				assertThat(sampleData.get("UPDATE_USER"), is("TESTUSER"));
				assertThat(sampleData.get("UPDATE_DT"), notNullValue());
			}
			{
				Map<String, Object> sampleData = sampleDataList.get(2);
				assertThat(String.valueOf(sampleData.get("DATA_ID")), is("3"));
				assertThat(sampleData.get("MESSAGE"), is("メッセージ3"));
				assertThat(sampleData.get("CREATE_USER"), is("TESTUSER"));
				assertThat(sampleData.get("CREATE_DT"), notNullValue());
				assertThat(sampleData.get("UPDATE_USER"), is("TESTUSER"));
				assertThat(sampleData.get("UPDATE_DT"), notNullValue());
			}
		}
		
		// データを削除
		dao.delete(Arrays.asList(1, 3));
		this.connection.commit();
		{
			List<Map<String, Object>> sampleDataList = this.selectSampleData();
			assertThat(sampleDataList, hasSize(1));
			{
				Map<String, Object> sampleData = sampleDataList.get(0);
				assertThat(String.valueOf(sampleData.get("DATA_ID")), is("2"));
				assertThat(sampleData.get("MESSAGE"), is("メッセージ2"));
				assertThat(sampleData.get("CREATE_USER"), is("TESTUSER"));
				assertThat(sampleData.get("CREATE_DT"), notNullValue());
				assertThat(sampleData.get("UPDATE_USER"), is("TESTUSER"));
				assertThat(sampleData.get("UPDATE_DT"), notNullValue());
			}
		}
	}

	@Test(expected = DBException.class)
	public void test3SelectException() throws Exception {
		SqlSession sqlSession = mock(SqlSession.class);
		doThrow(IllegalStateException.class).when(sqlSession).selectList(any(), any());
		SampleDataDao dao = new SampleDataDao(sqlSession);
		dao.select(10, LocalDateTime.now());
	}

	@Test(expected = DBException.class)
	public void test4DeleteException() throws Exception {
		SqlSession sqlSession = mock(SqlSession.class);
		doThrow(IllegalStateException.class).when(sqlSession).delete(any(), any());
		SampleDataDao dao = new SampleDataDao(sqlSession);
		dao.delete(Arrays.asList(1, 2, 3));
	}

	@Test(expected = DBException.class)
	public void test5InsertException() throws Exception {
		SqlSession sqlSession = mock(SqlSession.class);
		doThrow(IllegalStateException.class).when(sqlSession).insert(any(), any());
		SampleDataDao dao = new SampleDataDao(sqlSession);
		dao.insert(1, "メッセージ", "TESTUSER");
	}

	private void insertSampleData(Integer dataId, String message, String createUser, LocalDateTime createDt,
			String updateUser, LocalDateTime updateDt) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into SAMPLE_DATA");
		sql.append(" (DATA_ID, MESSAGE, CREATE_USER, CREATE_DT, UPDATE_USER, UPDATE_DT)");
		sql.append(" values(?, ?, ?, ?, ?, ?)");
		try (PreparedStatement ps =  this.connection.getSqlSession().getConnection().prepareStatement(sql.toString())) {
			ps.setInt(1, dataId);
			ps.setString(2, message);
			ps.setString(3, createUser);
			ps.setTimestamp(4, Timestamp.valueOf(createDt));
			ps.setString(5, updateUser);
			ps.setTimestamp(6, Timestamp.valueOf(updateDt));
			ps.executeUpdate();
		}
		this.connection.commit();
	}

	private void deleteSampleData() throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from SAMPLE_DATA");
		try (PreparedStatement ps =  this.connection.getSqlSession().getConnection().prepareStatement(sql.toString())) {
			ps.executeUpdate();
		}
		this.connection.commit();
	}

	private List<Map<String, Object>> selectSampleData() throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from SAMPLE_DATA");
		sql.append(" order by DATA_ID");
		try (PreparedStatement ps =  this.connection.getSqlSession().getConnection().prepareStatement(sql.toString());
				ResultSet resultSet = ps.executeQuery()) {
			ResultSetMetaData meta = resultSet.getMetaData();
			List<String> columnLabelList = new ArrayList<>();
			for (int i = 0; i < meta.getColumnCount(); i++) {
				columnLabelList.add(meta.getColumnLabel(i + 1));
			}
			List<Map<String, Object>> recordList = new ArrayList<>();
			while (resultSet.next()) {
				Map<String, Object> record = new HashMap<>();
				for (String columnLabel : columnLabelList) {
					record.put(columnLabel, resultSet.getObject(columnLabel));
				}
				recordList.add(record);
			}
			return recordList;
		}
	}
}
