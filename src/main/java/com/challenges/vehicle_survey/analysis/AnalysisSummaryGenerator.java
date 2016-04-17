package com.challenges.vehicle_survey.analysis;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

import com.challenges.vehicle_survey.analysis.AnalysisSummary.SummaryRecord;

public class AnalysisSummaryGenerator {

	public <V extends AnalysisResultEntry> AnalysisSummary<V> summaryStatistics(Map<Long, V> analysisResult) {
		
		Objects.nonNull(analysisResult);
		if ( analysisResult.size() == 0 ) {
			
			return new AnalysisSummary<>(new SummaryRecord<>(0, null), 
					new SummaryRecord<>(0, null) );
		}
		
		SummaryRecord<V> summaryMaxRecord = this.getSummaryRecord(analysisResult, true);
		SummaryRecord<V> summaryMinRecord = this.getSummaryRecord(analysisResult, false);
		return new AnalysisSummary<>(summaryMaxRecord, summaryMinRecord);
	}
	
	private <V extends AnalysisResultEntry> SummaryRecord<V> getSummaryRecord(Map<Long,V> analysisResult, boolean needsMax) {
		
		Comparator<? super Map.Entry<Long,V>> valueComparator = (
	            entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue());
	    
	    Map.Entry<Long,V> requiredEntry = null;
	    if ( needsMax ) {
	    	
	    	requiredEntry = analysisResult.entrySet().stream().max(valueComparator).get();
	    } else {
	    	
	    	requiredEntry = analysisResult.entrySet().stream().min(valueComparator).get();
	    }
	    
	    return new SummaryRecord<V>(requiredEntry.getKey(), requiredEntry.getValue());
	}
}
