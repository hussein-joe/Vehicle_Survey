package com.challenges.vehicle_survey.analysis.result_write;

import com.challenges.vehicle_survey.analysis.AnalysisResultEntry;
import com.challenges.vehicle_survey.analysis.MapAnalysisResult;


public interface AnalysisResultWriter {

	<V extends AnalysisResultEntry> void write(MapAnalysisResult<V> result);
}
