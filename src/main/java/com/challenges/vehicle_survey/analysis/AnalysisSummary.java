package com.challenges.vehicle_survey.analysis;

public class AnalysisSummary<T extends AnalysisResultEntry> {

	private SummaryRecord<T> max;
	private SummaryRecord<T> min;
	
	
	public AnalysisSummary(SummaryRecord<T> max, SummaryRecord<T> min) {
		super();
		this.max = max;
		this.min = min;
	}
	public SummaryRecord<T> getMax() {
		return max;
	}


	public SummaryRecord<T> getMin() {
		return min;
	}


	public static class SummaryRecord<V extends AnalysisResultEntry> {
		
		private long recordKey;
		private V recordValue;
		public SummaryRecord(long recordKey, V recordValue) {
			super();
			this.recordKey = recordKey;
			this.recordValue = recordValue;
		}
		public long getRecordKey() {
			return recordKey;
		}
		public V getRecordValue() {
			return recordValue;
		}
	}
	
	
}
