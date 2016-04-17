package com.challenges.vehicle_survey.analysis;


public interface AnalysisResult<T> extends AnalysisPrintable{

	T getAnalysisResult();
	
	String getResultTitle();
	AnalysisSummary<? extends AnalysisResultEntry> getAnalysisSummary();
}
