package jp.co.esp.batch.purge.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import jp.co.esp.batch.purge.dto.SampleDataDto;

/**
 * サンプルデータリポジトリ
 */
public class SampleDataRepository {

	/** サンプルデータのリスト */
	private List<SampleDataDto> sampleDataList;

	/**
	 * コンストラクタ
	 */
	public SampleDataRepository() {
		this.sampleDataList = new ArrayList<>();
	}

	/**
	 * サンプルデータのリストを取得
	 * 
	 * @return サンプルデータのリスト
	 */
	public List<SampleDataDto> getSampleDataList() {
		return this.sampleDataList;
	}

	/**
	 * サンプルデータのリストを追加
	 * 
	 * @param sampleDataList サンプルデータのリスト
	 */
	public void addSampleDataList(List<SampleDataDto> sampleDataList) {
		this.sampleDataList.addAll(sampleDataList);
	}

	/**
	 * データIDのリストを取得
	 * 
	 * @return データIDのリスト
	 */
	public List<Integer> getDataIdList() {
		return this.getSampleDataList().stream()
			.map(sampleData -> sampleData.getDataId())
			.collect(Collectors.toList());
	}

	/**
	 * 指定したサイズで分割したデータIDのリストを取得
	 * 
	 * @param size 分割サイズ
	 * @return 指定したサイズで分割したデータIDのリスト
	 */
	public List<List<Integer>> getPartitionDataIdList(int size) {
		final AtomicInteger counter = new AtomicInteger(0);
		return new ArrayList<>(
				this.getDataIdList().stream()
					.collect(Collectors.groupingBy(
							it -> counter.getAndIncrement() / size))
					.values());
	}
}
