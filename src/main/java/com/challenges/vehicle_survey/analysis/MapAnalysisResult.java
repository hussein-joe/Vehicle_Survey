package com.challenges.vehicle_survey.analysis;

import java.util.Collection;
import java.util.Map;

import com.challenges.vehicle_survey.analysis.result_write.AnalysisResultWriter;

public class MapAnalysisResult<V extends AnalysisResultEntry> implements AnalysisResult<Map<Long, V>> {

	private Map<Long, V> analysisResultMap;
	private AnalysisSummary<V> analysisSummary;
	private String resultTitle;
	
	public MapAnalysisResult(String resultTitle, Map<Long, V> analysisResult, AnalysisSummary<V> analysisSummary) {
		super();
		this.analysisResultMap = analysisResult;
		this.resultTitle = resultTitle;
		this.analysisSummary = analysisSummary;
	}
	
	@Override
	public Map<Long, V> getAnalysisResult() {
		
		return this.analysisResultMap;
	}
	
	public Collection<V> getMapEntries() {
		
		return analysisResultMap.values();
	}
	
	@Override
	public String getResultTitle() {
		
		return this.resultTitle;
	}
	@Override
	public AnalysisSummary<V> getAnalysisSummary() {
		return analysisSummary;
	}



	@Override
	public void accept(AnalysisResultWriter analysisResultWriter) {
		
		analysisResultWriter.write( this );
	}
	
	public static class AnalysisPeak<V> {
		
		private Long peakKey;
		private V peakValue;
		
		public AnalysisPeak(Long peakKey, V peakValue) {
			super();
			this.peakKey = peakKey;
			this.peakValue = peakValue;
		}
		public Long getPeakKey() {
			return peakKey;
		}
		public V getPeakValue() {
			return peakValue;
		}
	}
}
